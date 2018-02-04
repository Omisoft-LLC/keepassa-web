package com.omisoft.keepassa.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.omisoft.keepassa.structures.SecureString;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by leozhekov on 11/1/16.
 */
@Data
@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoggedUserInfo {

  private String email;
  private SecureString password;
  private String companyId;

  public LoggedUserInfo(String email, SecureString password, String companyId) {
    this.email = email;
    this.password = password;
    this.companyId = companyId;
  }

  public LoggedUserInfo() {

  }

}
