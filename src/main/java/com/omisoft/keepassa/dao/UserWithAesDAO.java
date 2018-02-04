package com.omisoft.keepassa.dao;

import com.omisoft.keepassa.entities.passwords.PasswordSafe;
import com.omisoft.keepassa.entities.passwords.UserWithAES;
import com.omisoft.server.common.dao.BaseDAO;
import com.omisoft.server.common.exceptions.DataBaseException;
import javax.persistence.EntityManager;

/**
 * Created by leozhekov on 2/10/17.
 */

public class UserWithAesDAO extends BaseDAO<UserWithAES> {

  public UserWithAesDAO() {
    super(UserWithAES.class);
  }

  public UserWithAES findByPasswordSafeAndEmail(PasswordSafe passwordSafe, String email)
      throws DataBaseException {
    EntityManager session = getEntityManager();
    try {
      javax.persistence.Query query = session.createQuery(
          "select ua from UserWithAES ua where ua.passwordSafe=:passwordSafe and email=:email");
      query.setParameter("passwordSafe", passwordSafe);
      query.setParameter("email", email);
      return (UserWithAES) query.getSingleResult();
    } catch (Exception e) {
      throw new DataBaseException(e);
    }
  }
}
