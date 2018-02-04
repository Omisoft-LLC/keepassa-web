package com.omisoft.keepassa.filters;

import com.google.inject.Singleton;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Redirect to app page if mobile browser is detected
 * Created by dido on 02.03.17.
 */
@Singleton
@Slf4j
public class MobileAppRedirectFilter extends HttpFilter {

  private static final String ANDROID_UA = "android";

  @Override
  public void doFilter(HttpServletRequest request, HttpServletResponse response,
      HttpSession session, FilterChain chain) throws ServletException, IOException {
    log.info("IN APP DETECTOR");
    String ua = request.getHeader("User-Agent");

    if (StringUtils.isNotEmpty(ua) && ua.toLowerCase().contains(ANDROID_UA)) {
      response.sendRedirect("/openapp");
    } else {
      chain.doFilter(request, response);
    }
  }
}
