package com.omisoft.keepassa.dto.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.omisoft.keepassa.entities.users.User;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by nslavov on 12/8/16.
 */
@Slf4j
@Data
public class ProfileDTO {

  private String firstName;
  private String lastName;
  private Boolean isTwoFEnabled;
  private String position;
  private String about;
  private String email;
  private Long id;
  @JsonProperty("contactInfo")
  private ContactInfoDTO contactInfo;

  public ProfileDTO() {
  }

  public ProfileDTO(User user) {
    firstName = user.getFirstName();
    lastName = user.getLastName();
    position = user.getPosition();
    about = user.getAbout();
    email = user.getEmail();
    isTwoFEnabled = user.getIsTwoFEnabled();
    contactInfo = new ContactInfoDTO(user.getContactInfo());

  }

}
