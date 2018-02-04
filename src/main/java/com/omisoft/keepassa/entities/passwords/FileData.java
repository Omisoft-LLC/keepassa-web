package com.omisoft.keepassa.entities.passwords;

import com.omisoft.server.common.entities.BaseEntity;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

/**
 * File data (LOB) This should be encrypted Created by dido on 9/26/16.
 */
@Getter
@Setter
@Entity
@Table(name = "file_data")
@Audited
@NamedQueries({
    @NamedQuery(name = FileData.FILE_DATA_FOR_PASSWORD, query = "select new FileData(f.id,f.name,f.filename,f.mimeType) from PasswordSafe p  left join p.attachedFiles as f where p.id=:id and p.attachedFiles is not empty")})
public class FileData extends BaseEntity {

  public static final String FILE_DATA_FOR_PASSWORD = "FILE_DATA_FOR_PASSWORD";
  private String filename;
  private String mimeType;
  private String name;
  @Basic(fetch = FetchType.LAZY)
  @NotAudited
  private byte[] content;
  private String description;
  @Column(name = "is_encrypted", columnDefinition = "BOOLEAN default true")
  private Boolean encrypt;


  public FileData() {
    encrypt = Boolean.TRUE;

  }

  public FileData(String name, String filename, String mimeType, byte[] content) {
    this.name = name;
    this.filename = filename;
    this.mimeType = mimeType;
    this.content = content;
  }

  public FileData(UUID id, String name, String filename, String mimeType) {
    this.setId(id);
    this.name = name;
    this.filename = filename;
    this.mimeType = mimeType;
  }

  // public FileData(String name,String filename, byte[] content) {
  // this.name = name;
  // this.filename = filename;
  // this.mimeType = mimeType;
  // this.content = content;
  // }

  // public DefaultStreamedContent getImageStreamContent() {
  // if (this.getContent() != null && this.mimeType != null && this.filename != null) {
  //
  // DefaultStreamedContent ds = new DefaultStreamedContent(new ByteArrayInputStream(this.content),
  // this.mimeType, this.filename);
  // return ds;
  // } else {
  // return null;
  // }
  //
  // }

  public void setContent(byte[] content) {
    this.content = content;
  }


  public void clear() {
    this.filename = null;
    this.mimeType = null;
    this.name = null;
    this.content = null;
    this.description = null;
  }

}
