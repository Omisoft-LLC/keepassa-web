package com.omisoft.keepassa.dto.rest;

import com.omisoft.keepassa.entities.users.PermissionEnum;
import com.omisoft.keepassa.entities.users.Role;
import java.util.Set;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Role dto
 * Created by dido on 16.01.17.
 */
@Slf4j
@Data
public class RoleDTO {

  private String roleName;
  private Set<PermissionEnum> permissions;

  public RoleDTO(Role role) {
    roleName = role.getRoleName();
    permissions = role.getPermissions();
  }

  public RoleDTO() {

  }

}
