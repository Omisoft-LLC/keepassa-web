package com.omisoft.keepassa.dto;

import java.util.UUID;
import lombok.Data;

/**
 * Created by leozhekov on 11/12/16. DTO for the inmemory cache for groups. Used when inviting
 * people in the group to store the group info and key, so when the user accepts the invitation, the
 * key is copied to his keystore.
 */
@Data
public class GroupInfoDTO {

  // TODO findbugs says that sharedKey should not be stored like that.
  // EI_EXPOSE_REP, EI_EXPOSE_REP2
  // may expose internal representation by storing an externally mutable object into GroupInfoDTO.sharedKey
  private UUID groupId;
  private String userEmail;

  public GroupInfoDTO(UUID groupId, String userEmail) {
    this.groupId = groupId;
    this.userEmail = userEmail;
  }

  public GroupInfoDTO() {
  }
}
