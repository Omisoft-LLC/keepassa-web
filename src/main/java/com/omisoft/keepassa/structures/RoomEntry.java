package com.omisoft.keepassa.structures;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.websocket.Session;
import lombok.Data;

/**
 * Created by dido on 20.02.17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RoomEntry {

  private String uuid;
  @JsonIgnore
  private Session session;

  public RoomEntry() {

  }

  public RoomEntry(String uuid, Session session) {
    this.uuid = uuid;
    this.session = session;

  }

}
