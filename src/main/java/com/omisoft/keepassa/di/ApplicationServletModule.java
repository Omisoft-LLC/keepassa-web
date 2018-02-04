package com.omisoft.keepassa.di;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.Scopes;
import com.google.inject.servlet.ServletModule;
import com.omisoft.keepassa.configuration.FileConfigService;
import com.omisoft.keepassa.constants.Constants;
import com.omisoft.keepassa.constants.RestUrl;
import com.omisoft.keepassa.filters.AdminEndpointFilter;
import com.omisoft.keepassa.filters.GZipServletFilter;
import com.omisoft.keepassa.filters.PageAuthorizationFilter;
import com.omisoft.keepassa.filters.RestartFilter;
import com.omisoft.keepassa.filters.SecurityHeadersFilter;
import com.omisoft.keepassa.filters.SystemFilter;
import com.omisoft.keepassa.servlets.MutualSSLServlet;
import com.omisoft.keepassa.servlets.WebJarsServlet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

/**
 * Created by leozhekov on 10/28/16.
 */
public class ApplicationServletModule extends ServletModule {

  @Override
  protected void configureServlets() {
    // filter("/*").through(com.omisoft.keepassa.filters.LoggingFilter.class);

    bind(MessageBodyReader.class).to(JacksonJsonProvider.class);
    bind(MessageBodyWriter.class).to(JacksonJsonProvider.class);
    bind(org.jboss.resteasy.plugins.server.servlet.FilterDispatcher.class)
        .in(Scopes.SINGLETON); // Workaround
    // for
    // third
    // party
    // servlets
    filter("/*").through(SecurityHeadersFilter.class);
    serve("/mutualSSL").with(MutualSSLServlet.class);
    filter("/*").through(GZipServletFilter.class);
    filter("/*").through(SystemFilter.class);
    filter(RestUrl.REST + RestUrl.SECURE + "/*")
        .through(com.omisoft.keepassa.filters.AuthorityFilter.class);
    filter("/secure/*").through(PageAuthorizationFilter.class);
    filter(RestUrl.REST + RestUrl.SECURE + RestUrl.ADMIN + "/*").through(AdminEndpointFilter.class);
//    filter("/").through(MobileAppRedirectFilter.class);
    if (Constants.HOSTED_MODE.equals(FileConfigService.getInstance().getConfig().getMode())) {

      // todo maybe???
      List<String> initUrls = new ArrayList<>();
      initUrls.add("/secure/*");
      initUrls.add("/rest/secure/*");
      initUrls.add("/login.html");
      initUrls.add("/");
      filter("/rest/init/*").through(RestartFilter.class);

    }

    Map<String, String> options = new HashMap<>();
    options.put("resteasy.servlet.mapping.prefix", RestUrl.REST);

    filter("/rest/*").through(org.jboss.resteasy.plugins.server.servlet.FilterDispatcher.class,
        options); // Workaround for third party servlets
    serve("/webjars/*").with(WebJarsServlet.class);
  }

}
