package com.omisoft.keepassa.dto.rest;

import com.omisoft.keepassa.entities.users.User;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.Data;

/**
 * Created by leozhekov on 1/11/17.
 */
@Data
public class SecurityDTO implements Serializable {

  private String workFrom;
  private String workTo;
  private boolean isSuspended;
  private boolean isExpired;
  private boolean isExpiredEnabled;
  private boolean isWorkHoursAllowed;
  //  private boolean isPasswordExpired;
//  private boolean isPasswordExpiredEnabled;
//  private Date passwordExpirationDate;
  private Date accountExpirationDate;
  private String accExpDateString;
  private String passExpDateString;

  public SecurityDTO(User user) {
    this.isSuspended = user.getIsSuspended();
    this.isExpiredEnabled = user.getIsExpiredEnabled();
    this.isExpired = user.getIsExpired();
//    this.isPasswordExpired = user.getIsPasswordExpired();
//    this.isPasswordExpiredEnabled = user.getIsPasswordExpiredEnabled();
    this.isWorkHoursAllowed = user.getIsWorkHoursAllowed();
//    this.passwordExpirationDate = user.getPasswordExpirationDate();
    this.accountExpirationDate = user.getExpirationDate();
    if (this.isWorkHoursAllowed) {
      this.workFrom = new SimpleDateFormat("HH:mm").format(user.getBeginWorkHour());
      this.workTo = new SimpleDateFormat("HH:mm").format(user.getEndWorkHour());
    }
  }

  public SecurityDTO() {

  }
}
