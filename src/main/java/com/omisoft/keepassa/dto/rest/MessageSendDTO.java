package com.omisoft.keepassa.dto.rest;

import java.util.List;
import lombok.Data;

/**
 * Created by leozhekov on 1/5/17.
 */
@Data
public class MessageSendDTO {

  private String subject;
  private String message;
  private List<String> emailsTo;
}
