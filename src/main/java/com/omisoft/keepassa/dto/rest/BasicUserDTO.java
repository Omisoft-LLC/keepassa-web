package com.omisoft.keepassa.dto.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.omisoft.keepassa.structures.SecureString;
import lombok.Data;

/**
 * Created by leozhekov on 11/1/16. Basic user info
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasicUserDTO {

  private String name;
  private String lastName;
  private String email;
  private Boolean disableCaptcha;
  private SecureString password;
  private SecureString repeatPassword;
  private String verify;

  public BasicUserDTO() {
    disableCaptcha = Boolean.FALSE;

  }
}
