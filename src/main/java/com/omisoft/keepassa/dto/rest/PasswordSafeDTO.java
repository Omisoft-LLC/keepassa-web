package com.omisoft.keepassa.dto.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.omisoft.keepassa.entities.passwords.PasswordSafe;
import com.omisoft.keepassa.entities.passwords.UserWithAES;
import com.omisoft.keepassa.structures.SecureString;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.Data;

/**
 * Password Safe DTO Created by leozhekov on 10/28/16.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PasswordSafeDTO {

  private String name;
  private String url;
  private String appId;
  private SecureString password;
  private String accountName;
  private String description;
  private UUID id;
  private Date createdOn;
  private boolean isCreator;
  private String sharedWith;
  private List<String> sharedWithList;
  private Set<UserWithAESDTO> sharedWithUsers;
  private String creatorEmail;
  private boolean inGroup;
  private boolean inShares;
  private byte[] clientAESKey;

  public PasswordSafeDTO() {
  }

  public PasswordSafeDTO(PasswordSafe passwordSafe) {
    name = passwordSafe.getName();
    url = passwordSafe.getUrl();
    appId = passwordSafe.getAppId();
    accountName = passwordSafe.getUsername();
    // TODO write decrypt method in DAO
    // password = passwordSafe.getPassword();
    accountName = passwordSafe.getUsername();
    description = passwordSafe.getDescription();
    id = passwordSafe.getId();
    createdOn = passwordSafe.getCreatedOn();
    creatorEmail = (passwordSafe.getCreator() != null) ? passwordSafe.getCreator().getEmail() : "";
    inGroup = passwordSafe.getInGroup();
    inShares = passwordSafe.getInShares();
    clientAESKey = passwordSafe.getClientAESKey();
    sharedWithUsers = mapSharedWith(passwordSafe.getUserWithAESList());
  }

  public PasswordSafeDTO(PasswordSafe passwordSafe, boolean isCreator) {
    name = passwordSafe.getName();
    url = passwordSafe.getUrl();
    appId = passwordSafe.getAppId();
    accountName = passwordSafe.getUsername();
    // TODO write decrypt method in DAO
    // password = passwordSafe.getPassword();
    accountName = passwordSafe.getUsername();
    description = passwordSafe.getDescription();
    id = passwordSafe.getId();
    createdOn = passwordSafe.getCreatedOn();
    this.isCreator = isCreator;
    creatorEmail = (passwordSafe.getCreator() != null) ? passwordSafe.getCreator().getEmail() : "";

  }

  public PasswordSafeDTO(PasswordSafe passwordSafe, boolean isCreator,
      List<String> sharedWithList) {
    name = passwordSafe.getName();
    url = passwordSafe.getUrl();
    appId = passwordSafe.getAppId();
    accountName = passwordSafe.getUsername();
    // TODO write decrypt method in DAO
    // password = passwordSafe.getPassword();
    accountName = passwordSafe.getUsername();
    description = passwordSafe.getDescription();
    id = passwordSafe.getId();
    createdOn = passwordSafe.getCreatedOn();
    this.isCreator = isCreator;
    this.sharedWithList = sharedWithList;
    sharedWith = concatUserEmails(sharedWithList);
    creatorEmail = (passwordSafe.getCreator() != null) ? passwordSafe.getCreator().getEmail() : "";
  }

  private Set<UserWithAESDTO> mapSharedWith(Set<UserWithAES> userWithAESList) {
    Set<UserWithAESDTO> users = new HashSet<>();
    for (UserWithAES usr : userWithAESList) {
      users.add(new UserWithAESDTO(usr));
    }
    return users;
  }

  private String concatUserEmails(List<String> sharedWithList) {
    StringBuilder buf = new StringBuilder();
    for (String str : sharedWithList) {
      buf.append(str).append(", ");
    }
    if (sharedWithList.size() > 0) {
      buf.delete(buf.length() - 2, buf.length());
    }
    return buf.toString();
  }

}
