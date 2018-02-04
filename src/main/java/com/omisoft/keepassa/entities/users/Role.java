package com.omisoft.keepassa.entities.users;

import com.omisoft.keepassa.dto.rest.RoleDTO;
import com.omisoft.server.common.entities.BaseEntity;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

/**
 * Holds user roles
 * Created by dido on 9/28/16.
 */
@Entity
@Getter
@Setter
@Audited
public class Role extends BaseEntity {

  @ManyToMany(mappedBy = "roles", targetEntity = User.class)
  public Set<User> users;
  private String roleName;
  @Enumerated(EnumType.STRING)
  @ElementCollection(targetClass = PermissionEnum.class, fetch = FetchType.EAGER)
  @JoinTable(name = "permissions_roles", joinColumns = @JoinColumn(name = "role_id"))
  @Column(nullable = false)
  private Set<PermissionEnum> permissions;

  public Role() {
    permissions = new HashSet<>();

  }

  public Role(RoleDTO roleDTO) {
    permissions = roleDTO.getPermissions();
    roleName = roleDTO.getRoleName();
  }

  @Override
  public String toString() {
    return "Role{" + "roleName='" + roleName + '\'' + ", permissions="
        + permissions + '}';
  }
}
