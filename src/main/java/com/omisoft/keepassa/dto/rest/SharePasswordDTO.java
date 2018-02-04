package com.omisoft.keepassa.dto.rest;

import com.omisoft.keepassa.structures.SecureString;
import java.util.List;
import java.util.UUID;
import lombok.Data;

/**
 * Share password dto Created by dido on 04.01.17.
 */
@Data
public class SharePasswordDTO {

  private List<String> emails;
  private UUID passwordSafeId;
  private SecureString password;
}
