package com.omisoft.keepassa.dao;

import com.omisoft.keepassa.entities.users.Company;
import com.omisoft.keepassa.entities.users.User;
import com.omisoft.keepassa.exceptions.NotFoundException;
import com.omisoft.keepassa.exceptions.SecurityException;
import com.omisoft.server.common.dao.BaseDAO;
import com.omisoft.server.common.exceptions.DataBaseException;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import lombok.extern.slf4j.Slf4j;


/**
 * Created by leozhekov on 10/28/16.
 */
@SuppressWarnings("unchecked")
@Slf4j
public class UserDAO extends BaseDAO<User> {

  public UserDAO() {
    super(User.class);
  }

  /**
   * Finds the user by email.
   *
   * @param email email
   * @throws NotFoundException if no user is found
   */
  public User findUserByEmailWithExc(String email) throws NotFoundException {
    EntityManager entityManager = getEntityManager();
    Query q = entityManager.createNamedQuery(User.USER_BY_EMAIL);

    q.setParameter("email", email);
    List<User> result = q.getResultList();
    if (result.size() == 1) {
      return result.get(0);
    } else {
      throw new NotFoundException("Could not find user by email: " + email);
    }

  }

  public User findUserByEmailWithNull(String email) {
    EntityManager entityManager = getEntityManager();
    Query q = entityManager.createNamedQuery(User.USER_BY_EMAIL);
    q.setParameter("email", email);
    List<User> result = q.getResultList();
    if (result.size() == 1) {
      return result.get(0);
    } else {
      return null;
    }

  }


  public void suspendUserById(UUID userId)
      throws NotFoundException, DataBaseException, com.omisoft.server.common.exceptions.NotFoundException {
    User user = findById(userId);
    user.setIsSuspended(Boolean.TRUE);
    saveOrUpdate(user);
  }

  public void suspendUser(User user) throws DataBaseException {
    user.setIsSuspended(Boolean.TRUE);
    saveOrUpdate(user);
  }

  public List<User> findMatchingUsers(String query) {
    EntityManager entityManager = getEntityManager();
    Query q = entityManager.createNamedQuery(User.FIND_USERS);
    q.setParameter("search", "%" + query + "%");
    return (List<User>) q.getResultList();

  }


  /**
   * Find user by id and company
   */
  public User findByIdAndCompany(UUID userId, Company company) throws SecurityException {
    EntityManager entityManager = getEntityManager();
    Query q = entityManager.createNamedQuery(User.FIND_USER_AND_COMPANY);
    q.setParameter("userId", userId);
    q.setParameter("company", company);
    try {
      User foundUser = (User) q.getSingleResult();
      return foundUser;
    } catch (Exception e) {
      throw new SecurityException(e);
    }
  }

  /**
   * Get all users for company
   */
  public List<User> findAllByCompany(Company company) {
    EntityManager entityManager = getEntityManager();
    Query q = entityManager.createNamedQuery(User.ALL_USERS_BY_COMPANY);
    q.setParameter("company", company);
    return q.getResultList();
  }

  public User findUserByEmailAndCompany(String email, Company company) throws DataBaseException {
    EntityManager entityManager = getEntityManager();
    try {
      Query q = entityManager.createNamedQuery(User.FIND_BY_EMAIL_AND_COMPANY);
      q.setParameter("email", email);
      q.setParameter("company", company);
      User user = (User) q.getSingleResult();
      return user;
    } catch (Exception e) {
      return null;
    }
  }
}
