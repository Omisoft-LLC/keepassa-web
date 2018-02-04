package com.omisoft.keepassa.dto;

import com.omisoft.keepassa.entities.users.User;
import lombok.Data;

/**
 * Created by leozhekov on 08.12.16.
 */
@Data
public class SafeBasicUserInfoDTO {

  private String name;
  private String email;

  public SafeBasicUserInfoDTO(String name, String email) {
    this.name = name;
    this.email = email;
  }

  public SafeBasicUserInfoDTO() {
  }

  public SafeBasicUserInfoDTO(User u) {
    // this.name = u.getFirstName()+" "+u.getLastName();
    this.name = u.getFirstName();
    this.email = u.getEmail();
  }
}
