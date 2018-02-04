package com.omisoft.keepassa.constants;

import com.omisoft.keepassa.structures.SecureString;

/**
 * Created by leozhekov on 11/18/16.
 */
public class Constants {
  public static final String CONFIG_FILE = "/opt/keepassa/config.properties";
  public static final String AES_KEY_FILE= "/opt/keepassa/akey";
  public static final String TWOFISH_KEY_FILE= "/opt/keepassa/tkey";
  public static final String SERPENT_KEY_FILE= "/opt/keepassa/skey";

  public static final String AUTHORIZATION_HEADER = "authorization";
  public static final String SYSTEM_MOD = "System_mod";
  public static final String DEV_MODE = "DEV";
  public static final String CLOUD_MODE = "CLOUD";
  public static final String HOSTED_MODE = "HOSTED";
  public static final String CONF_DIR = "/opt/keepassa/etc/";

  public static final String SYSTEM_CERT_PATH = "/opt/keepassa/etc/system.bsk";
  public static final String LOGS_DIR = "/opt/keepassa/logs/";
  public static final String TRUSTSTORE_FORMAT = "JKS";
  public static final String RECAPTCHA_KEY = "6LfZ0xAUAAAAAMccBpE6KBwIzQhOw8QqrYOEUtgt";
  public static final String RECAPTCHA_VERIFY = "https://www.google.com/recaptcha/api/siteverify";


  /**
   * Connections
   */

  public static final String DB_URL = "jdbc:postgresql://192.168.4.32:5432/";
  public static final String DB_NAME = "keepassa_dev";
  public static final String DB_USER = "postgres";
  public static final String DB_PASSWORD = "asdqwe123@";
  public static final String PERSISTENCE_UNIT = "keepassa";
  public static final String SYSTEM_USER = "SYSTEM";
  public static final String CA_CERT_ENTRY = "CA_CERTIFICATE";
  public static final String CA_PRIVATE_KEY_ALIAS = "CA_PRIVATE_KEY";
  public static final String SPACER = "_";
  public static final String CONF_FILE = CONF_DIR + "configuration.json";
  public static final SecureString CONSTANT_PASSWORD =
      new SecureString(new char[]{'c', '0', 'n', 'S', 'T', '@', 'N', 'T'});
  public static final String SYSTEM_IV_PATH = "/opt/keepassa/etc/system.iv";
  public static final int IV_BITS = 16;
  /**
   * QR Code stuff
   */
  public static final int QR_CODE_SIZE = 200;
  public static final String QR_CODE_FILE_PNG = "png";
  public static final String QR_CODE_FILE_JPG = "jpg";
  /**
   * Android only headers
   */
  public static final String CLIENT_ID = "CLIENT_ID";
  public static final String CLIENT_ID_VALUE = "KEEPASSAANDOIDCLIENT";
  public static final boolean IS_INIT = true;
  public static final String HTTPS_KEYSTORE = CONF_DIR + "keystore.jks";
  public static final String CLIENT_TRUSTSTORE = CONF_DIR + "clients.jks";
  public static final String USER_REDIS_DTO = "LoggedUserInfo";
  // TODO (load from singleton on first start
  public static final String SYSTEM_INIT = "SYSTEM_INIT";
  public static final String SMTP_SERVER = "SMTP_SERVER";
  public static final String SMTP_PORT = "SMTP_PORT";
  public static final String SMTP_USER = "SMTP_USER";
  public static final String SMTP_PASSWORD = "SMTP_PASSWORD";
  public static final String SMTP_TLS = "SMTP_TLS";
  public static final String MUTUAL_SSL = "MUTUAL_SSL";
  public static final String LDAP = "LDAP";
  public static final String LDAP_SERVER = "LDAP_SERVER";
  public static final String LDAP_USER = "LDAP_USER";
  public static final String LDAP_PASSWORD = "LDAP_PASSWORD";
  public static final String LDAP_GROUP = "LDAP_GROUP";
  public static final String LDAP_DOMAIN_NAME = "LDAP_DOMAIN_NAME";
  //Servlet Context Attribute
//First time setup system flag
  public static final String SETUP = "SETUP";
  /**
   * System KS. Move to properties and encrypt
   */
  public static SecureString SYSTEM_KEYSTORE_MASTER_PASSWORD =
      SecureString.EMPTY_STRING;
  public static SecureString SYSTEM_KEYSTORE_USER_PASSWORD =
      SecureString.EMPTY_STRING;
  public static SecureString SYSTEM_KEYSTORE_CONSTANTS_PASSWORD =
      SecureString.EMPTY_STRING;
  public static SecureString MASTER_PASSWORD = SecureString.EMPTY_STRING;
}
