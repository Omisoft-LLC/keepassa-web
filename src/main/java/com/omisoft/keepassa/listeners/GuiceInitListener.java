package com.omisoft.keepassa.listeners;

import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.omisoft.keepassa.constants.RestUrl;
import com.omisoft.keepassa.di.ApplicationServletModule;
import com.omisoft.keepassa.di.DbModule;
import com.omisoft.keepassa.di.DependencyModule;
import com.omisoft.keepassa.di.RestModule;
import com.omisoft.keepassa.di.SwaggerModule;
import com.omisoft.keepassa.utils.InjectorHolder;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.plugins.guice.GuiceResteasyBootstrapServletContextListener;
import org.jboss.resteasy.plugins.guice.ext.JaxrsModule;

/**
 * Created by dido on 11/23/16.
 */
@Slf4j
public class GuiceInitListener extends GuiceResteasyBootstrapServletContextListener {

  @Override
  protected void withInjector(Injector injector) {
    InjectorHolder.setInjector(injector);
  }

  @Override
  protected Stage getStage(ServletContext servletContext) {

    return Stage.PRODUCTION;

  }

  /**
   * Override this method to instantiate your {@link com.google.inject.Module}s yourself.
   */
  @Override
  protected List<? extends Module> getModules(final ServletContext context) {
    final List<Module> result = new ArrayList<>();
    result.add(new DbModule());
    result.add(new DependencyModule());
    result.add(new RestModule());
    result.add(new ApplicationServletModule());
    result.add(new SwaggerModule(RestUrl.REST));
    result.add(new JaxrsModule());

    return result;

  }

}
