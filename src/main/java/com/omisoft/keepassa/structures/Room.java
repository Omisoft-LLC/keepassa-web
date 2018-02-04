package com.omisoft.keepassa.structures;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import lombok.Data;

/**
 * WS Romm
 * Created by dido on 20.02.17.
 */
@Data
public class Room {

  private String uuid;
  private RoomEntry caller;
  private RoomEntry callee;
  private SignalMessage offerBuffer;
  private boolean init;
  private Queue<SignalMessage> iceMsgBuffer;

  public Room() {

    iceMsgBuffer = new ConcurrentLinkedDeque<>();
  }

  public RoomEntry getOtherParty(String mId) {
    if (mId.equalsIgnoreCase(caller.getUuid())) {
      return callee;
    } else {
      return caller;
    }
  }

  public void addIceMsgToBuffer(SignalMessage msg) {
    iceMsgBuffer.add(msg);
  }
}
