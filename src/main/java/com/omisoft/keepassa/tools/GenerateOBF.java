package com.omisoft.keepassa.tools;

/**
 * Call jetty function
 *
 * Usage - java org.eclipse.jetty.util.security.Password [<user>] <password> Created by dido on
 * 12.12.16.
 */
public class GenerateOBF {

  public static void main(String[] args) {
    org.eclipse.jetty.util.security.Password.main(args);
  }
}
