package com.omisoft.keepassa.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by nslavov on 5/10/16.
 */
public class Token {

  String token;

  public Token(@JsonProperty("token") String token) {
    this.token = token;
  }

  public String getToken() {
    return token;
  }
}
