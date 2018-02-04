package com.omisoft.keepassa.dto.rest;

import java.util.Date;
import lombok.Data;

/**
 * Created by leozhekov on 1/31/17.
 */
@Data
public class CertInTrustStoreDTO {

  private String name;
  private String alias;
  private Date validFrom;
  private Date validTo;

  public CertInTrustStoreDTO(String name, Date validFrom, Date validTo, String alias) {
    this.name = name;
    this.validFrom = validFrom;
    this.validTo = validTo;
    this.alias = alias;
  }

  public CertInTrustStoreDTO() {
  }

}
