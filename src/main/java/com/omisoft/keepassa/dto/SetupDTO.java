package com.omisoft.keepassa.dto;

import com.omisoft.keepassa.structures.SecureString;
import lombok.Data;

/**
 * Created by nslavov on 6/23/17.
 * First time setup system administrator - email, password and public key
 */
@Data
public class SetupDTO {

  private String email;
  private SecureString password;
  private String publicKey;

}
