package com.omisoft.keepassa.structures;

import com.omisoft.keepassa.dto.EncryptionDTO;
import com.omisoft.keepassa.entities.passwords.PasswordSafeKey;
import com.omisoft.keepassa.exceptions.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;

import static com.omisoft.keepassa.structures.SecureKeystore.KeyType.*;


/**
 * Secure keystore, holds security entities, serialized as blob Stores all entries in BC, uber
 * keystore, and data is encrypted with AES-TWO-Fish serpent. All aliases are sha 512 digested
 * Created by dido on 19.12.16.
 */
@Slf4j
public class SecureKeystore {

  public static final String KEYSTORE_TYPE = "UBER";
  public static final String KEYSTORE_PROVIDER = "BC";

  public static final int BLOCK_BITS = 256;
  public static final String RSA_ALGORITHM = "RSA";
  private static final int RSA_KEY_SIZE = 2048;
  private static final int IV_SIZE = 16;
  public static byte[] AES_MASTER_KEY;
  public static byte[] SERPENT_MASTER_KEY;
  public static byte[] TWOFISH_MASTER_KEY;


  /**
   * This constructor is only used during serialization/deserialization
   */
  public SecureKeystore() {

  }


  /**
   * Decrypt byte array with private key stored in keystore
   */
  public static byte[] decryptWithPrivateKey(byte[] encryptedMessage, SecureString userPassword,
      PrivateKey privateKey)
      throws SecurityException {
    byte[] decryptedMessage = null;
    try {
//      PrivateKey privateKey = loadPrivateKey(PRIVATE_KEY_ALIAS, userPassword);
      Cipher cypher = Cipher.getInstance("RSA/None/OAEPWithSHA256AndMGF1Padding");
      cypher.init(Cipher.DECRYPT_MODE, privateKey);
      decryptedMessage = cypher.doFinal(encryptedMessage);
    } catch (NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException
        | NoSuchPaddingException | InvalidKeyException e) {
      e.printStackTrace();
    }
    return decryptedMessage;
  }

