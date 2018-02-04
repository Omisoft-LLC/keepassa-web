package com.omisoft.keepassa.dto.rest;

import com.omisoft.keepassa.enums.Severity;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 * Created by leozhekov on 11/1/16.
 */
@Data
public class ErrorDTO {

  private String message;

  private Severity severity;

  private int errorCode;

  private String err;

  private Map<String, String> validationMessages = new HashMap<>();


  /**
   * Constructs new ErrorDto.
   *
   * @param message error title
   */
  public ErrorDTO(String err, String message) {
    this.err = err;
    this.message = message;
    // this.severity = Severity.ERROR;

  }

  public ErrorDTO(String err, String message, Map<String, String> validationMessages) {
    this.err = err;
    this.message = message;
    this.validationMessages = validationMessages;
  }

  /**
   * Constructs new error dto and set severity.
   *
   * @param message error title
   * @param severity severity
   */
  public ErrorDTO(String err, String message, Severity severity) {
    this.err = err;
    this.message = message;
    this.severity = severity;
  }

  public ErrorDTO(String err, String message, Severity severity, int errorCode) {
    this.err = err;
    this.message = message;
    this.severity = severity;
    this.errorCode = errorCode;
  }
}
