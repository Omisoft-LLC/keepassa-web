package com.omisoft.keepassa.entities.users;

import com.omisoft.server.common.entities.BaseEntity;
import java.util.Set;
import java.util.UUID;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by dido on 05.05.17.
 */
@Getter
@Setter
@Entity
public class CompanyInvite extends BaseEntity {

  private UUID companyId;
  private String inviteCode;
  @ElementCollection
  private Set<String> invitedEmails;

}
