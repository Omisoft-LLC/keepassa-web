package com.omisoft.keepassa.entities.passwords;

import com.omisoft.keepassa.dto.rest.GroupDTO;
import com.omisoft.keepassa.entities.users.Company;
import com.omisoft.keepassa.entities.users.User;
import com.omisoft.server.common.entities.BaseEntity;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

/**
 * Holds group info
 * Created by leozhekov on 10/28/16.
 */
@Entity
@Table(name = "groups")
@Audited
@Getter
@Setter
@NamedQueries({
    @NamedQuery(name = Company.DELETE_EMPTY_GROUP, query = "delete  from Group g where g.groupSafes is empty"),
    @NamedQuery(name = Company.FIND_GROUPS_BY_USER, query = "select g from Group g where :user MEMBER OF g.users")})
public class Group extends BaseEntity {

  @Column(name = "name")
  private String name;

  @Column(name = "group_email")
  private String groupEmail;

  @Column(name = "description")
  private String description;


  @OneToOne
  private User admin;

  @ManyToMany(mappedBy = "groups")
  private Set<User> users;

  @ManyToMany(mappedBy = "groups", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<PasswordSafe> groupSafes;

  public Group() {
    users = new HashSet<>();
    groupSafes = new HashSet<>();
  }

  public Group(GroupDTO groupDTO) {
    this();
    name = groupDTO.getName();
    groupEmail = groupDTO.getGroupEmail();
    description = groupDTO.getDescription();
    if (groupDTO.getId() != null) {
      setId(groupDTO.getId());
    }
  }

  @Override
  public String toString() {
    return name + groupEmail + description;
  }
}
