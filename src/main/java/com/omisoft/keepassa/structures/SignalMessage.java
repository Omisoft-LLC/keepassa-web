package com.omisoft.keepassa.structures;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Signaling message
 * Created by dido on 20.02.17.
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class SignalMessage {

  private MsgType type;
  private String sender;
  private String roomId;
  private String recipient;
  private Object sdp;

  public SignalMessage(MsgType type, String sender, String roomId, String recipient, Object sdp) {
    this.type = type;
    this.sender = sender;
    this.roomId = roomId;
    this.recipient = recipient;
    this.sdp = sdp;
  }

  public SignalMessage() {

  }

  /**
   * Msg Type
   */
  public enum MsgType {
    ICE, OFFER, ANSWER, PING, GET_OFFER
  }
}

