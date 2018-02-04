package com.omisoft.keepassa.servlets;

import com.google.inject.Singleton;
import com.omisoft.keepassa.MainApp;
import com.omisoft.keepassa.utils.Utils;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by dido on 11/23/16.
 */
@Slf4j
@Singleton
public class WebJarsServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    log.info("SERVING WEBJAR");
    log.info(req.getPathInfo());
    String fullPath = "/META-INF/resources/webjars" + req.getPathInfo();
    log.info("FULL PATH:" + fullPath);
    InputStream input = MainApp.class.getResourceAsStream(fullPath);
    if (input == null) {
      log.info("NULL");
    } else {
      resp.setContentType("application/javascript");

      Utils.stream(input, resp.getOutputStream());
    }
    resp.flushBuffer();

  }
}
