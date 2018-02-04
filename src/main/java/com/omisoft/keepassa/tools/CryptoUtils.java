package com.omisoft.keepassa.tools;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import com.omisoft.keepassa.exceptions.SecurityException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.text.ParseException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Digest and encryption utils.
 * This class should be kept compatible with Android
 * Created by dido on 02.02.17.
 */

public class CryptoUtils {


  public static final String KEYSTORE_PROVIDER = "BC";
  public static final String KEYSTORE_TYPE = "UBER";
  private static final String TAG = CryptoUtils.class.getName();
  private static final int IV_SIZE = 16;
  private static final int BLOCK_BITS = 256;
  private static final String KEYSTORE_PASSWORD = "test";
  private static final String PK_ENCRYPT_ALG = "RSA/NONE/OAEPWithSHA512AndMGF1Padding";


  public static String sha512hex(String inputData) {

    return new String(Hex.encodeHex(org.apache.commons.codec.digest.DigestUtils.sha512(inputData)));
  }

  public static String sha256hex(String inputData) {

    return new String(Hex.encodeHex(org.apache.commons.codec.digest.DigestUtils.sha256(inputData)));
  }

  public static byte[] sha256(String inputData) {

    return org.apache.commons.codec.digest.DigestUtils.sha256(inputData);
  }


  public static SecretKeySpec constructAesKey(String password)
      throws NoSuchProviderException, NoSuchAlgorithmException {
    byte[] passwordHash = sha256(password);
    return new SecretKeySpec(passwordHash, "AES");
  }


  public static SecretKey constructAesKey()
      throws NoSuchProviderException, NoSuchAlgorithmException {
    KeyGenerator aesKeyGenerator = KeyGenerator.getInstance("AES", CryptoUtils.KEYSTORE_PROVIDER);
    aesKeyGenerator.init(BLOCK_BITS);
    SecretKey encryptionKey = aesKeyGenerator.generateKey();
    encryptionKey.getEncoded();
    return encryptionKey;
  }


  public static byte[] encrypt(SecretKey key, final byte[] message)
      throws SecurityException {
    SecureRandom random = new SecureRandom();
    byte[] iv = new byte[IV_SIZE];
    random.nextBytes(iv);
    // log.info("encrypt iv:" + Arrays.toString(iv));
    // log.info("ENCRYPT KEY:" + keyType.name() + ":" + Arrays.toString(key.getEncoded()));

    Cipher cipher;
    try {
      cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", KEYSTORE_PROVIDER);

      cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
      byte[] encryptedMessage = cipher.doFinal(message);
      byte[] concatenetad = new byte[encryptedMessage.length + IV_SIZE];
      System.arraycopy(iv, 0, concatenetad, 0, IV_SIZE);
      System.arraycopy(encryptedMessage, 0, concatenetad, IV_SIZE, encryptedMessage.length);

      return concatenetad;
    } catch (NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException
        | InvalidAlgorithmParameterException | BadPaddingException | NoSuchProviderException
        | NoSuchPaddingException e) {
      throw new SecurityException(e);
    }

  }

  /**
   * Decrypt message
   */
  public static byte[] decrypt(SecretKey key, final byte[] message)
      throws SecurityException {

    Cipher cipher;
    try {
      cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", KEYSTORE_PROVIDER);

      byte[] iv = new byte[IV_SIZE];
      byte[] originalEncryptedMessage = new byte[message.length - IV_SIZE];

      System.arraycopy(message, 0, iv, 0, IV_SIZE);
      System.arraycopy(message, IV_SIZE, originalEncryptedMessage, 0,
          originalEncryptedMessage.length);

      cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
      return cipher.doFinal(originalEncryptedMessage);
    } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException
        | InvalidKeyException | IllegalBlockSizeException | InvalidAlgorithmParameterException
        | NoSuchProviderException e) {
      throw new SecurityException(e);
    }
  }

  /**
   * Decrypt byte array with private key stored in keystore
   */
  public static byte[] decryptWithPrivateKey(PrivateKey privateKey, byte[] encryptedMessage)
      throws SecurityException {
    byte[] decryptedMessage = null;
    try {
      Cipher cypher = Cipher.getInstance(PK_ENCRYPT_ALG);
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
  public static byte[] encryptWithPublicKey(PublicKey publicKey, byte[] messageToEncrypt)
      throws SecurityException {
    try {
      Cipher cypher = Cipher.getInstance(PK_ENCRYPT_ALG);

      cypher.init(Cipher.ENCRYPT_MODE, publicKey);
      return cypher.doFinal(messageToEncrypt);
    } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException
        | NoSuchAlgorithmException | NoSuchPaddingException e) {
      throw new SecurityException(e);

    }


  }

  public static KeyPair generateRSAKeyPair()
      throws NoSuchProviderException, NoSuchAlgorithmException {
    SecureRandom random = new SecureRandom();
    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", KEYSTORE_PROVIDER);
    generator.initialize(2048, random);
    return generator.generateKeyPair();

  }


  public static void main(String[] args)
      throws NoSuchProviderException, NoSuchAlgorithmException, SecurityException {
    Security.addProvider(new BouncyCastleProvider());
    KeyPair kp = generateRSAKeyPair();
    String test = "test";
    SecretKey sc = constructAesKey();
    System.out.println(DigestUtils.sha512Hex(sc.getEncoded()));
    byte[] encrypted = encryptWithPublicKey(kp.getPublic(), sc.getEncoded());
    System.out.println(DigestUtils.sha512Hex(encrypted));
    byte[] decrypted = decryptWithPrivateKey(kp.getPrivate(), encrypted);
    SecretKey aesKey = new SecretKeySpec(decrypted, "AES");
    System.out.println(DigestUtils.sha512Hex(aesKey.getEncoded()));

//    System.out.println(decrypted);
    System.out.println();
  }

  public static PublicKey getPublicKey(File publicKeyFile) {
    PublicKey publicKey = null;
    try {
      BufferedReader br = new BufferedReader(new FileReader(publicKeyFile));
      String decryptedData = br.readLine();

      RSAKey rsaKey = RSAKey.parse(decryptedData);
      publicKey = rsaKey.toPublicKey();
    } catch (ParseException | JOSEException | IOException e) {
      e.printStackTrace();
    }

    return publicKey;
  }


  public static PublicKey getPublicKey(String pubKeyString) {
    PublicKey publicKey = null;
      try {
      RSAKey rsaKey = RSAKey.parse(pubKeyString);
      publicKey = rsaKey.toPublicKey();
    } catch (ParseException | JOSEException  e) {
      e.printStackTrace();
    }

    return publicKey;
  }
}