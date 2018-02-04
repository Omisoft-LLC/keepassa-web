package com.omisoft.keepassa.dto.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.omisoft.keepassa.entities.passwords.UserWithAES;
import java.util.UUID;
import lombok.Data;

/**
 * Created by leozhekov on 2/8/17.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserWithAESDTO {

  private String email;
  private String key;
  private UUID psId;

  public UserWithAESDTO(String email, String key) {
    this.email = email;
    this.key = key;
  }

  public UserWithAESDTO() {
  }

  public UserWithAESDTO(UserWithAES usr) {
    this.email = usr.getEmail();
    this.key = usr.getKey();
  }

}
