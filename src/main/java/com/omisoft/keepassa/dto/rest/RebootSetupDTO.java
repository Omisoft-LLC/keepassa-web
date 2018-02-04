package com.omisoft.keepassa.dto.rest;

import com.omisoft.keepassa.structures.SecureString;
import lombok.Data;

/**
 * Created by leozhekov on 2/6/17.
 */
@Data
@Deprecated
public class RebootSetupDTO {

  private String adminEmail;
  private SecureString masterPassword;
  private SecureString adminPassword;
}
