package com.omisoft.keepassa.entities.users;

import com.omisoft.server.common.entities.BaseEntity;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

/**
 * Company entity
 * Created by dido on 13.03.17.
 */
@Getter
@Setter
@Entity
@Audited
@NamedQueries({
    @NamedQuery(name = Company.CHECK_IF_ADMIN, query = "select count (c) from Company c where c = :userCompany  and administrator=:user")})
public class Company extends BaseEntity {

  public static final String DELETE_EMPTY_GROUP = "DELETE_EMPTY_GROUP";
  public static final String FIND_GROUPS_BY_USER = "FIND_GROUPS_BY_USER";
  public static final String CHECK_IF_ADMIN = "CHECK_IF_ADMIN";
  @OneToMany(mappedBy = "company")
  private Set<User> users = new HashSet<>();
  private String name;
  @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = false,
      fetch = FetchType.LAZY)
  @JoinColumn(name = "company_id", referencedColumnName = "id")
  private Set<Department> departments = new HashSet<>();
  @OneToOne
  private User administrator;
  @Column(name = "website", columnDefinition = "VARCHAR(100)")
  private String website;
  @Column(name = "adress", columnDefinition = "VARCHAR(100)")
  private String address;
  @Column(name = "about", columnDefinition = "VARCHAR(300)")
  private String about;
  @Column(name = "ca")
  private byte[] caFile;


  public Company() {

  }

  // Creates a new company with the following user as admin
  public Company(User admin, String companyName) {
    this.administrator = admin;
    this.name = companyName;
  }


}
