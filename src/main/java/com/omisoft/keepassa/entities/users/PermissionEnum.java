package com.omisoft.keepassa.entities.users;

/**
 * Holds fine tune permissions
 * Created by dido on 10/5/16.
 */
public enum PermissionEnum {
  READ("Read"), WRITE("Write");
  private String value;

  PermissionEnum(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
