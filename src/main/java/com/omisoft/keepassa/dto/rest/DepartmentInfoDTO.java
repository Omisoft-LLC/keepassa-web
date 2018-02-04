package com.omisoft.keepassa.dto.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.omisoft.keepassa.entities.users.Department;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.Data;

/**
 * Created by leozhekov on 1/11/17.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DepartmentInfoDTO implements Serializable {

  private String departmentName;
  private String departmentHeadEmail;
  private String departmentHead;
  private Date createdOn;
  private Date memberSince;
  private int numberOfMembers;
  private UUID id;
  private List<UserInDeptDTO> members;

  public DepartmentInfoDTO(Department department) {
    this();
    this.departmentName = department.getDepartmentName();
    this.departmentHead =
        department.getDepartmentHead().getFirstName() + " " + department.getDepartmentHead()
            .getLastName();
    this.createdOn = department.getCreatedOn();
    this.memberSince = department.getCreatedOn();
    this.numberOfMembers = department.getUsers().size();
    this.departmentHeadEmail = department.getDepartmentHead().getEmail();
    this.id = department.getId();
  }

  public DepartmentInfoDTO() {
    members = new ArrayList<>();
  }
}
