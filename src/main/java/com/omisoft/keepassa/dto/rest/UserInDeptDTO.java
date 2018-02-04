package com.omisoft.keepassa.dto.rest;

import com.omisoft.keepassa.entities.users.User;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import lombok.Data;

/**
 * Created by leozhekov on 1/12/17.
 */
@Data
public class UserInDeptDTO implements Serializable {

  private String name;
  private Date memberSince;
  private UUID id;

  public UserInDeptDTO(User user) {
    this.name = user.getFirstName() + " " + user.getLastName();
    this.memberSince = user.getCreatedOn();
    this.id = user.getId();
  }

  public UserInDeptDTO() {

  }
}
