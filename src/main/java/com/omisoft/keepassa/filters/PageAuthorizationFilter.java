package com.omisoft.keepassa.filters;

import com.omisoft.keepassa.authority.UserAuthority;
import com.omisoft.keepassa.constants.Constants;
import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Used by web pages and resources
 * Created by dido on 9/26/16.
 */
@Slf4j
@javax.inject.Singleton
public class PageAuthorizationFilter extends HttpFilter {

  @Inject
  private UserAuthority authority;

  @Override
  public void destroy() {

  }

  @Override
  public void doFilter(HttpServletRequest request, HttpServletResponse response,
      HttpSession session, FilterChain chain)
      throws IOException, ServletException {
    boolean loggedIn = false; // TODO FIX
    if (StringUtils.isNotEmpty(request.getHeader(Constants.AUTHORIZATION_HEADER))) {
      loggedIn = true;
    }
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (Constants.AUTHORIZATION_HEADER.equals(cookie.getName()) && authority
            .isExist(cookie.getValue())) {
          loggedIn = true;

        }
      }
    }
    String loginURL = request.getContextPath() + "/login.html";

    if (loggedIn) {
      response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
      response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
      response.setDateHeader("Expires", 0); // Proxies.

      log.info("FILTER AUTHORITY OK");
      chain.doFilter(request, response); // So, just continue request.
    } else {
      log.info("FILTER AUTHORITY REDIRECT");

      response.sendRedirect(loginURL); // So, just perform standard synchronous redirect.
    }
  }
}
