package com.omisoft.keepassa.dto.feedback;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by dido on 10/21/16.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FeedbackDTO {

  @JsonIgnore
  private String ip;
  @JsonIgnore
  private String userEmail;
  @JsonIgnore
  private String browser;
  private String url;
  private String note;
  @JsonProperty("img")
  private String imageUrl;
  @JsonIgnore
  private String imageCid;
  private String datePosted;
  private String windowHeight;
  private String windowWidth;
  private String subject;
}
