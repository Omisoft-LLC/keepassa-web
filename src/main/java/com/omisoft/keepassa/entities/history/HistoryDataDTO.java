package com.omisoft.keepassa.entities.history;

import java.io.Serializable;
import lombok.Data;

/**
 * Holds revision info Created by dido on 10/7/16.
 */
@Data
public class HistoryDataDTO implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = -1619245868596241775L;

  private HistoryData historyData;
  private Long entityId;

}
