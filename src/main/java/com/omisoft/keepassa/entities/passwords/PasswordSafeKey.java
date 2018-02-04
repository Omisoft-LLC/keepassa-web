package com.omisoft.keepassa.entities.passwords;

import com.omisoft.keepassa.entities.users.User;
import com.omisoft.server.common.entities.BaseEntity;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

/**
 * Holds encrypted keys
 * Created by dido on 23.06.17.
 */
@Getter
@Setter
@Entity
public class PasswordSafeKey extends BaseEntity {
  @Basic(fetch = FetchType.LAZY)
  private byte[] aesKey;
  @Basic(fetch = FetchType.LAZY)
  private byte[] serpentKey;
  @Basic(fetch = FetchType.LAZY)
  private byte[] twofishKey;
  @ManyToOne
  private PasswordSafe passwordSafe;
  @ManyToOne
  private User user;

}
