package com.omisoft.keepassa.entities.passwords;

import com.omisoft.keepassa.structures.SecureKeystore;
import com.omisoft.server.common.entities.BaseEntity;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

/**
 * Key repository table holds encrypted keys which are coppied to destination after a user logs in
 * Created by dido on 27.12.16.
 */
@Getter
@Setter
@Slf4j
@Table(name = "KEY_REPOSITORY")
@Entity
@Audited
@NamedQueries({
    @NamedQuery(name = KeyRepoEntry.FIND_KEYS_BY_RECIPEINT, query = "select k from KeyRepoEntry k where k.recipentUUID=:recipientUUID order by k.createdOn ASC")})
public class KeyRepoEntry extends BaseEntity {

  public static final String FIND_KEYS_BY_RECIPEINT = "FIND_KEYS_BY_RECIPEINT";
  @NotAudited
  private byte[] encryptedKey;

  @NotAudited
  private byte[] encryptedAlias;

  private UUID recipentUUID;

  private UUID senderUUID;

  private UUID groupUUID;
  @NotAudited
  @Enumerated(EnumType.STRING)
  private SecureKeystore.KeyType keyType;
  @Enumerated
  private OperationType operationType;

  public KeyRepoEntry() {

  }

  public KeyRepoEntry(byte[] encryptedAlias, byte[] encryptedKey, SecureKeystore.KeyType keyType,
      UUID senderUUID, UUID recipentUUID, OperationType operation) {
    this.encryptedAlias = encryptedAlias;
    this.encryptedKey = encryptedKey;
    this.keyType = keyType;
    this.senderUUID = senderUUID;
    this.recipentUUID = recipentUUID;
    this.operationType = operation;

  }
}
