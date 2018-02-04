package com.omisoft.keepassa.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.omisoft.keepassa.MainApp;
import com.omisoft.keepassa.dao.CompanyDAO;
import com.omisoft.keepassa.dao.DepartmentDAO;
import com.omisoft.keepassa.dao.GroupDAO;
import com.omisoft.keepassa.dao.HistoryDAO;
import com.omisoft.keepassa.dao.PasswordSafeDAO;
import com.omisoft.keepassa.dao.RoleDAO;
import com.omisoft.keepassa.dao.SettingsDAO;
import com.omisoft.keepassa.dao.TrustStoreDAO;
import com.omisoft.keepassa.dao.UserDAO;
import com.omisoft.keepassa.dao.UserWithAesDAO;
import com.omisoft.keepassa.listeners.StartupManager;
import com.omisoft.keepassa.structures.SecureKeystore;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import java.io.File;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

/**
 * Holds dependencies that are not exposed as web
 * Created by leozhekov on 10/28/16.
 */
@Slf4j
public class DependencyModule extends AbstractModule {

  protected void configure() {
    bind(UserDAO.class);
    bind(PasswordSafeDAO.class);
    bind(GroupDAO.class);
    bind(SettingsDAO.class);
    bind(DepartmentDAO.class);
    bind(TrustStoreDAO.class);
    bind(RoleDAO.class);
    bind(UserWithAesDAO.class);
    bind(HistoryDAO.class);
    bind(CompanyDAO.class);
    bind(StartupManager.class).asEagerSingleton();
    requestStaticInjection(SecureKeystore.class);


  }

  @Provides
  @Singleton
  public Configuration freeMarkerProvider() {
    try {
      Configuration conf = new Configuration(Configuration.VERSION_2_3_23);
      URL directoryPath = MainApp.class.getResource("/");

      if (directoryPath != null) {

        File directory = new File(directoryPath.toURI());
        FileTemplateLoader ftl = new FileTemplateLoader(directory);
        conf.setTemplateLoader(ftl);
      } else {
        ClassTemplateLoader ctl = new ClassTemplateLoader(getClass(), "/");
        conf.setTemplateLoader(ctl);
      }
      conf.setDefaultEncoding("UTF-8");
      conf.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
      return conf;
    } catch (Exception e) {
      log.error("ERROR WITH FREEMARKER:", e);
      return null;
    }

  }

  @Provides
  @com.google.inject.Singleton
  public Executor createThreadPoolExecutor() {
    BlockingQueue<Runnable> queue = new LinkedBlockingDeque<>();
    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(20, 100, 2, TimeUnit.MINUTES,
        queue);
    return threadPoolExecutor;
  }

}
