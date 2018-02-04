package com.omisoft.keepassa.dao;

import static com.omisoft.keepassa.entities.users.Department.FIND_BY_ID_AND_COMPANY;
import static com.omisoft.keepassa.entities.users.Department.FIND_MATCHING_DEPARTMENTS;

import com.omisoft.keepassa.entities.users.Company;
import com.omisoft.keepassa.entities.users.Department;
import com.omisoft.server.common.dao.BaseDAO;
import com.omisoft.server.common.exceptions.DataBaseException;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import lombok.NonNull;

/**
 * Departments DAO
 * Created by leozhekov on 1/11/17.
 */
public class DepartmentDAO extends BaseDAO<Department> {

  public DepartmentDAO() {
    super(Department.class);
  }

  public List<Department> findMatchingDepartments(@NonNull String query, Company company)
      throws DataBaseException {
    EntityManager entityManager = getEntityManager();
    try {
      entityManager.getTransaction().begin();
      Query q = entityManager.createNamedQuery(FIND_MATCHING_DEPARTMENTS);
      q.setParameter("company", company);
      q.setParameter("search", "%" + query + "%");
      List<Department> departments = (List<Department>) q.getResultList();
      return departments;
    } catch (Exception e) {
      throw new DataBaseException(e);
    }
  }


  public Department findByIdAndCompany(UUID departmentId, Company company)
      throws DataBaseException {
    EntityManager entityManager = getEntityManager();
    try {
      Query q = entityManager.createNamedQuery(FIND_BY_ID_AND_COMPANY);
      q.setParameter("departmentId", departmentId);
      q.setParameter("company", company);
      Department department = (Department) q.getSingleResult();
      return department;
    } catch (Exception e) {
      throw new DataBaseException(e);
    }
  }

  public List<Department> findAllByCompany(Company company) throws DataBaseException {
    EntityManager entityManager = getEntityManager();
    try {
      Query q = entityManager.createNamedQuery(Department.FIND_ALL_BY_COMPANY);
      q.setParameter("company", company);
      List<Department> departmentList = q.getResultList();
      return departmentList;


    } catch (Exception e) {
      throw new DataBaseException(e);
    }
  }
}
