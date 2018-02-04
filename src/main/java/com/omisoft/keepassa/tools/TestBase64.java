package com.omisoft.keepassa.tools;

import org.apache.commons.codec.binary.Base64;

/**
 * Created by dido on 06.03.17.
 */
public class TestBase64 {

  public static void main(String[] args) {
    String vase = "hD5vGa5n2LA7/1+dKp+gfddXkmnT7GZLmM8oousxlYiQQtNGJ+GrIadGRN5ivIT2NSxfwZDNyPPi 43SLmVhRtjBs6+uxDzkhHrStiboiIwrEUpy14/QnVDrBVc3Bm7jZrn1P+AuoD3BCVldvqaVrWSB4 FABJ76+80cDUAlqBldftghRw+oe/H8RvXOg5waVk7E9SxCYrHjtq6SeoTa66r64j193PWA1q+LQM fWQ+jMwJ0V9IXdXRUph3K79vhf/zl6d8y0QMVf2W8MJtWgxkW3qfAx+NXwe8eFF5zEAgWILKHGR+ JXQCfLawHvmTPebsgDxz7PJ2H1ixyuVB8qTOOg==";
    byte[] a = Base64.decodeBase64(vase);
    System.out.println(a.length);
  }
}
