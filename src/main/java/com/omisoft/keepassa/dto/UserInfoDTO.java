package com.omisoft.keepassa.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.inject.servlet.SessionScoped;
import com.omisoft.keepassa.dto.rest.SecurityDTO;
import com.omisoft.keepassa.entities.users.User;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * Holds logged user info Created by dido on 9/26/16. Session scoped
 */
@Data
@SessionScoped
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfoDTO implements Serializable {

  private String firstName;
  private String email;
  private String lastName;
  private String ipAddress;
  private SecurityDTO security;
  private Date loginDate;
  private String departments;
  private String position;
  private Date createdOn;
  private String id;
  private String lastAction;

  public UserInfoDTO() {

  }

  public UserInfoDTO(User user) {
    this.firstName = user.getFirstName();
    this.email = user.getEmail();
    this.lastName = user.getLastName();
    this.loginDate = new Date();
    this.position = user.getPosition();
    this.createdOn = user.getCreatedOn();
    this.id = user.getId().toString();
  }


  public UserInfoDTO(User user, String departments) {
    this.firstName = user.getFirstName();
    this.email = user.getEmail();
    this.lastName = user.getLastName();
    this.security = new SecurityDTO(user);
    this.loginDate = new Date();
    this.departments = departments;
    this.position = user.getPosition();
    this.createdOn = user.getCreatedOn();
    this.id = user.getId().toString();
  }


  public void setProps(User usr, String ip) {
    firstName = usr.getFirstName();
    email = usr.getEmail();
    loginDate = new Date();
    lastName = usr.getLastName();
    ipAddress = ip;
    this.position = usr.getPosition();
    // TODO set departments
  }
}
