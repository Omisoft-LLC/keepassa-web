package com.omisoft.keepassa.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.omisoft.keepassa.constants.RestUrl;
import com.omisoft.keepassa.di.GuiceWebSocketConfigurator;
import com.omisoft.keepassa.structures.Room;
import com.omisoft.keepassa.structures.RoomEntry;
import com.omisoft.keepassa.structures.RoomPool;
import com.omisoft.keepassa.structures.SignalMessage;
import com.omisoft.server.common.interfaces.WebSocket;
import java.io.IOException;
import java.util.Queue;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;

/**
 * Web Socket used for signalling
 * Created by dido on 20.02.17.
 */
@ServerEndpoint(value = RestUrl.SIGNAL_ENDPOINT, configurator = GuiceWebSocketConfigurator.class)
@Slf4j
public class SignalSocket implements WebSocket{

  private final RoomPool roomPool;
  private final ObjectMapper mapper;

  @Inject
  public SignalSocket(RoomPool roomPool, ObjectMapper mapper) {
    log.info("INITING SOCKET");
    this.roomPool = roomPool;
    this.mapper = mapper;
  }

  @OnOpen
  public void onWebSocketConnect(Session sess) {
    sess.setMaxIdleTimeout(15 * 60 * 1000 * 60);
    log.info("Signal Socket Connected: " + sess);
    log.info("ROOM SIZE" + String.valueOf(roomPool.size()));
  }

  @OnMessage
  public void onWebSocketText(String message, Session session) throws IOException {

    SignalMessage msg = mapper.readValue(message, SignalMessage.class);
    switch (msg.getType()) {
      case OFFER: {
        Room newRoom = new Room();
        newRoom.setUuid(msg.getRoomId());
        log.info("ROOM ID:" + msg.getRoomId());
        newRoom.setOfferBuffer(msg);
        newRoom.setCaller(new RoomEntry(msg.getSender(), session));
        roomPool.addRoom(msg.getRoomId(), newRoom);

        break;
      }
      case ANSWER: {
        log.info("ANSWER");
        Room room = roomPool.getRoom(msg.getRoomId());
        if (hasCredentials(room.getCaller(), room.getCallee())) {
          log.info(mapper.writeValueAsString(msg));
          sendMessage(roomPool.getRoom(msg.getRoomId()).getCaller().getSession(), msg);
          break;
        } else {
          session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY,
              "Unauthorized for this room"));

        }
      }
      case ICE: {
        Room room = roomPool.getRoom(msg.getRoomId());
        if (room != null && room.isInit()) {
          sendMessage(roomPool.getRoom(msg.getRoomId()).getOtherParty(msg.getSender()).getSession(),
              msg);
        } else {
          if (room != null) {
            room.addIceMsgToBuffer(msg);
          }
        }
        break;
      }

      case GET_OFFER: {
        Room room = roomPool.getRoom(msg.getRoomId());
        if (room != null) {
          RoomEntry callee = new RoomEntry(msg.getSender(), session);
          if (hasCredentials(room.getCaller(), callee)) {
            room.setCallee(new RoomEntry(msg.getSender(), session));
            room.setInit(true);

            if (room.getOfferBuffer() != null) {

              room.setInit(true);

              sendMessage(session, room.getOfferBuffer());
              Queue<SignalMessage> queue = room.getIceMsgBuffer();
              SignalMessage iceMsg;
              while ((iceMsg = queue.poll()) != null) {
                sendMessage(session, iceMsg);
              }
              room.getIceMsgBuffer().clear();
              room.setOfferBuffer(null);

            }
          } else {
            session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY,
                "Unauthorized for this room"));
          }
        }
        break;
      }
      case PING: {
        // Do nothing
        break;
      }
      default:
        log.info("MSG NOT DEFINED");
    }

  }

  private boolean hasCredentials(RoomEntry caller, RoomEntry callee) {
    String callerEmail = caller.getUuid().substring(caller.getUuid().indexOf("_") + 1);
    String calleeEmail = callee.getUuid().substring(callee.getUuid().indexOf("_") + 1);
    return callerEmail.equalsIgnoreCase(calleeEmail);
  }


  @OnClose
  public void onWebSocketClose(Session session, CloseReason reason) {
    try {
      Room room = roomPool.getRoomBySession(session);
      if (room != null && room.getCaller() != null && room.getCaller().getSession() != null) {
        if (!room.getCaller().getSession().isOpen()) {
          room.getCallee().getSession().close();
        } else {
          room.getCaller().getSession().close();
        }

        roomPool.removeRoom(room.getUuid());
        log.info("Remove room");
      }


    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @OnError
  public void onWebSocketError(Throwable cause) {

  }

  private void sendMessage(Session sess, String message) {
    if (sess != null && sess.isOpen()) {
      sess.getAsyncRemote().sendText(message);
    } else {
      log.error("SESSION CLOSED !!!!");
    }

  }

  private void sendMessage(Session sess, SignalMessage message) {
    try {
      sendMessage(sess, mapper.writeValueAsString(message));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

  }

}