  /**
   * Encrypt with public key
   */
  public static byte[] encryptWithPublicKey(byte[] messageToEncrypt, PublicKey publicKey)
      throws SecurityException {
    try {
      Cipher cypher = Cipher.getInstance("RSA/None/OAEPWithSHA256AndMGF1Padding");

      cypher.init(Cipher.ENCRYPT_MODE, publicKey);
      return cypher.doFinal(messageToEncrypt);
    } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException
        | NoSuchAlgorithmException | NoSuchPaddingException e) {
      log.error("ERROR ENCRYPTING WITH PRIVATE KEY");
      throw new SecurityException(e);

    }


  }


  public static byte[] encryptWithKey(byte[] message, byte[] encodedKey, KeyType keyType)
      throws SecurityException {
    try {
      return encrypt(convertByteToSecretKey(encodedKey, keyType), message, keyType);

    } catch (NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchProviderException | NoSuchAlgorithmException e) {
      e.printStackTrace();
      throw new SecurityException(e);
    }

  }

  public static byte[] decryptWithKey(byte[] message, byte[] encodedKey, KeyType keyType)
      throws SecurityException {
    try {

      return encrypt(convertByteToSecretKey(encodedKey, keyType), message, keyType);
    } catch (NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchProviderException | NoSuchAlgorithmException e) {
      e.printStackTrace();
      throw new SecurityException(e);
    }
  }

  /**
   * Converts encoded key back to secret key
   *
   * @param keyType type of key
   */


  public static SecretKey convertByteToSecretKey(byte[] encodedKey, KeyType keyType) {
    return new SecretKeySpec(encodedKey, 0, encodedKey.length, keyType.name());
  }


  public static KeyPair generateRSAKeyPair()
      throws NoSuchProviderException, NoSuchAlgorithmException {
    SecureRandom random = new SecureRandom();
    KeyPairGenerator generator = KeyPairGenerator.getInstance(RSA_ALGORITHM, KEYSTORE_PROVIDER);
    generator.initialize(RSA_KEY_SIZE, random);
    return generator.generateKeyPair();

  }

  /**
   * Constructs keystore password
   */
  public static SecureString constructKeyStorePassword(SecureString masterPassword,
      SecureString userPassword, SecureString constPassword) {
    return masterPassword.concat(userPassword).concat(constPassword);

  }


  public static SecretKey constructKey(KeyType keyType) throws SecurityException {
    KeyGenerator keyGenerator;
    try {
      keyGenerator = KeyGenerator.getInstance(keyType.getAlgorithumName(), KEYSTORE_PROVIDER);
    } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
      log.error("ERROR GENERATING KEY", e);
      throw new SecurityException(e);
    }

    return keyGenerator.generateKey();
  }


  /**
   * Decrypt password. First call open
   */
  public static SecureString decryptPassword(EncryptionDTO encryptionDTO) throws SecurityException {

    byte[] decryptedMessage;

    try {
      //printAliases();
      SecretKey aesKey = new SecretKeySpec(encryptionDTO.getEncodedAESKey(), 0,
          encryptionDTO.getEncodedAESKey().length, KeyType.AES.name());

      SecretKey twoFishKey = new SecretKeySpec(encryptionDTO.getEncodedTwoFishKey(), 0,
          encryptionDTO.getEncodedTwoFishKey().length, KeyType.
          TWOFISH.name());
      SecretKey serpentKey = new SecretKeySpec(encryptionDTO.getEncodedSerpentKey(), 0,
          encryptionDTO.getEncodedSerpentKey().length, KeyType.
          SERPENT.name());

      // Decrypt 3 times (SERPENT, TWOFISH,AES) - in reverse order
      decryptedMessage = decrypt(serpentKey, encryptionDTO.getEncryptedMessage(), SERPENT);
      decryptedMessage = decrypt(twoFishKey, decryptedMessage, KeyType.TWOFISH);
      decryptedMessage = decrypt(aesKey, decryptedMessage, KeyType.AES);

      return new SecureString(decryptedMessage);


    } catch (NoSuchAlgorithmException | NoSuchProviderException
        | NoSuchPaddingException | InvalidKeyException
        | InvalidAlgorithmParameterException | BadPaddingException
        | IllegalBlockSizeException e) {
      log.error("ERROR DURING PASSWORD DECRYPTION", e);
      throw new java.lang.SecurityException(e);
    }

  }

  /**
   * Decrypt file
   */
  public static byte[] decryptFile(EncryptionDTO encryptionDTO)
      throws SecurityException {

    byte[] decryptedMessage;

    try {
      SecretKey aesKey = new SecretKeySpec(encryptionDTO.getEncodedAESKey(), 0,
          encryptionDTO.getEncodedAESKey().length, KeyType.AES.name());

      SecretKey twoFishKey = new SecretKeySpec(encryptionDTO.getEncodedTwoFishKey(), 0,
          encryptionDTO.getEncodedTwoFishKey().length, KeyType.
          TWOFISH.name());
      SecretKey serpentKey = new SecretKeySpec(encryptionDTO.getEncodedSerpentKey(), 0,
          encryptionDTO.getEncodedSerpentKey().length, KeyType.
          SERPENT.name());

      // Decrypt 3 times (SERPENT, TWOFISH,AES) - in reverse order
      decryptedMessage = decrypt(serpentKey, encryptionDTO.getEncryptedMessage(), KeyType.SERPENT);
      decryptedMessage = decrypt(twoFishKey, decryptedMessage, KeyType.TWOFISH);
      decryptedMessage = decrypt(aesKey, decryptedMessage, KeyType.AES);

      return decryptedMessage;


    } catch (NoSuchAlgorithmException | NoSuchProviderException
        | NoSuchPaddingException | InvalidKeyException
        | InvalidAlgorithmParameterException | BadPaddingException
        | IllegalBlockSizeException e) {
      log.error("ERROR DURING FILE DECRYPTION", e);
      throw new java.lang.SecurityException(e);
    }

  }


  /**
   * First call open Triple encoded password
   */
  public static EncryptionDTO encryptPassword(
      final SecureString passwordToEncrypt) throws SecurityException {
    byte[] encryptedMessage;
    EncryptionDTO encryptionDTO = new EncryptionDTO();

    try {
      SecretKey aesKey = constructKey(KeyType.AES);
      SecretKey twoFishKey = constructKey(KeyType.TWOFISH);
      SecretKey serpentKey = constructKey(KeyType.SERPENT);
      encryptionDTO.setEncodedAESKey(aesKey.getEncoded());
      encryptionDTO.setEncodedTwoFishKey(twoFishKey.getEncoded());
      encryptionDTO.setEncodedSerpentKey(serpentKey.getEncoded());

      // Encrypt 3 times (AES,BLOWFISH AND SERPENT
      encryptedMessage = encrypt(aesKey, passwordToEncrypt.toBytes(), KeyType.AES);
      encryptedMessage = encrypt(twoFishKey, encryptedMessage, KeyType.TWOFISH);
      encryptedMessage = encrypt(serpentKey, encryptedMessage, KeyType.SERPENT);
      encryptionDTO.setEncryptedMessage(encryptedMessage);


    } catch (NoSuchAlgorithmException | NoSuchProviderException
        | NoSuchPaddingException | InvalidKeyException
        | InvalidAlgorithmParameterException | BadPaddingException
        | IllegalBlockSizeException e) {
      log.error("ERROR DURING PASSWORD ENCRYPTION", e);
      throw new SecurityException(e);
    }

    return encryptionDTO;

  }

  /**
   * First call open. Encrypt file
   *
   * @param fileToEncrypt fule to encrypt as byte array
   * @return encrypted file byte
   */
  public static EncryptionDTO encryptWithKeys(byte[] fileToEncrypt, EncryptionDTO inputKeysDTO)
      throws SecurityException {
    byte[] encryptedMessage;

    EncryptionDTO encryptionDTO = new EncryptionDTO();

    try {

      SecretKey aesKey = convertByteToSecretKey(inputKeysDTO.getEncodedAESKey(),AES);

      SecretKey twoFishKey = convertByteToSecretKey(inputKeysDTO.getEncodedAESKey(),TWOFISH);

      SecretKey serpentKey = convertByteToSecretKey(inputKeysDTO.getEncodedAESKey(),SERPENT);

      encryptionDTO.setEncodedAESKey(aesKey.getEncoded());
      encryptionDTO.setEncodedTwoFishKey(twoFishKey.getEncoded());
      encryptionDTO.setEncodedSerpentKey(serpentKey.getEncoded());
      // Encrypt 3 times (AES,BLOWFISH AND SERPENT
      encryptedMessage = encrypt(aesKey, fileToEncrypt, KeyType.AES);
      encryptedMessage = encrypt(twoFishKey, encryptedMessage, KeyType.TWOFISH);
      encryptedMessage = encrypt(serpentKey, encryptedMessage, KeyType.SERPENT);
      encryptionDTO.setEncryptedMessage(encryptedMessage);

    } catch (NoSuchAlgorithmException | NoSuchProviderException
        | NoSuchPaddingException | InvalidKeyException
        | InvalidAlgorithmParameterException | BadPaddingException
        | IllegalBlockSizeException e) {
      log.error("ERROR DURING FILE  ENCRYPTION", e);
      throw new SecurityException(e);
    }

    return encryptionDTO;

  }

  /**
   * Actual encryption method
   */
  private static byte[] encrypt(SecretKey key, final byte[] message, KeyType keyType)
      throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException,
      InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException,
      IllegalBlockSizeException {
    SecureRandom random = new SecureRandom();
    byte[] iv = new byte[IV_SIZE];
    random.nextBytes(iv);
    // log.info("encrypt iv:" + Arrays.toString(iv));
    // log.info("ENCRYPT KEY:" + keyType.name() + ":" + Arrays.toString(key.getEncoded()));

    Cipher cipher = null;
    switch (keyType) {
      case AES: {
        cipher = Cipher.getInstance("AES/CTR/NoPadding", KEYSTORE_PROVIDER);
      }
      break;
      case TWOFISH: {
        cipher = Cipher.getInstance("Twofish/CTR/NoPadding", KEYSTORE_PROVIDER);
      }
      break;
      case SERPENT: {
        cipher = Cipher.getInstance("Serpent/CTR/NoPadding", KEYSTORE_PROVIDER);
      }
      break;
      default:
        log.info("value -> " + keyType + " - default case - do nothing -- findbugs patch");
    }
    assert cipher != null;  // to silence findbugs. Probably not good, find permanent fix.
    cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
    byte[] encryptedMessage = cipher.doFinal(message);
    byte[] concatenetad = new byte[encryptedMessage.length + IV_SIZE];
    System.arraycopy(iv, 0, concatenetad, 0, IV_SIZE);
    System.arraycopy(encryptedMessage, 0, concatenetad, IV_SIZE, encryptedMessage.length);
    log.info(keyType + " ENCRYPT IV: " + DigestUtils.sha1Hex(iv));

    return concatenetad;
  }


  private static byte[] decrypt(byte[] key, final byte[] message, KeyType keyType) throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, InvalidAlgorithmParameterException {
    return decrypt(convertByteToSecretKey(key,keyType),message,keyType);
  }
  /**
   * Decrypt message
   */
  private static byte[] decrypt(SecretKey key, final byte[] message, KeyType keyType)
      throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException,
      InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException,
      IllegalBlockSizeException {

    Cipher cipher = null;
    switch (keyType) {
      case AES: {
        cipher = Cipher.getInstance("AES/CTR/NoPadding", KEYSTORE_PROVIDER);
      }
      break;
      case TWOFISH: {
        cipher = Cipher.getInstance("Twofish/CTR/NoPadding", KEYSTORE_PROVIDER);
      }
      break;
      case SERPENT: {
        cipher = Cipher.getInstance("Serpent/CTR/NoPadding", KEYSTORE_PROVIDER);
      }
      break;
      default:
        log.info("value -> " + keyType + " - default case - do nothing -- findbugs patch");
    }

    assert cipher != null;  // to silence findbugs. Probably not good, find permanent fix.
    byte[] iv = new byte[IV_SIZE];
    byte[] originalEncryptedMessage = new byte[message.length - IV_SIZE];

    System.arraycopy(message, 0, iv, 0, IV_SIZE);
    System
        .arraycopy(message, IV_SIZE, originalEncryptedMessage, 0, originalEncryptedMessage.length);
    log.info(keyType + " DECRYPT IV: " + DigestUtils.sha1Hex(iv));

    cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
    return cipher.doFinal(originalEncryptedMessage);
  }

  public static EncryptionDTO decrtptEncryptionKeys(PasswordSafeKey key) throws SecurityException {
    EncryptionDTO encryptionDTO = new EncryptionDTO();
    try {
      encryptionDTO.setEncodedAESKey(decrypt(AES_MASTER_KEY,key.getAesKey(),KeyType.AES));
      encryptionDTO.setEncodedTwoFishKey(decrypt(TWOFISH_MASTER_KEY,key.getAesKey(),KeyType.AES));
      encryptionDTO.setEncodedSerpentKey(decrypt(SERPENT_MASTER_KEY,key.getAesKey(),KeyType.AES));
    } catch (NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException | InvalidAlgorithmParameterException | NoSuchProviderException e) {
      throw new SecurityException("Can't decrypt encryption keys",e);
    }

    return encryptionDTO;
  }


  /**
   * Supported key types, holds algorithum name
   */
  public enum KeyType {
    AES("AES"), TWOFISH("Twofish"), SERPENT("Serpent"), RSA("RSA");

    private String algorithumName;


    KeyType(String algorithumName) {
      this.algorithumName = algorithumName;
    }

    public String getAlgorithumName() {
      return algorithumName;
    }
  }

  public static byte[] encryptWithPBE(SecureString password, byte[] salt, byte[] message)
      throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException {
    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
    SecretKey tmp = factory.generateSecret(spec);
    SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
     /* Encrypt the message. */
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipher.init(Cipher.ENCRYPT_MODE, secret);
    AlgorithmParameters params = cipher.getParameters();
    SecureRandom random = new SecureRandom();
    byte[] iv = new byte[IV_SIZE];
    random.nextBytes(iv);

    byte[] encryptedMessage = cipher.doFinal(message);
    byte[] concatenetad = new byte[encryptedMessage.length + IV_SIZE];
    System.arraycopy(iv, 0, concatenetad, 0, IV_SIZE);
    System.arraycopy(encryptedMessage, 0, concatenetad, IV_SIZE, encryptedMessage.length);
    return concatenetad;
  }

  public static byte[] decryptWithPBE(SecureString password, byte[] salt, byte[] encryptedMessage)
      throws InvalidKeyException, InvalidParameterSpecException, InvalidKeySpecException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
    SecretKey tmp = factory.generateSecret(spec);
    SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    byte[] iv = new byte[IV_SIZE];
    byte[] originalEncryptedMessage = new byte[encryptedMessage.length - IV_SIZE];

    System.arraycopy(encryptedMessage, 0, iv, 0, IV_SIZE);
    System
        .arraycopy(encryptedMessage, IV_SIZE, originalEncryptedMessage, 0,
            originalEncryptedMessage.length);
    cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
    byte[] decryptedMessage = cipher.doFinal(originalEncryptedMessage);
    return decryptedMessage;
  }

}
