package com.omisoft.keepassa.dto.feedback;

import java.util.List;
import lombok.Data;

/**
 * Created by leozhekov on 1/10/17.
 */
@Data
public class RedmineIssueDTO {

  private String project_id;
  private String tracker_id;
  private String status_id;
  private String subject;
  private String description;
  private List<RedmineIssueImgDTO> uploads;
}
