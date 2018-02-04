package com.omisoft.keepassa;

import static com.omisoft.keepassa.constants.Constants.CLOUD_MODE;
import static com.omisoft.keepassa.constants.Constants.CONFIG_FILE;
import static com.omisoft.keepassa.constants.Constants.DEV_MODE;
import static com.omisoft.keepassa.constants.Constants.HOSTED_MODE;

import com.omisoft.keepassa.configuration.Configuration;
import com.omisoft.keepassa.configuration.FileConfigService;
import com.omisoft.keepassa.constants.Constants;
import com.omisoft.keepassa.constants.RestUrl;
import com.omisoft.keepassa.di.ApplicationServletModule;
import com.omisoft.keepassa.di.DependencyModule;
import com.omisoft.keepassa.di.RestModule;
import com.omisoft.keepassa.exceptions.SecurityException;
import com.omisoft.keepassa.structures.SecureKeystore;
import com.omisoft.keepassa.structures.SecureKeystore.KeyType;
import com.omisoft.keepassa.structures.SecureString;
import com.omisoft.keepassa.ws.SignalSocket;
import com.omisoft.server.common.di.CommonModule;
import com.omisoft.server.common.di.DbModule;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Created by leozhekov on 10/28/16. Main app class that starts the server.
 */
@Slf4j
public class MainApp extends com.omisoft.server.common.microservice.MicroServiceApp {

  private static final int HTTP_PORT = 8080;
  private static final String DATE_FORMAT = "dd/MM/yyyy - HH:mm:ss";



