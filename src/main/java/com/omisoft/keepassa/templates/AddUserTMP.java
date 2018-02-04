package com.omisoft.keepassa.templates;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by leozhekov on 11/12/16. Template class for the group invitation email.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class AddUserTMP extends BaseTMP {

  private String adminEmail;
  private String logo;
  private String subject;
  private String message;
  private String inviteCode;

  public AddUserTMP() {
  }

  public AddUserTMP(String adminEmail, String logo, String message, String inviteCode) {
    this.adminEmail = adminEmail;
    this.logo = logo;
    this.message = message;
    this.inviteCode = inviteCode;
  }
}
