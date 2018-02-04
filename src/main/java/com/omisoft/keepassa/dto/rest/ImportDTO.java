package com.omisoft.keepassa.dto.rest;

import javax.ws.rs.FormParam;
import lombok.Data;
import org.jboss.resteasy.annotations.providers.multipart.PartType;

/**
 * Transfers import info
 */
@Data
public class ImportDTO {

  @FormParam("uploadedFile")
  @PartType("application/octet-stream")
  private byte[] data;
  @FormParam("filename")
  private String fileName;
  @FormParam("type")
  private String type;
  @FormParam("encrypt")
  private Boolean encrypt;

  public ImportDTO() {
  }

}


