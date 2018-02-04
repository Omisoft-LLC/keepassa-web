package com.omisoft.keepassa.dto.rest;

import java.util.UUID;
import javax.ws.rs.FormParam;
import lombok.Data;
import org.jboss.resteasy.annotations.providers.multipart.PartType;

@Data
public class FileDTO {

  @FormParam("uploadedFile")
  @PartType("application/octet-stream")
  private byte[] data;
  @FormParam("filename")
  private String fileName;
  @FormParam("type")
  private String mimeType;
  @FormParam("name")
  private String name;
  @FormParam("id")
  private UUID id;
  @FormParam("encrypt")
  private Boolean encrypt;

  public FileDTO() {
  }

}


