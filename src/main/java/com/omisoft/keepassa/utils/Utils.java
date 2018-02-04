package com.omisoft.keepassa.utils;

import com.omisoft.keepassa.constants.Constants;
import com.omisoft.keepassa.dto.UserInfoDTO;
import com.omisoft.keepassa.structures.SecureString;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.security.SecureRandom;
import java.util.Date;
import java.util.UUID;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Created by leozhekov on 11/4/16. Static class contains error response methods that get called
 * from many other methods.
 */
public class Utils {

  private static final String ALLOWED_SYMBOLS =
      "123456789AaBbCcDdEeFfGgHhIiJjKkLlMmNnPpQqRrSsTtUuVvWwXxYyZz";
  private static final String HEX_DIGITS = "0123456789abcdef";
  private static SecureRandom rnd = new SecureRandom();

//  /**
//   * Used in all the general catch blocks that catch Exception.
//   *
//   * @param url rest url
//   * @return
//   */
//  public static Response errorResponseCatchBlock(String url, String message) {
//    return Response.status(417).entity(new ErrorDTO(url, message)).build();
//  }


  /**
   * Method that generates random string from a predefined set of symbols
   *
   * @param len the length of the generated string
   * @return generated string
   */
  public static String randomString(int len) {
    StringBuilder sb = new StringBuilder(len);
    for (int i = 0; i < len; i++) {
      sb.append(ALLOWED_SYMBOLS.charAt(rnd.nextInt(ALLOWED_SYMBOLS.length())));
    }
    return sb.toString();
  }

  public static String getHostFromUrl(String url) {
    URI uri = null;
    try {
      uri = new URI(url);
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }

    String host = uri != null ? uri.getHost() : null;
    if (host == null) {
      return url;
    } else {
      return host;
    }
  }

  public static UserInfoDTO getSystemUser() {
    UserInfoDTO user = new UserInfoDTO();
    user.setFirstName(Constants.SYSTEM_USER);
    user.setLastName(Constants.SYSTEM_USER);
    user.setIpAddress("127.0.0.1");
    user.setLoginDate(new Date());
    return user;
  }

  public static String createId() {
    UUID uuid = java.util.UUID.randomUUID();
    return uuid.toString();
  }

  public static UUID createUUID() {
    UUID uuid = java.util.UUID.randomUUID();
    return uuid;
  }

  public static String toHex(byte[] data, int length) {
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i != length; i++) {
      int v = data[i] & 0xff;
      buf.append(HEX_DIGITS.charAt(v >> 4));
      buf.append(HEX_DIGITS.charAt(v & 0xf));
    }
    return buf.toString();
  }

  /**
   * Return the passed in byte array as a hex string.
   *
   * @param data the bytes to be converted.
   * @return a hex representation of data.
   */
  public static String toHex(byte[] data) {
    {
      return toHex(data, data.length);
    }
  }

  /**
   * Generate random password
   */
  public static SecureString generateRandomPassword(int length) {
    char[] chars =
        RandomStringUtils.random(length, 0, 0, true, true, null, new SecureRandom()).toCharArray();
    shuffleArray(chars); // shuffle array not to leave trace in memory
    return new SecureString(chars);
  }

  /**
   * Shuffle array
   */
  public static void shuffleArray(char[] ar) {

    for (int i = ar.length - 1; i > 0; i--) {
      int index = rnd.nextInt(i + 1);
      // Simple swap
      char a = ar[index];
      ar[index] = ar[i];
      ar[i] = a;
    }
  }

  public static long stream(InputStream input, OutputStream output) throws IOException {
    try (
        ReadableByteChannel inputChannel = Channels.newChannel(input);
        WritableByteChannel outputChannel = Channels.newChannel(output)
    ) {
      ByteBuffer buffer = ByteBuffer.allocateDirect(10240);
      long size = 0;

      while (inputChannel.read(buffer) != -1) {
        buffer.flip();
        size += outputChannel.write(buffer);
        buffer.clear();
      }

      return size;
    }
  }
}
