package com.omisoft.keepassa.entities.passwords;

import com.omisoft.keepassa.dto.rest.UserWithAESDTO;
import com.omisoft.server.common.entities.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.envers.Audited;

/**
 * Created by leozhekov on 2/7/17.
 */
@Getter
@Setter
@Entity
@Table(name = "user_with_aes")
@Audited
public class UserWithAES extends BaseEntity {

  @Column(name = "email")
  private String email;
  @Column(name = "encryptedAesKey", columnDefinition = "VARCHAR(2048)")
  private String key;
  @ManyToOne(optional = false)
  @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, CascadeType.PERSIST,
      CascadeType.MERGE})
  @JoinColumn(name = "passwordSafe_id", referencedColumnName = "id")
  private PasswordSafe passwordSafe;

  public UserWithAES(String email, String key) {
    this.email = email;
    this.key = key;
  }

  public UserWithAES() {
    super();
  }

  public UserWithAES(UserWithAESDTO userWithAESDTO) {
    this();
    this.email = userWithAESDTO.getEmail();
    this.key = userWithAESDTO.getKey();
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (email != null ? email.hashCode() : 0);
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    UserWithAES that = (UserWithAES) o;

    return email != null ? email.equals(that.email) : that.email == null;
  }

  public PasswordSafe getPasswordSafe() {
    return passwordSafe;
  }

  public void setPasswordSafe(PasswordSafe passwordSafe) {
    this.passwordSafe = passwordSafe;
  }
}
