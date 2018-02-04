package com.omisoft.keepassa.listeners;

import com.omisoft.keepassa.constants.RestUrl;
import com.omisoft.keepassa.endpoints.PasswordSafeEndpoint;
import io.swagger.config.ScannerFactory;
import io.swagger.jaxrs.config.BeanConfig;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class SwaggerServletContextListener implements
    ServletContextListener {

  SwaggerServletContextListener() {
  }

  @Override
  public void contextInitialized(ServletContextEvent event) {

    BeanConfig beanConfig = getBeanConfig();
    event.getServletContext().setAttribute("reader",
        beanConfig);
    event.getServletContext().setAttribute("swagger",
        beanConfig.getSwagger());
    event.getServletContext().setAttribute("scanner",
        ScannerFactory.getScanner());
  }

  @Override
  public void contextDestroyed(ServletContextEvent event) {
  }

  private BeanConfig getBeanConfig() {
    log.info("ADDING SWAGGER");
    BeanConfig beanConfig = new BeanConfig();
    beanConfig.setTitle("Keepassa Web API");
    beanConfig.setDescription("REST API for Keepassa");
    beanConfig.setVersion("1.0.2");
    beanConfig.setLicense("Commercial");
    beanConfig.setSchemes(new String[]{"https"});
    beanConfig.setHost("keepassa.omisoft.eu");
    beanConfig.setBasePath(RestUrl.REST);
    beanConfig.setPrettyPrint(true);
    // setScan() must be called last
    beanConfig.setResourcePackage(
        PasswordSafeEndpoint.class.getPackage().getName());
    beanConfig.setScan(true);

    return beanConfig;
  }
}