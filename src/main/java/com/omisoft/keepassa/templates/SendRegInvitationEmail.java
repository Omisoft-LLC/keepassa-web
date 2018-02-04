package com.omisoft.keepassa.templates;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by leozhekov on 1/6/17.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SendRegInvitationEmail extends BaseTMP {

  private String sender;
  private String logo;

  public SendRegInvitationEmail(String sender, String logo) {
    this.sender = sender;
    this.logo = logo;
  }
}
