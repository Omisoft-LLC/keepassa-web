package com.omisoft.keepassa.dto.rest;

import lombok.Data;

/**
 * Created by nslavov on 1/18/17.
 */
@Data
public class LoginDTO {

  private String email;

  private byte[] avatar;

//  private String verify;

  public LoginDTO() {
  }

  public LoginDTO(String email, byte[] avatar) {
    this.email = email;
    this.avatar = avatar;
  }


}
