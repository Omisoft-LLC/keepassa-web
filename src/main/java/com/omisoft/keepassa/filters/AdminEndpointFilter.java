package com.omisoft.keepassa.filters;

import static com.omisoft.keepassa.constants.Constants.USER_REDIS_DTO;

import com.google.inject.Singleton;
import com.omisoft.keepassa.dao.CompanyDAO;
import com.omisoft.keepassa.dao.UserDAO;
import com.omisoft.keepassa.dto.LoggedUserInfo;
import com.omisoft.keepassa.entities.users.User;
import com.omisoft.server.common.exceptions.DataBaseException;
import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * Bind this filter to admin functionality endpoint
 * Created by dido on 15.03.17.
 */
@Singleton
@Slf4j
public class AdminEndpointFilter extends HttpFilter {

  @Inject
  private UserDAO userDAO;

  @Inject
  private CompanyDAO companyDao;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {

  }

  @Override
  public void destroy() {

  }

  @Override
  public void doFilter(HttpServletRequest request, HttpServletResponse response,
      HttpSession session, FilterChain chain) throws ServletException, IOException {
    LoggedUserInfo loggedUserInfo = (LoggedUserInfo) request.getAttribute(USER_REDIS_DTO);
    User user = userDAO.findUserByEmailWithNull(loggedUserInfo.getEmail());
    try {
      if (companyDao.isUserAdmin(user)) {
        log.info("USER IS ADMIN");
      } else {
        response.setStatus(401);
        return;
      }
    } catch (DataBaseException e) {
      response.setStatus(401);

    }
    chain.doFilter(request, response);
  }
}
