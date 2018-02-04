package com.omisoft.keepassa.dto.feedback;

import lombok.Data;

/**
 * Created by leozhekov on 1/10/17.
 */
@Data
public class RedmineIssueObjDTO {

  private RedmineIssueDTO issue;

  public RedmineIssueObjDTO(RedmineIssueDTO issue) {
    this.issue = issue;
  }

  public RedmineIssueObjDTO() {

  }
}
