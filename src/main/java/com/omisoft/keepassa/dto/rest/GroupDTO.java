package com.omisoft.keepassa.dto.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.omisoft.keepassa.dto.SafeBasicUserInfoDTO;
import com.omisoft.keepassa.entities.passwords.Group;
import com.omisoft.keepassa.entities.passwords.PasswordSafe;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.Data;

/**
 * Created by leozhekov on 10/28/16. Group DTO. Used pretty much everywhere in group functionality.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupDTO {

  private String name;
  private String groupEmail;
  private String description;
  private List<PasswordSafeDTO> groupSafes;
  private List<SafeBasicUserInfoDTO> users;
  private UUID id;
  private String adminEmail;
  private int numberOfSafes;
  private int numberOfUsers;

  public GroupDTO(String name, String groupEmail, String description,
      Set<PasswordSafeDTO> groupSafes, UUID groupId, String adminEmail) {
    this.name = name;
    this.groupEmail = groupEmail;
    this.description = description;
    this.groupSafes = new ArrayList<>();
    this.groupSafes.addAll(groupSafes);
    this.id = groupId;
    this.adminEmail = adminEmail;
  }


  public GroupDTO() {
    this.groupSafes = new ArrayList<>();
  }


  public GroupDTO(Group group) {
    this();
    name = group.getName();
    groupEmail = group.getGroupEmail();
    description = group.getDescription();
    id = group.getId();
    adminEmail = group.getAdmin().getEmail();
    numberOfSafes = group.getGroupSafes().size();
    numberOfUsers = group.getUsers().size();
    if (group.getGroupSafes() != null) {
      for (PasswordSafe passwordSafe : group.getGroupSafes()) {
        groupSafes.add(new PasswordSafeDTO(passwordSafe));
      }
    }
  }

}
