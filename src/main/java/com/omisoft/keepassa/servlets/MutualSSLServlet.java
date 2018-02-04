package com.omisoft.keepassa.servlets;

import com.omisoft.keepassa.authority.UserAuthority;
import com.omisoft.keepassa.constants.Constants;
import com.omisoft.keepassa.dao.TrustStoreDAO;
import com.omisoft.keepassa.dao.UserDAO;
import com.omisoft.keepassa.dto.LoggedUserInfo;
import com.omisoft.keepassa.entities.users.User;
import com.omisoft.keepassa.utils.CertAndKeyUtils;
import com.omisoft.keepassa.utils.TimeBasedOneTimePasswordUtil;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

/**
 * Mutual SSL Filter
 * Enabled when we are in hosted mode
 * Created by dido on 18.01.17.
 */
@Slf4j
@javax.inject.Singleton
public class MutualSSLServlet extends HttpServlet {

  @Inject
  private TrustStoreDAO trustStoreDAO;

  @Inject
  private UserDAO userDAO;
  @Inject
  private UserAuthority authority;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String certEncoded = request.getHeader("X-SSL-ClientCert-Base64");
    log.info(certEncoded);

    // which encoding
    X509Certificate certificate = CertAndKeyUtils
        .getCertificateFromBytes(Base64.decodeBase64(certEncoded));
    String result;
    if (trustStoreDAO.checkIfCertificateIsValid(request, certificate)) {
      LoggedUserInfo loggedUserInfo = authority
          .getUser(request.getHeader(Constants.AUTHORIZATION_HEADER));
      User user = userDAO.findUserByEmailWithNull(loggedUserInfo.getEmail());
      try {
        String currentNumber = TimeBasedOneTimePasswordUtil
            .generateCurrentNumber(user.getMutualSslOTPKey());
        response.getWriter().print(currentNumber);
      } catch (GeneralSecurityException e) {
        e.printStackTrace();
      }
    } else {
      authority.getUser(request.getHeader(Constants.AUTHORIZATION_HEADER));
      authority.removeUser(request.getHeader(Constants.AUTHORIZATION_HEADER));
    }


  }


}