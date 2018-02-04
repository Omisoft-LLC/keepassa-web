package com.omisoft.keepassa.dao;

import static com.omisoft.keepassa.entities.users.Company.CHECK_IF_ADMIN;

import com.omisoft.keepassa.entities.users.Company;
import com.omisoft.keepassa.entities.users.User;
import com.omisoft.server.common.dao.BaseDAO;
import com.omisoft.server.common.exceptions.DataBaseException;
import javax.persistence.EntityManager;
import javax.persistence.Query;


/**
 * Created by leozhekov on 10/28/16.
 */
@SuppressWarnings("unchecked")
public class CompanyDAO extends BaseDAO<Company> {

  public CompanyDAO() {
    super(Company.class);
  }


  public boolean isUserAdmin(User user) throws DataBaseException{
    if (user.getCompany() == null) {
      return false;
    }
    EntityManager entityManager = getEntityManager();
    try {
      Query q = entityManager.createNamedQuery(CHECK_IF_ADMIN);
      q.setParameter("userCompany", user.getCompany());
      q.setParameter("user", user);
      Long result = (Long) q.getSingleResult();

      return result > 0L;
    } catch (Exception e) {
      throw new DataBaseException(e);
    }
  }

  public boolean existsCompany(String companyName) {
    EntityManager entityManager = getEntityManager();
    Query q = entityManager.createQuery("select c from Company c where c.name=:name");
    q.setParameter("name", companyName);
    return q.getResultList().size() > 0;
  }
}
