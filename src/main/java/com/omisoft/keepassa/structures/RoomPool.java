package com.omisoft.keepassa.structures;

import com.google.inject.Singleton;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.Session;
import lombok.Data;

/**
 * Room Pool
 * Created by dido on 20.02.17.
 */
@Singleton
@Data
public class RoomPool {

  private final static Map<String, Room> pool = new ConcurrentHashMap<>();

  public void addRoom(String roomId, Room room) {
    pool.put(roomId, room);
  }


  public Room getRoom(String roomId) {
    return pool.get(roomId);
  }

  public synchronized Room removeRoom(String roomId) {
    return pool.remove(roomId);
  }

  public int size() {
    return pool.size();
  }

  public Map<String, Room> getPool() {
    return pool;
  }

  public Room getRoomBySession(Session session) {
    Room room = null;
    for (Map.Entry<String, Room> entry : pool.entrySet()) {
      if (entry.getValue().getCaller().getSession() == session
          || entry.getValue().getCallee().getSession() == session) {
        room = entry.getValue();
      }
    }
    return room;
  }

  public boolean hasRoomWithId(String roomId) {
    return pool.containsKey(roomId);
  }
}
