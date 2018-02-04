package com.omisoft.keepassa.templates;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by leozhekov on 11/12/16. Template class for the group invitation email.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InvitedTMP extends BaseTMP {

  private String adminEmail;
  private String logo;
  private String groupName;

  public InvitedTMP() {
  }

  public InvitedTMP(String adminEmail, String logo, String groupName) {
    this.adminEmail = adminEmail;
    this.logo = logo;
    this.groupName = groupName;
  }
}
