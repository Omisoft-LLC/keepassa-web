package com.omisoft.keepassa.dto.rest;

import com.omisoft.keepassa.entities.users.Department;
import com.omisoft.keepassa.entities.users.User;
import lombok.Data;


/**
 * Created by leozhekov on 1/19/17.
 */
@Data
public class SelectTagsSearchDTO {

  private String id;
  private String text;

  public SelectTagsSearchDTO(User user) {
    id = user.getEmail();
    text = user.getEmail();
  }

  public SelectTagsSearchDTO(Department department) {
    id = department.getId().toString();
    text = department.getDepartmentName();
  }

  public SelectTagsSearchDTO() {
  }
}
