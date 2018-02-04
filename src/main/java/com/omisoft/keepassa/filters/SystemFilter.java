package com.omisoft.keepassa.filters;

import static com.omisoft.keepassa.constants.Constants.SYSTEM_MOD;

import com.omisoft.keepassa.configuration.Configuration;
import com.omisoft.keepassa.configuration.FileConfigService;
import java.io.IOException;
import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by nslavov on 1/11/17.
 */
@Singleton
@Slf4j
public class SystemFilter implements Filter {

  Configuration configuration = FileConfigService.getInstance().getConfig();

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    log.info("SYSTEM FILTER - STARTED");
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
      FilterChain filterChain) throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
    HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
    Cookie[] cokkies = httpRequest.getCookies();
    // if (cokkies!=null) {
    // for (Cookie c : cokkies) {
    // if (SYSTEM_MOD.equals(c.getName()) && configuration.getMode().equals(c.getValue())) {
    // break;
    // } else {
    Cookie cookie = new Cookie(SYSTEM_MOD, configuration.getMode());
    cookie.setPath("/");
    cookie.setDomain("");
    cookie.setMaxAge(-1);
    cookie.setSecure(false);
    cookie.setHttpOnly(false);
    httpResponse.addCookie(cookie);
    filterChain.doFilter(servletRequest, servletResponse);


  }

  @Override
  public void destroy() {

  }
}
