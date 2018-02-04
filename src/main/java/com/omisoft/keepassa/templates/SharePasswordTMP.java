package com.omisoft.keepassa.templates;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by leozhekov on 11/12/16. Template class for the group invitation email.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SharePasswordTMP extends BaseTMP {

  private String userEmail;
  private String logo;
  private String shareName;

  public SharePasswordTMP() {
  }

  public SharePasswordTMP(String userEmail, String logo, String shareName) {
    this.userEmail = userEmail;
    this.logo = logo;
    this.shareName = shareName;
  }
}
