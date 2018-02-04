package com.omisoft.keepassa.dto.feedback;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Created by leozhekov on 1/10/17.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RedmineIssueImgDTO {

  private String token;
  private String filename;
  private String content_type;

  public RedmineIssueImgDTO(String token, String filename, String content_type) {
    this.token = token;
    this.filename = filename;
    this.content_type = content_type;
  }

  public RedmineIssueImgDTO() {
  }
}
