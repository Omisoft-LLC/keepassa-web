package com.omisoft.keepassa.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omisoft.keepassa.constants.Constants;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by dido on 03.01.17.
 */
@Slf4j
public class FileConfigService {

  private static FileConfigService INSTANCE;
  private Configuration configuration;

  private FileConfigService() throws IOException {
    try (FileInputStream fis = new FileInputStream(Constants.CONF_FILE)) {

      ObjectMapper objectMapper = new ObjectMapper();
      this.configuration =
          objectMapper.readValue(fis, Configuration.class);
      log.info("CONFIGURATION:" + configuration);
    } catch (IOException e) {
      log.error("GENERIC EXCEPTION", e);
    }

  }

  public static FileConfigService getInstance() {
    if (INSTANCE == null) {
      try {
        return INSTANCE = new FileConfigService();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return INSTANCE;
  }

  public Configuration getConfig() {
    return configuration;
  }

  /**
   * Check if dev mode
   */
  public boolean isDev() {
    return Constants.DEV_MODE.equalsIgnoreCase(getConfig().getMode());
  }

  public boolean isHosted() {
    return Constants.HOSTED_MODE.equalsIgnoreCase(getConfig().getMode());
  }


  public void saveConfig() {
    try (FileOutputStream fos = new FileOutputStream(Constants.CONF_FILE)) {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.writeValue(fos, getConfig());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}