package com.omisoft.keepassa.authority;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Singleton;
import com.omisoft.keepassa.dto.LoggedUserInfo;
import com.omisoft.keepassa.entities.users.User;
import com.omisoft.keepassa.structures.SecureString;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by nslavov on 3/25/16.
 */
@Singleton
public class UserAuthority {

  private static final int TIMEOUT = 7200;
  private static final String TOKEN_PREFIX = "KPU_";
  private final ObjectMapper mapper;
  private final Cache<String, String> loggedUsers;

  @Inject
  public UserAuthority(ObjectMapper mapper) {
    this.mapper = mapper;
    loggedUsers = CacheBuilder.newBuilder().expireAfterAccess(TIMEOUT, TimeUnit.SECONDS).build();
  }

  public void addUser(String token, User userForRedis, SecureString password)
      throws JsonProcessingException {
    LoggedUserInfo loggedUserInfo;
    if (userForRedis.getCompany() == null) {
      loggedUserInfo = new LoggedUserInfo(userForRedis.getEmail(), password, null);
    } else {
      loggedUserInfo = new LoggedUserInfo(userForRedis.getEmail(), password,
          userForRedis.getCompany().getId().toString());

    }
    loggedUsers.put(token, mapper.writeValueAsString(loggedUserInfo));

  }

  public LoggedUserInfo getUser(String token) {

    String user = loggedUsers.getIfPresent(token);
    if (!StringUtils.isBlank(user)) {
      try {
        return mapper.readValue(user, LoggedUserInfo.class);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      return null;
    }
    return null;
  }

  public boolean isExist(String token) {
    String user = loggedUsers.getIfPresent(token);
    return !StringUtils.isBlank(user);
  }


  public void removeUser(String token) {
    loggedUsers.invalidate(token);

  }
}
