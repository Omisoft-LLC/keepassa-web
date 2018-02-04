package com.omisoft.keepassa.di;

import com.google.inject.AbstractModule;
import com.omisoft.keepassa.endpoints.AccountEndpoint;
import com.omisoft.keepassa.endpoints.AdminEndpoint;
import com.omisoft.keepassa.endpoints.AuditEndpoint;
import com.omisoft.keepassa.endpoints.Feedback;
import com.omisoft.keepassa.endpoints.GroupsEndpoint;
import com.omisoft.keepassa.endpoints.ImportEndpoint;
import com.omisoft.keepassa.endpoints.InitEndpoint;
import com.omisoft.keepassa.endpoints.MessagingEndpoint;
import com.omisoft.keepassa.endpoints.PasswordSafeEndpoint;
import com.omisoft.keepassa.endpoints.ProfileEndpoint;
import com.omisoft.keepassa.endpoints.SettingsEndpoint;
import com.omisoft.keepassa.interceptors.AdminRequestInterceptor;
import com.omisoft.keepassa.providers.DataBaseExceptionProvider;
import com.omisoft.keepassa.providers.GenericExceptionProvider;
import com.omisoft.keepassa.providers.NotFoundExceptionProvider;
import com.omisoft.keepassa.providers.SecurityExceptionProvider;
import lombok.extern.slf4j.Slf4j;

/**
 * All redis resources should be described here
 * Created by dido on 12/6/16.
 */
@Slf4j
public class RestModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(AccountEndpoint.class);
    bind(Feedback.class);
    bind(GroupsEndpoint.class);
    bind(InitEndpoint.class);
    bind(PasswordSafeEndpoint.class);
    bind(ProfileEndpoint.class);
    bind(MessagingEndpoint.class);
    bind(AdminEndpoint.class);
    bind(ImportEndpoint.class);
    bind(AuditEndpoint.class);
    bind(SettingsEndpoint.class);
    bind(AdminRequestInterceptor.class);
    bind(DataBaseExceptionProvider.class);
    bind(SecurityExceptionProvider.class);
    bind(GenericExceptionProvider.class);
    bind(NotFoundExceptionProvider.class);

    log.info("INITING REST SERVICES");
  }
}
