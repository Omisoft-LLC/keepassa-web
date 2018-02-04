package com.omisoft.keepassa.dao;

import com.omisoft.keepassa.entities.passwords.PasswordSafeKey;
import com.omisoft.server.common.dao.BaseDAO;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.UUID;

/**
 * Password safe key DAO
 * Created by dido on 29.06.17.
 */
public class PasswordSafeKeyDAO extends BaseDAO<PasswordSafeKey> {

  protected PasswordSafeKeyDAO() {
    super(PasswordSafeKey.class);
  }
// TODO
  public PasswordSafeKey findKeysByUserAndPasswordSafe(UUID uid, UUID pid) {

    EntityManager entityManager = getEntityManager();
    Query q = entityManager.createQuery("select k from PasswordSafeKey k inner join k.passwordSafe ps inner join k.user u where u.id=:uid and ps.id=:pid");
    q.setParameter("uid", uid);
    q.setParameter("pid", pid);
    List<PasswordSafeKey> keys = q.getResultList();
    if (keys.size()==1) {
      return keys.get(0);
    } else {
      return null;
    }
  }
}
