package com.omisoft.keepassa.dto.rest;

import java.util.List;
import java.util.UUID;
import lombok.Data;

/**
 * Created by leozhekov on 1/20/17.
 */
@Data
public class AddToDepartmentsDTO {

  private UUID id;
  private List<UUID> departments;
}
