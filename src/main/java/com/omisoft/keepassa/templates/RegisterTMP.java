package com.omisoft.keepassa.templates;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by leozhekov on 11/12/16. Template class for the registration confirmation email.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RegisterTMP extends BaseTMP {

  private String title;
  private String logo;

  public RegisterTMP(String title, String logo) {
    this.title = title;
    this.logo = logo;
  }

  public RegisterTMP() {
  }
}
