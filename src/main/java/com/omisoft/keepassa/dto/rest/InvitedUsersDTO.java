package com.omisoft.keepassa.dto.rest;

import java.util.Set;
import java.util.UUID;
import lombok.Data;

/**
 * Created by leozhekov on 11/12/16. Invited Users. Used by the android client (or in future other
 * clients as well) to send list of emails to be added to the group.
 */
@Data
public class InvitedUsersDTO {

  //  private List<String> emails;
  private Set<UserWithAESDTO> userWithAES;
  private UUID groupId;

  public InvitedUsersDTO(Set<UserWithAESDTO> userWithAES, UUID groupId) {
//    this.emails = emails;
    this.userWithAES = userWithAES;
    this.groupId = groupId;
  }

  public InvitedUsersDTO() {
  }
}
