package com.omisoft.keepassa.dto.rest;

import com.omisoft.keepassa.entities.users.ContactInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Contact Info Created by dido on 13.12.16.
 */
@Slf4j
@Data
public class ContactInfoDTO {

  private String mobilePhone;
  private String facebook;
  private String twitter;
  private String skype;
  private String googlePlus;
  private String linkedIn;
  private String website;

  public ContactInfoDTO() {
  }

  public ContactInfoDTO(ContactInfo contactInfo) {
    this.mobilePhone = contactInfo.getMobilePhone();
    this.facebook = contactInfo.getFacebook();
    this.twitter = contactInfo.getTwitter();
    this.skype = contactInfo.getSkype();
    this.googlePlus = contactInfo.getGooglePlus();
    this.linkedIn = contactInfo.getLinkedIn();
    this.website = contactInfo.getWebsite();
  }
}
