package com.omisoft.keepassa.entities.users;

import com.omisoft.keepassa.dto.rest.ContactInfoDTO;
import java.io.Serializable;
import javax.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by dido on 13.12.16.
 */
@Embeddable
@Getter
@Setter
public class ContactInfo implements Serializable {

  private String mobilePhone;
  private String facebook;
  private String twitter;
  private String skype;
  private String googlePlus;
  private String linkedIn;
  private String website;

  public ContactInfo() {  // Fix for https://hibernate.atlassian.net/browse/HHH-7610
    mobilePhone = new String();
  }

  public ContactInfo(ContactInfoDTO contactInfoDTO) {
    this();
    if (contactInfoDTO != null) {
      this.mobilePhone = contactInfoDTO.getMobilePhone();
      this.facebook = contactInfoDTO.getFacebook();
      this.twitter = contactInfoDTO.getTwitter();
      this.skype = contactInfoDTO.getSkype();
      this.googlePlus = contactInfoDTO.getGooglePlus();
      this.linkedIn = contactInfoDTO.getLinkedIn();
      this.website = contactInfoDTO.getWebsite();
    }
  }
}
