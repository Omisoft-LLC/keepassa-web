package com.omisoft.keepassa.dao;

import com.omisoft.keepassa.entities.passwords.PasswordSafe;
import com.omisoft.keepassa.entities.passwords.UserWithAES;
import com.omisoft.keepassa.entities.users.User;
import com.omisoft.server.common.dao.BaseDAO;
import com.omisoft.server.common.exceptions.DataBaseException;
import lombok.NonNull;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.omisoft.keepassa.entities.passwords.PasswordSafe.FIND_PASSWORD_BY_APP_ID;

/**
 * Password safe dao Created by leozhekov on 10/28/16.
 */
public class PasswordSafeDAO extends BaseDAO<PasswordSafe> {

  private final UserWithAesDAO userWithAesDAO;

  @Inject
  public PasswordSafeDAO(UserWithAesDAO userWithAesDAO) {
    super(PasswordSafe.class);
    this.userWithAesDAO = userWithAesDAO;

  }


  /**
   * Find all password safes  by appId
   */
  public Set<PasswordSafe> findByAppId(@NonNull User user, @NonNull String appId) {
    Set<PasswordSafe> passwords = new HashSet<>();
    if (user != null) {
      EntityManager em = getEntityManager();
      Query q = em.createNamedQuery(FIND_PASSWORD_BY_APP_ID);
      q.setParameter("user", user);
      q.setParameter("appId", appId);
      List<PasswordSafe> ps = q.getResultList();
      passwords.addAll(ps);
    }
    return passwords;
  }

  public Set<PasswordSafe> findPersonalSafes(User user) throws DataBaseException {
    EntityManager entityManager = getEntityManager();
    try {
      Query q = entityManager.createQuery(
          "select p from PasswordSafe p where p.creator=:user and p.inGroup=false and p.inShares=false");
      q.setParameter("user", user);
      List<PasswordSafe> passwordSafes = q.getResultList();
      Set<PasswordSafe> finalSafes = new HashSet<>(passwordSafes);
      return finalSafes;
    } catch (Exception e) {
      throw new DataBaseException(e);
    }
  }

  public PasswordSafe findByEncryptId(UUID uuid) throws DataBaseException {
    EntityManager entityManager= getEntityManager();
    try {
      Query q = entityManager
          .createQuery("select p from PasswordSafe p where p.passwordEncryptId=:passwordEncript");
      q.setParameter("passwordEncript", uuid);
      PasswordSafe passwordSafe = (PasswordSafe) q.getSingleResult();
      return passwordSafe;
    } catch (Exception e) {
      throw new DataBaseException(e);
    }
  }

  @Override
  public void remove(PasswordSafe passwordSafe) throws DataBaseException {
    List<UserWithAES> allUA = getAllUAForPassword(passwordSafe);
    for (UserWithAES ua : allUA
        ) {
      userWithAesDAO.remove(ua);
    }
    super.remove(passwordSafe);
  }

  private List<UserWithAES> getAllUAForPassword(PasswordSafe passwordSafe) {
    EntityManager session = getEntityManager();
    Query q = session.createQuery("select ua from UserWithAES ua where passwordSafe=:passwordSafe");
    q.setParameter("passwordSafe", passwordSafe);
    return (List<UserWithAES>) q.getResultList();
  }

  public List findSafeByUrl(UUID id, String url) {
    EntityManager session = getEntityManager();
    Query q = session.createQuery(
        "select p from User u  left join u.passwordSafes as p where u.id=:id and p.url=:url");
    q.setParameter("id", id);
    q.setParameter("url", url);
    return q.getResultList();
  }
}
