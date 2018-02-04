package com.omisoft.keepassa.entities.users;


import com.omisoft.keepassa.dto.rest.ContactInfoDTO;
import com.omisoft.keepassa.dto.rest.ProfileDTO;
import com.omisoft.keepassa.entities.passwords.FileData;
import com.omisoft.keepassa.entities.passwords.Group;
import com.omisoft.keepassa.entities.passwords.PasswordSafe;
import com.omisoft.keepassa.entities.passwords.PasswordSafeKey;
import com.omisoft.keepassa.structures.SecureKeystore;
import com.omisoft.server.common.entities.BaseEntity;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.QueryHints;
import org.hibernate.envers.Audited;

/**
 * User Entity Created by leozhekov on 10/28/16.
 */
@Entity
@Table(name = "users", indexes = {@Index(name = "emailIdx", columnList = "email", unique = true)})
@Audited
@Getter
@Setter
@Slf4j
@NamedQueries({
    @NamedQuery(name = User.USER_BY_EMAIL, query = "select u from User u where u.email=:email", hints = @QueryHint(name = QueryHints.FLUSH_MODE, value = "ALWAYS")),
    @NamedQuery(name = User.FIND_USERS, query = "Select u from User u where upper(u.email) like :search"),
    @NamedQuery(name = User.FIND_USER_AND_COMPANY, query = "select u from User u where u.id=:userId and u.company=:company"),
    @NamedQuery(name = User.ALL_USERS_BY_COMPANY, query = "select u from User u where u.company=:company"),
    @NamedQuery(name = User.FIND_BY_EMAIL_AND_COMPANY, query = "select u from User u where u.email=:email and u.company=:company")})
public class User extends BaseEntity {

  public static final String USER_BY_EMAIL = "USER_BY_EMAIL";
  public static final String FIND_USERS = "FIND_USERS";
  public static final String FIND_USER_AND_COMPANY = "FIND_USER_AND_COMPANY";
  public static final String ALL_USERS_BY_COMPANY = "ALL_USERS_BY_COMPANY";
  public static final String FIND_BY_EMAIL_AND_COMPANY = "FIND_BY_EMAIL_AND_COMPANY";
  private static final long serialVersionUID = 3694126891134368267L;

  @Column(name = "firstName")
  private String firstName;

  @Column(name = "lastName")
  private String lastName;

  @Column(name = "password")
  private String password;



  @Column(name = "position")
  private String position;

  @Column(name = "about", columnDefinition = "VARCHAR(254)")
  private String about;

  @Column(name = "email", nullable = false)

  private String email;

  @Basic(fetch = FetchType.LAZY)
  private byte[] authKey;

  private String mutualSslOTPKey;
  @Column(name = "clientPublicKey", columnDefinition = "VARCHAR(2048)")
  private String clientPublicKey;

  @Basic(fetch = FetchType.LAZY)
  @Column(name = "serverPublicKey", columnDefinition = "VARCHAR(4096)")
  private String serverPublicKeyAndCertificate;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "file_data_id")
  private FileData poster;

  // security flags
  @Column(name = "isTwoFEnabled", columnDefinition = "BOOLEAN default false")
  private Boolean isTwoFEnabled;
  @Column(name = "isExpired", columnDefinition = "BOOLEAN default false")
  private Boolean isExpired;
  @Column(name = "isExpiredEnabled", columnDefinition = "BOOLEAN default false")
  private Boolean isExpiredEnabled;
  @Column(name = "isSuspended", columnDefinition = "BOOLEAN default false")
  private Boolean isSuspended;

  @Column(name = "isAdmin", columnDefinition = "BOOLEAN default false")
  private Boolean isAdmin;
  //  @Column(name = "isPasswordExpired", columnDefinition = "BOOLEAN default false")
