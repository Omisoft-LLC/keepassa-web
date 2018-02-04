package com.omisoft.keepassa.dto.rest;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 * Created by leozhekov on 11/18/16. Configuration dto. Used for initializing types and URL paths in
 * applications.
 */
@Data
public class ConfigurationDTO {

  private Map<String, String> restUrls;

  public ConfigurationDTO() {
    restUrls = new HashMap<>();
  }
}
