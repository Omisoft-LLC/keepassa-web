package com.omisoft.keepassa.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Holds encryption data.
 * All kes are encoded
 * Created by dido on 28.06.17.
 */
@Getter
@Setter
public class EncryptionDTO {
  private byte[] encryptedMessage;
  private byte[] encodedAESKey;
  private byte[] encodedSerpentKey;
  private byte[] encodedTwoFishKey;

}
