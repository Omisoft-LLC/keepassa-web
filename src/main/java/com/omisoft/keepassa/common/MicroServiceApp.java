package com.omisoft.keepassa.common;

import static com.omisoft.keepassa.constants.Constants.LOGS_DIR;

import com.omisoft.keepassa.configuration.FileConfigService;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Abstract class to init microservices Created by dido on 16.06.16.
 */
@Slf4j
public abstract class MicroServiceApp {

  private static final String SERVICE_TYPE = "_http._tcp.local.";
  private static MicroServiceApp INSTANCE;
  public boolean isAlive = false;
  private String serviceName;
  private Server server;
  private boolean test;

  public static final MicroServiceApp getInstance() {
    return INSTANCE;
  }

  public abstract void preSetup();

  public void start(String name) {
    start(name, false);
  }

  public abstract ServletContextHandler initWebsocket(Server server);

  public void start(String name, boolean testing, DependenciesEnum... dependecies) {
    createLogDir();
    INSTANCE = this;
    long begin = System.currentTimeMillis();
    this.test = testing;
    log.info("STARTING MICROSERVICE:" + name);
    serviceName = name;
    registerGlobalErrorHandler();
    try {
      waitForServices(dependecies);
      preSetup();
      initJetty();
      addShutdownHook();
      isAlive = true;
      log.info(
          "SUCCESS STARTING MICROSERVICE:" + name + " FOR " + (System.currentTimeMillis() - begin)
              + " ms.");
      if (!test) {
        server.join();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Shutdown hook, handles System.exit and kill -15
   */
  private void addShutdownHook() {
    Thread shutdownMonitor = new ShutdownMonitor(server);
    shutdownMonitor.start();
    isAlive = false;
    final MicroServiceApp instance = this;

    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        try {

          instance.stop();
          log.info("SERVICE " + serviceName + " STOPPED!!!");
        } catch (Exception e) {
          log.error("Exception during server stop in shutdown hook", e);
        }
      }
    });
  }

  private void registerGlobalErrorHandler() {
    Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
      public void uncaughtException(Thread t, Throwable e) {
        log.error("UNHANDLED ERROR IN THREAD:" + t.getName());
        log.error("UNHANDLED ERROR!!!", e);

      }
    });
  }

  public abstract void configureHttps(Server server, HttpConfiguration httpConfiguration);

  public abstract void configureHttp(Server server, HttpConfiguration httpConfiguration);

  private void addJmxSupport(Server server) {
    MBeanContainer mbContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
    server.addBean(mbContainer);
  }

  public abstract HttpConfiguration addHttpConfiguration();


  public abstract WebAppContext initWebApp(Server server);

  public void initJetty() throws Exception {
    ContextHandlerCollection contexts = new ContextHandlerCollection();

    // Setup Threadpool
    QueuedThreadPool threadPool = new QueuedThreadPool();
    threadPool.setMinThreads(8);
    threadPool.setMaxThreads(1024);
    Server server = new Server(threadPool);
    HttpConfiguration httpConfiguration = addHttpConfiguration();
    configureHttp(server, httpConfiguration);
//    configureHttps(server, httpConfiguration);
    addJmxSupport(server);
    WebAppContext webAppContext = initWebApp(server);
    ServletContextHandler wsContextHandler = initWebsocket(server);

    this.server = server;

    // Set Form Limits
    server.setAttribute("org.eclipse.jetty.server.Request.maxFormContentSize", 10000);
    server.setAttribute("org.eclipse.jetty.server.Request.maxFormKeys", 200);

    MetricsService.initMetrics(Thread.currentThread().getStackTrace()[1].getClassName());

    // Start services
    log.info("STARTING SERVICES");

    contexts.setHandlers(new Handler[]{wsContextHandler, webAppContext});
    server.setHandler(contexts);

    log.info("STARTING SERVER");

    server.start();
    log.info("SERVER IS IN MODE:" + FileConfigService.getInstance().getConfig().getMode());
    log.info("SERVER IS STARTED!!!");
    server.join();

  }


  private void createLogDir() {
    // todo hardcoded filename/path according to findbugs. Do something maybe.
    File logDir = new File(LOGS_DIR);
    // todo RV_RETURN_VALUE_IGNORED_BAD_PRACTICE - findbugs???
    boolean madeDirs = logDir.mkdirs();
  }


  /**
   * Wait for services that are dependencies of the current service
   */
  private void waitForServices(DependenciesEnum[] dependencies) {
    if (dependencies != null) {
      for (DependenciesEnum d : dependencies) {

        try {
          InetUtils.checkAvailabilityAndBlock(d.getHost(), d.getPort());
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }

    }
  }


  /**
   * Stop server. Code is executed by shutdown hook
   */
  protected void stop() {
    try {
      log.info("Exiting service:" + serviceName);
      server.stop();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Generic catch all error handler
   */
  public static class ErrorHandler extends ErrorPageErrorHandler {

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request,
        HttpServletResponse response) throws IOException {
      response.getWriter()
          .append("{\"status\":\"GENERIC ERROR\",\"message\":\"HTTP ")
          .append(String.valueOf(response.getStatus()))
          .append("\"}");
    }
  }
}


