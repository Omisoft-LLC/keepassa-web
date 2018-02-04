package com.omisoft.keepassa.entities.passwords;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.omisoft.keepassa.dto.rest.PasswordSafeDTO;
import com.omisoft.keepassa.entities.users.User;
import com.omisoft.keepassa.structures.SecureString;
import com.omisoft.server.common.entities.BaseEntity;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

/**
 * Created by leozhekov on 10/28/16.
 */
@Entity
@Table(name = "password_safe", indexes = {@Index(name = "urlIdx", columnList = "url"),
    @Index(name = "appIdIdx", columnList = "app_id"),
    @Index(name = "encryptIdIdx", columnList = "password_encrypt_id", unique = true)})
@Audited
@Getter
@Setter
@NamedQueries({
    @NamedQuery(name = PasswordSafe.FIND_PASSWORD_BY_APP_ID, query = "select ps from PasswordSafe ps where :user MEMBER OF ps.users and ps.appId=:appId")})
public class PasswordSafe extends BaseEntity {

  public static final String FIND_PASSWORD_BY_APP_ID = "FIND_PASSWORD_BY_APP_ID";
  @Column(name = "name")
  private String name;
  @Column(name = "url")
  private String url;
  @Column(name = "app_id")
  private String appId;
  @Basic(fetch = FetchType.LAZY)
  private byte[] password;
  @Column(name = "accountName")
  private String username;
  @Column(name = "description")
  private String description;
  @Transient
  private SecureString decodedPassword;
  @JsonIgnore
  @OneToMany(
      fetch = FetchType.LAZY)
  @JoinTable(name = "password_safe_file_data",
      joinColumns = {@JoinColumn(name = "passwordsafe_id", referencedColumnName = "id")},
      inverseJoinColumns = {@JoinColumn(name = "filedata_id", referencedColumnName = "id")})
  private Set<FileData> attachedFiles = new HashSet<>();

  @ManyToMany(mappedBy = "passwordSafes", fetch = FetchType.LAZY)
  @JsonIgnore
  private Set<User> users;

  @OneToOne
  @JsonIgnore
  private User creator;

  @ManyToMany
  @JsonIgnore
  private Set<Group> groups;
  @Column(nullable = false, name = "password_encrypt_id")
  private UUID passwordEncryptId;

  @Column(name = "isExpired")
  private Boolean isExpired;

  @Column(name = "inGroup", columnDefinition = "BOOLEAN default false")
  private Boolean inGroup;

  @Column(name = "inShares", columnDefinition = "BOOLEAN default false")
  private Boolean inShares;

  @Basic(fetch = FetchType.LAZY)
  private byte[] clientAESKey;
  @OneToMany(fetch = FetchType.EAGER)
  private Set<UserWithAES> userWithAESList;

  @OneToMany
  private Set<PasswordSafeKey> keys;


  public PasswordSafe() {
    super();
    passwordEncryptId = UUID.randomUUID();
    groups = new HashSet<>();
    users = new HashSet<>();
    userWithAESList = new HashSet<>();
    attachedFiles = new HashSet<>();
  }

  public PasswordSafe(PasswordSafeDTO passwordSafeDTO) {
    this();
    username = passwordSafeDTO.getAccountName();
    description = passwordSafeDTO.getDescription();
    name = passwordSafeDTO.getName();
    url = passwordSafeDTO.getUrl();
    appId = passwordSafeDTO.getAppId();
    isExpired = Boolean.FALSE;
    inGroup = passwordSafeDTO.isInGroup();
    inShares = passwordSafeDTO.isInShares();
    clientAESKey = passwordSafeDTO.getClientAESKey();

  }

  public void addAttachedFiles(FileData fileData) {
    this.attachedFiles.add(fileData);
  }

//  public void  mapUserWithAes(List<UserWithAESDTO> userWithAESDTOS) {
//    if (this.getInGroup() || this.getInShares()) {
//      for (UserWithAESDTO dto : userWithAESDTOS) {
//        userWithAESList.add(new UserWithAES(dto));
//      }
//    }
//  }

}
