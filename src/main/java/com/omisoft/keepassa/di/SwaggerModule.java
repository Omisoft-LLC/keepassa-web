package com.omisoft.keepassa.di;

import com.google.inject.servlet.ServletModule;
import com.omisoft.keepassa.filters.ApiOriginFilter;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by dido on 21.02.17.
 */
@Slf4j
public class SwaggerModule extends ServletModule {

  private final String path;

  public SwaggerModule() {
    this.path = null;
  }

  public SwaggerModule(final String path) {
    this.path = path;  // e.g., "/api"
  }


  @Override
  protected void configureServlets() {

    bind(ApiListingResource.class);
    bind(SwaggerSerializers.class);

    if (path == null) {
      filter("/*").through(ApiOriginFilter.class);
    } else {
      filter(path + "/*").through(ApiOriginFilter.class);
    }
  }
}