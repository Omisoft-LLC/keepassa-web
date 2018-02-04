package com.omisoft.keepassa.dao;

import com.omisoft.keepassa.entities.users.Role;
import com.omisoft.server.common.dao.BaseDAO;

/**
 * Role DAO
 * Created by dido on 1/11/17.
 */
public class RoleDAO extends BaseDAO<Role> {

  public RoleDAO() {
    super(Role.class);
  }
}
