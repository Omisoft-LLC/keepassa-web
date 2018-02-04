package com.omisoft.keepassa.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.omisoft.keepassa.structures.SecureString;
import lombok.Data;

/**
 * Created by dido on 03.01.17. Configuration file. Read from configuration.json
 */
@Data
public class Configuration {

  // CLOUD,DEV,HOSTED
  private String mode;
  private Boolean init_done;
  @JsonProperty("mp")
  private SecureString masterPassword;
  @JsonProperty("skmp")
  private SecureString systemKeystoreMasterPassword;
  @JsonProperty("skup")
  private SecureString systemKeyStoreUserPassword;
  @JsonProperty("skcp")
  private SecureString systemKeystoreConstantPassword;
  @JsonProperty("truststore_password")
  private SecureString truststorePassword;

  private String serverUrl;
}
