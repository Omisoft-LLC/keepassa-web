package com.omisoft.keepassa.dto.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.omisoft.keepassa.entities.history.HistoryData;
import java.util.Date;
import lombok.Data;

/**
 * HistoryDTO
 * Created by dido on 27.01.17.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HistoryDTO {

  private String username;
  /**
   * Real name
   */
  private String name;


  private String ipAddress;

  private String action;


  private Date loginDate;

  private Date actionDate;

  public HistoryDTO(HistoryData data) {
    username = data.getUsername();
    action = data.getAction();
    ipAddress = data.getIpAddress();
    loginDate = data.getLoginDate();
    actionDate = new Date(data.getTimestamp());
    name = data.getName();
  }

}
