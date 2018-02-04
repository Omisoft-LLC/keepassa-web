package com.omisoft.keepassa.filters;

import static com.omisoft.keepassa.constants.Constants.AUTHORIZATION_HEADER;
import static com.omisoft.keepassa.constants.Constants.USER_REDIS_DTO;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.omisoft.keepassa.authority.UserAuthority;
import com.omisoft.keepassa.dto.LoggedUserInfo;
import com.omisoft.keepassa.utils.AuthUtils;
import java.io.IOException;
import java.text.ParseException;
import javax.inject.Inject;
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
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

/**
 * Created by nslavov on 3/25/16.
 */
@Singleton
@Slf4j
public class AuthorityFilter implements Filter {


  @Inject
  private UserAuthority authority;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {

  }

  // TODO Refactor
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;
    String authHeader = httpRequest.getHeader(AUTHORIZATION_HEADER);
    log.info(authHeader);
    String method = httpRequest.getMethod();
    Cookie[] cokkies = httpRequest.getCookies();
    Cookie authCookie;
    if (StringUtils.isBlank(authHeader) || authHeader.equals("null")) {
      for (Cookie c : cokkies) {
        if (AUTHORIZATION_HEADER.equalsIgnoreCase(c.getName())) {
          authCookie = c;
          authHeader = authCookie.getValue();
          break;
        }
      }
    }

    if (method.equals("OPTIONS")) {
      httpResponse.setStatus(200);
    } else {
      if ((StringUtils.isBlank(authHeader) || !authority.isExist(authHeader))) {
        httpResponse.setStatus(401);
      } else {
        JWTClaimsSet claimSet;
        try {
          claimSet = (JWTClaimsSet) AuthUtils.decodeToken(authHeader);
        } catch (ParseException | JOSEException e) {
          httpResponse.setStatus(401);
          return;
        }
        // ensure that the token is not expired
        if (new DateTime(claimSet.getExpirationTime()).isBefore(DateTime.now())) {
          httpResponse.setStatus(401);
        } else {
          LoggedUserInfo redisDTO = authority.getUser(authHeader);
          request.setAttribute(USER_REDIS_DTO, redisDTO);
          filterChain.doFilter(request, response);
        }
      }
    }

  }

  @Override
  public void destroy() {

  }
}

// byte[] base64decodedBytes = Base64.getDecoder().decode("ZW1wdHlfdG5AYWJ2LmJnOmFzZGFzZA==");
// logger.info("Original String: " + new String(base64decodedBytes, "utf-8"));
