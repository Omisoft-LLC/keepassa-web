package com.omisoft.keepassa.interceptors;

import com.omisoft.keepassa.dao.UserDAO;
import com.omisoft.keepassa.endpoints.BaseEndpoint;
import com.omisoft.keepassa.entities.users.User;
import com.omisoft.keepassa.exceptions.NotFoundException;
import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by dido on 15.03.17.
 */
@AdminRequest
@Slf4j
public class AdminRequestInterceptor implements ContainerRequestFilter, BaseEndpoint {

  @Inject
  UserDAO userDAO;
  @Context
  private HttpServletRequest request;

  @Override
  public void filter(ContainerRequestContext containerRequestContext) throws IOException {
    log.info("IN ADMIN REQUEST FILTER");
    try {
      User adminUser = getDbUser(request, userDAO);
      String path = containerRequestContext.getUriInfo().getPath();
      log.info("PATH:" + path);
    } catch (NotFoundException e) {
      e.printStackTrace();
    }
  }
}
