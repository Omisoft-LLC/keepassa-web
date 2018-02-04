package com.omisoft.keepassa.dao;

import com.omisoft.keepassa.entities.users.CompanyInvite;
import com.omisoft.server.common.dao.BaseDAO;

/**
 * Company invite
 * Created by dido on 05.05.17.
 */
public class CompanyInviteDAO extends BaseDAO<CompanyInvite> {

  protected CompanyInviteDAO() {
    super(CompanyInvite.class);
  }

  public void findByInviteCode(String inviteCode) {
  }
}
