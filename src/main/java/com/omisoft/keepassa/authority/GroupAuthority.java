package com.omisoft.keepassa.authority;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.omisoft.keepassa.dto.GroupInfoDTO;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by leozhekov on 11/16/16.
 */
@Singleton
@Slf4j
public class GroupAuthority {

  private static final int TIMEOUT = 86400;
  private static final String TOKEN_PREFIX = "KPG_";
  private final ObjectMapper mapper;
  private Cache<String, String> groupAuthorityCache;

  @Inject
  public GroupAuthority(ObjectMapper mapper) {
    this.mapper = mapper;
    groupAuthorityCache =
        CacheBuilder.newBuilder().expireAfterWrite(TIMEOUT, TimeUnit.SECONDS).build();
  }

  public void addGroup(String token, String groupForRedis) {
    groupAuthorityCache.put(token, groupForRedis);

  }

  public GroupInfoDTO getGroupInfo(String token) {
    String group = groupAuthorityCache.getIfPresent(token);
    if (!StringUtils.isBlank(group)) {
      try {
        return mapper.readValue(group, GroupInfoDTO.class);
      } catch (IOException e) {
        log.error("ERROR MAPPING:", e);
      }
    } else {
      return null;
    }
    return null;
  }

  public boolean isExist(String token) {
    String group = groupAuthorityCache.getIfPresent(token);
    return !StringUtils.isBlank(group);
  }


  public void removeGroup(String token) {
    groupAuthorityCache.invalidate(token);
  }
}
