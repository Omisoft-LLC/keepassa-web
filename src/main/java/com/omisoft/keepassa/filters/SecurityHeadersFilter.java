package com.omisoft.keepassa.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * Standard CORS filter, and security headers
 *
 * @author dido
 */
@javax.inject.Singleton
public class SecurityHeadersFilter implements Filter {

  public SecurityHeadersFilter() {
  }

  public void init(FilterConfig fConfig) throws ServletException {
  }

  /**
   * Executes filter
   */
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletResponse httpResponse = (HttpServletResponse) response;
    httpResponse.addHeader("Access-Control-Allow-Origin", "*");
    httpResponse.addHeader("Access-Control-Allow-Headers",
        "Origin, X-Requested-With, Content-Type, Accept, Authorization");
    httpResponse.addHeader("Access-Control-Expose-Headers", "Authorization");
    httpResponse.addHeader("Access-Control-Allow-Credentials", "true");
    // Deny iframing (Disable due to iframe download)
//    httpResponse.addHeader("X-Frame-Options", "DENY");
    // Overwrite hsts header to include preload
    httpResponse
        .setHeader("Strict-Transport-Security", "max-age=63072000; includeSubDomains; preload");
    httpResponse.addHeader("X-Content-Type-Options", "nosniff");
    httpResponse.addHeader("X-XSS-Protection", "1");
    chain.doFilter(request, response);
  }

  public void destroy() {
  }
}
