package com.omisoft.keepassa.tools;

import com.omisoft.keepassa.configuration.FileConfigService;
import com.omisoft.keepassa.constants.Constants;
import java.io.FileOutputStream;
import java.security.KeyStore;

/**
 * Generates a new client.jks truststore with the password that is set in the configuration.json
 * file. Created by leozhekov on 1/31/17.
 */
public class GenerateTrustStore {

  public static void main(String[] args) {
    KeyStore ks;
    try {
      ks = KeyStore.getInstance(Constants.TRUSTSTORE_FORMAT);
      ks.load(null, null);
      FileOutputStream fos = new FileOutputStream(Constants.CLIENT_TRUSTSTORE);
      ks.store(fos,
          FileConfigService.getInstance().getConfig().getTruststorePassword().toCharArray());
      fos.close();
      System.out.println("client.jks created");
      System.out.println("DONE");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