  static {
  removeCryptographyRestrictions();
  FileInputStream input = null;
  try {
  File file = new File(CONFIG_FILE);
    Console console = System.console();
  if (file.exists()) {
    Properties prop = new Properties();



      input = new FileInputStream(file);

      prop.load(input);

      char[] systemPass = console.readPassword("Enter Keepassa Password:");
      SecureString securePass = new SecureString(systemPass);
      String salt = prop.getProperty("AUTH_KEY");
      if (BCrypt.checkpw(securePass.toString(), salt)) {
        SecureKeystore.AES_MASTER_KEY = SecureKeystore
            .decryptWithPBE(securePass, salt.getBytes(Charset.forName("UTF-8")),
                FileUtils.readFileToByteArray(new File(Constants.AES_KEY_FILE)));
        SecureKeystore.TWOFISH_MASTER_KEY = SecureKeystore
            .decryptWithPBE(securePass, salt.getBytes(Charset.forName("UTF-8")), FileUtils.readFileToByteArray(new File(Constants.AES_KEY_FILE)));
        SecureKeystore.SERPENT_MASTER_KEY = SecureKeystore
            .decryptWithPBE(securePass, salt.getBytes(Charset.forName("UTF-8")),FileUtils.readFileToByteArray(new File(Constants.AES_KEY_FILE) ));
      } else {
        console.writer().print("Wrong password! Exiting...");
        System.exit(1);
      }


  } else {
    Properties props = new Properties();
    SecureString masterPass;
    String salt;
    while (true) {
       masterPass = new SecureString(
          console.readPassword("Please, enter new master password for Keepassa:"));
      SecureString confirmPass = new SecureString(
          console.readPassword("Please, confirm master password for Keepassa:"));
      if (masterPass.equals(confirmPass)) {
        salt = BCrypt.gensalt();
       break;
      } else {
        System.out.println("Passwords do not match! Try again");
      }
    }
    // Generate master keys

    SecretKey aesKey=SecureKeystore.constructKey(KeyType.AES);
    SecretKey twoFishKey = SecureKeystore.constructKey(KeyType.AES);
    SecretKey serpentKey = SecureKeystore.constructKey(KeyType.AES);
    FileUtils.writeByteArrayToFile(new File(Constants.AES_KEY_FILE),SecureKeystore.encryptWithPBE(masterPass,salt.getBytes(Charset.forName("UTF-8")),aesKey.getEncoded()));
    FileUtils.writeByteArrayToFile(new File(Constants.TWOFISH_KEY_FILE),SecureKeystore.encryptWithPBE(masterPass,salt.getBytes(Charset.forName("UTF-8")),twoFishKey.getEncoded()));
    FileUtils.writeByteArrayToFile(new File(Constants.SERPENT_KEY_FILE),SecureKeystore.encryptWithPBE(masterPass,salt.getBytes(Charset.forName("UTF-8")),serpentKey.getEncoded()));
    props.setProperty("AUTH_KEY",salt);
  props.store(new FileWriter(new File(CONFIG_FILE)),"Generated on "+new SimpleDateFormat(DATE_FORMAT).format(new Date()));
    System.out.println("System setup done. Please, restart app!");
    System.exit(0);
  }
  } catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | InvalidAlgorithmParameterException | NoSuchPaddingException | InvalidParameterSpecException | IllegalBlockSizeException | SecurityException e) {
    e.printStackTrace();
  } finally {
    if (input != null) {
      try {
        input.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}

  public static void main(String[] args)
      throws IOException {
    MainApp mainApp = new MainApp();
    mainApp.preSetup();
    Properties prop = new Properties();
    File file = new File(CONFIG_FILE);
    if (file.exists()) {
      FileInputStream input = new FileInputStream(file);

      prop.load(input);
      input.close();
    }
    mainApp.addHttp(HTTP_PORT).addJmxSupport()
        .addDISupport(new CommonModule(), new DependencyModule(), new DbModule("keepassa", prop),
            new ApplicationServletModule(), new RestModule())
        .addWebSockets(RestUrl.WS_PATH, SignalSocket.class);

    mainApp.start("Keepassa-Web", "/home/dido/test");

  }

  private static void removeCryptographyRestrictions() {
    if (!isRestrictedCryptography()) {
      log.debug("Cryptography restrictions removal not needed");
      return;
    }
    try {
        /*
         * Do the following, but with reflection to bypass access checks:
         *
         * JceSecurity.isRestricted = false;
         * JceSecurity.defaultPolicy.perms.clear();
         * JceSecurity.defaultPolicy.add(CryptoAllPermission.INSTANCE);
         */
      final Class<?> jceSecurity = Class.forName("javax.crypto.JceSecurity");
      final Class<?> cryptoPermissions = Class.forName("javax.crypto.CryptoPermissions");
      final Class<?> cryptoAllPermission = Class.forName("javax.crypto.CryptoAllPermission");

      final Field isRestrictedField = jceSecurity.getDeclaredField("isRestricted");
      isRestrictedField.setAccessible(true);
      final Field modifiersField = Field.class.getDeclaredField("modifiers");
      modifiersField.setAccessible(true);
      modifiersField.setInt(isRestrictedField, isRestrictedField.getModifiers() & ~Modifier.FINAL);
      isRestrictedField.set(null, false);

      final Field defaultPolicyField = jceSecurity.getDeclaredField("defaultPolicy");
      defaultPolicyField.setAccessible(true);
      final PermissionCollection defaultPolicy = (PermissionCollection) defaultPolicyField
          .get(null);

      final Field perms = cryptoPermissions.getDeclaredField("perms");
      perms.setAccessible(true);
      ((Map<?, ?>) perms.get(defaultPolicy)).clear();

      final Field instance = cryptoAllPermission.getDeclaredField("INSTANCE");
      instance.setAccessible(true);
      defaultPolicy.add((Permission) instance.get(null));

      log.debug("Successfully removed cryptography restrictions");
    } catch (final Exception e) {
      log.error("Failed to remove cryptography restrictions", e);
    }
  }

  private static boolean isRestrictedCryptography() {
    // This simply matches the Oracle JRE, but not OpenJDK.
    return "Java(TM) SE Runtime Environment".equals(System.getProperty("java.runtime.name"));
  }
  @Override
  public void preSetup() {
    System.setProperty("jetty.port", String.valueOf(HTTP_PORT));
    removeCryptographyRestrictions();
    Security.insertProviderAt(new BouncyCastleProvider(), 1);
    Configuration configuration = FileConfigService.getInstance().getConfig();

    log.info("Checking jar service");

    switch (configuration.getMode()) {
      case DEV_MODE:
        Constants.MASTER_PASSWORD = configuration.getMasterPassword();
        Constants.SYSTEM_KEYSTORE_MASTER_PASSWORD = configuration.getSystemKeystoreMasterPassword();
        Constants.SYSTEM_KEYSTORE_USER_PASSWORD = configuration.getSystemKeyStoreUserPassword();
        Constants.SYSTEM_KEYSTORE_CONSTANTS_PASSWORD =
            configuration.getSystemKeystoreConstantPassword();
        log.info("IN DEV MODE");
        configuration.setInit_done(true);
        break;
      case CLOUD_MODE:
        Constants.MASTER_PASSWORD = configuration.getMasterPassword();
        Constants.SYSTEM_KEYSTORE_MASTER_PASSWORD = configuration.getSystemKeystoreMasterPassword();
        Constants.SYSTEM_KEYSTORE_USER_PASSWORD = configuration.getSystemKeyStoreUserPassword();
        Constants.SYSTEM_KEYSTORE_CONSTANTS_PASSWORD =
            configuration.getSystemKeystoreConstantPassword();
        RestUrl.SERVER_NAME = configuration.getServerUrl();
        configuration.setInit_done(true);
        log.info("IN CLOUD MODE");
        break;
      case HOSTED_MODE:
        // hosted mode, password should be entered interactively
        log.info("IN HOSTED MODE");
        // TODO , read from input
        Constants.MASTER_PASSWORD = configuration.getMasterPassword();
        Constants.SYSTEM_KEYSTORE_MASTER_PASSWORD = configuration.getSystemKeystoreMasterPassword();
        Constants.SYSTEM_KEYSTORE_USER_PASSWORD = configuration.getSystemKeyStoreUserPassword();
        Constants.SYSTEM_KEYSTORE_CONSTANTS_PASSWORD =
            configuration.getSystemKeystoreConstantPassword();
        break;
    }
  }



}
