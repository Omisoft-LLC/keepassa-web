package com.omisoft.keepassa.templates;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by leozhekov on 1/5/17.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SendNormalEmailTMP extends BaseTMP {

  private String emailFrom;
  private String message;
  private String logo;

  public SendNormalEmailTMP(String emailFrom, String message, String logo) {
    this.emailFrom = emailFrom;
    this.message = message;
    this.logo = logo;
  }
}
