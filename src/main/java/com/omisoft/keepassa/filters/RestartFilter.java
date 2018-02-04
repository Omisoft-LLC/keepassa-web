package com.omisoft.keepassa.filters;

import com.omisoft.keepassa.configuration.Configuration;
import com.omisoft.keepassa.configuration.FileConfigService;
import com.omisoft.keepassa.constants.Constants;
import java.io.IOException;
import javax.inject.Singleton;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by nslavov on 3/25/16.
 * Filter check for first time setup
 */
@Singleton
@Slf4j
public class RestartFilter extends HttpFilter {

  private final Configuration config = FileConfigService.getInstance().getConfig();

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {

  }

  @Override
  public void destroy() {

  }

  @Override
  public void doFilter(HttpServletRequest request, HttpServletResponse response,
      HttpSession session, FilterChain filterChain)
      throws IOException, ServletException {
    log.info("FILTERR");
    log.info(String.valueOf(config.getInit_done()));
//    log.info(String.valueOf(request.getServletContext().getAttribute(Constants.SETUP)));

    if (!config.getInit_done()
        && request.getServletContext().getAttribute(Constants.SETUP) == null) {
//      response.sendRedirect("http://localhost/setup");
    }
    filterChain.doFilter(request, response);
  }
}

// byte[] base64decodedBytes = Base64.getDecoder().decode("ZW1wdHlfdG5AYWJ2LmJnOmFzZGFzZA==");
// logger.info("Original String: " + new String(base64decodedBytes, "utf-8"));
