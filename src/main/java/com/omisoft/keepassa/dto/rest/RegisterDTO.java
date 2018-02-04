package com.omisoft.keepassa.dto.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.omisoft.keepassa.structures.SecureString;
import lombok.Data;

/**
 * Created by leozhekov on 2/7/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RegisterDTO {

  private String email;
  private SecureString password;
  private String publicKey;
  private String verify;
  private String companyName;
  private String inviteCode;
}
