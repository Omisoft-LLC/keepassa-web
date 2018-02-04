package com.omisoft.keepassa.dto.rest;

import lombok.Data;

/**
 * Created by leozhekov on 2/8/17.
 */
@Data
public class LoggedUserDTO {

  private String email;
  private String publicKey;
  private Boolean twoF;
  private String token;

  public LoggedUserDTO(String email, String publicKey, Boolean isTwoFEnabled) {
    this.email = email;
    this.publicKey = publicKey;
    this.twoF = isTwoFEnabled;
  }

  public LoggedUserDTO() {
  }
}