//  private Boolean isPasswordExpired;
//  @Column(name = "isPasswordExpiredEnabled", columnDefinition = "BOOLEAN default false")
//  private Boolean isPasswordExpiredEnabled;
  @Column(name = "isWorkHoursAllowed", columnDefinition = "BOOLEAN default false")
  private Boolean isWorkHoursAllowed;

  @Column(name = "beginWorkHour")
  private Date beginWorkHour;
  @Column(name = "endWorkHour")
  private Date endWorkHour;
  @Column(name = "expires_on")
  private Date expirationDate;
  @Column(name = "password_expires_on")
  private Date passwordExpirationDate;


  @Embedded
  private ContactInfo contactInfo;


  @ManyToMany
  @JoinTable(name = "user_departments",
      joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "department_id", referencedColumnName = "id"))
  private Set<Department> departments;

  @ManyToMany(targetEntity = Role.class, fetch = FetchType.EAGER)
  @JoinTable(name = "user_roles", joinColumns =
  @JoinColumn(name = "user_id", referencedColumnName = "id"),
      inverseJoinColumns =
      @JoinColumn(name = "role_id", referencedColumnName = "id"))
  private Set<Role> roles;

  @Basic(fetch = FetchType.LAZY)
  private byte[] keyStore;

  @OneToMany
  private Set<PasswordSafeKey> keys;

  @ManyToMany
  @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
  @JoinTable(name = "user_groups", joinColumns = {@JoinColumn(name = "user_id", nullable = false)},
      inverseJoinColumns = {@JoinColumn(name = "group_id", nullable = false)})
  private Set<Group> groups;

  @ManyToMany
  @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
  @JoinTable(name = "user_personal_safes",
      joinColumns = {@JoinColumn(name = "user_id", nullable = false)},
      inverseJoinColumns = {@JoinColumn(name = "personalsafe_id", nullable = false)})
  private Set<PasswordSafe> passwordSafes;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_id")
  private Company company;

  public User() {
    this.groups = new HashSet<>();
    this.passwordSafes = new HashSet<>();
    this.contactInfo = new ContactInfo();
    this.poster = new FileData();
    this.isTwoFEnabled = Boolean.FALSE;
    this.isSuspended = Boolean.FALSE;
    this.isExpired = Boolean.FALSE;
    this.isExpiredEnabled = Boolean.FALSE;
//    this.isPasswordExpired = Boolean.FALSE;
//    this.isPasswordExpiredEnabled = Boolean.FALSE;
    this.isWorkHoursAllowed = Boolean.FALSE;
  }

  public User(ProfileDTO profileDTO) {
    this();
    this.firstName = profileDTO.getFirstName();
    this.lastName = profileDTO.getLastName();
    this.about = profileDTO.getAbout();
    this.contactInfo = new ContactInfo(profileDTO.getContactInfo());
  }


  public User mapProfile(ProfileDTO profileDTO) {
    this.firstName = profileDTO.getFirstName();
    this.lastName = profileDTO.getLastName();
    this.position = profileDTO.getPosition();
    this.about = profileDTO.getAbout();
    return this;
  }

  //
  public User mapContactInfo(ContactInfoDTO contactInfoDTO) {
    this.getContactInfo().setMobilePhone(contactInfoDTO.getMobilePhone());
    this.getContactInfo().setFacebook(contactInfoDTO.getFacebook());
    this.getContactInfo().setTwitter(contactInfoDTO.getTwitter());
    this.getContactInfo().setGooglePlus(contactInfoDTO.getGooglePlus());
    this.getContactInfo().setLinkedIn(contactInfoDTO.getLinkedIn());
    this.getContactInfo().setWebsite(contactInfoDTO.getWebsite());
    this.getContactInfo().setSkype(contactInfoDTO.getSkype());
    return this;
  }

  @PrePersist
  protected void onCreate() {

  }

  @PreUpdate
  protected void onUpdate() {
    log.info("UPDATING");
//    if (secureKeystore != null && secureKeystore.isDirty()) {
//      this.keyStore = SerializationUtils.serialize(secureKeystore);
//      secureKeystore.setDirty(false);
//    }
  }

  @PostLoad
  protected void onLoad() {
    log.info("LOADING");

//    this.secureKeystore = SerializationUtils.deserialize(keyStore);

  }


}


