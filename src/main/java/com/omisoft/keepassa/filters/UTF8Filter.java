package com.omisoft.keepassa.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Standard UTF-8 filter
 *
 * @author dido
 */
@javax.inject.Singleton
public class UTF8Filter implements Filter {

  /**
   * The default character encoding to set for requests that pass through this filter.
   */
  protected String mEncoding = null;

  /**
   * The filter configuration object we are associated with. If this value is null, this filter
   * instance is not currently configured.
   */
  protected FilterConfig mFilterConfig = null;

  /**
   * Place this filter into service.
   *
   * @param aFilterConfig The filter configuration object
   */
  public void init(FilterConfig aFilterConfig) throws ServletException {
    mFilterConfig = aFilterConfig;
    mEncoding = "UTF-8";

  }

  /**
   * Select and set (if specified) the character encoding to be used to interpret request parameters
   * for this request.
   *
   * @param aRequest The servlet request we are processing
   * @param aResult The servlet response we are creating
   * @param aChain The filter chain we are processing
   * @throws IOException if an input/output error occurs
   * @throws ServletException if a servlet error occurs
   */
  public void doFilter(ServletRequest aRequest, ServletResponse aResponse, FilterChain aChain)
      throws IOException, ServletException {

    aRequest.setCharacterEncoding(mEncoding);
    aResponse.setCharacterEncoding(mEncoding);

    // Pass control on to the next filter
    aChain.doFilter(aRequest, aResponse);
  }

  /**
   * Take this filter out of service.
   */
  public void destroy() {
    mEncoding = null;
    mFilterConfig = null;
  }


}
