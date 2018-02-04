package com.omisoft.keepassa.dto.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.omisoft.keepassa.structures.SecureString;
import lombok.Data;

/**
 * Created by leozhekov on 1/18/17.
 */
@Data
@Deprecated
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResetPasswordDTO {

  private String email;
  private SecureString oldPassword;
  private SecureString newPassword;
}
