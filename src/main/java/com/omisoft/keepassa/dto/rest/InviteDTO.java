package com.omisoft.keepassa.dto.rest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Data;

/**
 * Created by nslavov on 2/16/17.
 */
@Data
public class InviteDTO {

  private List<UserWithAESDTO> userWithAESDTOS;
  private Set<PasswordSafeDTO> passwordSafeDTOS;

  public InviteDTO() {
    this.userWithAESDTOS = new ArrayList<>();
    this.passwordSafeDTOS = new HashSet<>();
  }


}
