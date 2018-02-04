package com.omisoft.keepassa.entities.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.omisoft.keepassa.dto.rest.DepartmentInfoDTO;
import com.omisoft.server.common.entities.BaseEntity;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

/**
 * Holds department data reference table Created by dido on 11/16/16.
 */
@Entity
@Getter
@Setter
@Audited
@NamedQueries({
    @NamedQuery(name = Department.FIND_MATCHING_DEPARTMENTS, query = "Select d from Department d where d.company=:company and UPPER(d.departmentName) like :search"),
    @NamedQuery(name = Department.FIND_BY_ID_AND_COMPANY, query = "select d from Department d where d.id=:departmentId and d.company=:company"),
    @NamedQuery(name = Department.FIND_ALL_BY_COMPANY, query = "select d from Department d where d.company=:company")})
public class Department extends BaseEntity {

  public static final String FIND_MATCHING_DEPARTMENTS = "FIND_MATCHING_DEPARTMENTS";
  public static final String FIND_BY_ID_AND_COMPANY = "FIND_BY_ID_AND_COMPANY";
  public static final String FIND_ALL_BY_COMPANY = "FIND_ALL_BY_COMPANY";
  private static final long serialVersionUID = -8116523290098649420L;
  @Column(name = "name")
  private String departmentName;
  @ManyToMany(mappedBy = "departments")
  @JsonIgnore
  private Set<User> users;

  @OneToOne
  private User departmentHead;
  @ManyToOne
  private Company company;

  public Department(DepartmentInfoDTO departmentInfoDTO, User departmentHead) {
    this();
    this.departmentHead = departmentHead;
    this.departmentName = departmentInfoDTO.getDepartmentName();
    this.getUsers().add(departmentHead);
  }

  public Department() {
    this.users = new HashSet<>();
  }

  @Override
  public String toString() {
    return departmentName;
  }

}
