package com.omisoft.keepassa.listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class SessionCookieListener implements ServletContextListener {

  public void contextInitialized(ServletContextEvent sce) {
//    String comment = "Keepassa session cookie";
//    // String domain = "foo.com";
//    // String path = "/my/special/path";
//    boolean isSecure = true;
//    boolean httpOnly = false;
//    int maxAge = 30000;
//    String cookieName = "xsessionid";
//
//
//    SessionCookieConfig scf = sce.getServletContext().getSessionCookieConfig();
//
//    scf.setComment(comment);
//    scf.setHttpOnly(httpOnly);
//    scf.setMaxAge(maxAge);
//    scf.setSecure(isSecure);
//    scf.setName(cookieName);
  }

  public void contextDestroyed(ServletContextEvent sce) {

  }
}
