package com.omisoft.keepassa.dao;

import static com.omisoft.keepassa.entities.users.Company.DELETE_EMPTY_GROUP;
import static com.omisoft.keepassa.entities.users.Company.FIND_GROUPS_BY_USER;

import com.omisoft.keepassa.entities.passwords.Group;
import com.omisoft.keepassa.entities.users.User;
import com.omisoft.server.common.dao.BaseDAO;
import com.omisoft.server.common.exceptions.DataBaseException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * Actions with groups
 * Created by leozhekov on 11/4/16.
 */
public class GroupDAO extends BaseDAO<Group> {

  public GroupDAO() {
    super(Group.class);
  }

  public List<Group> findGroupsByUser(User user) {
    EntityManager entityManager = getEntityManager();
    Query q = entityManager.createNamedQuery(FIND_GROUPS_BY_USER);
    return q.getResultList();
  }

  public void deleteEmptyGroup() throws DataBaseException {

    EntityManager entityManager = getEntityManager();
    try {
      Query q = entityManager.createNamedQuery(DELETE_EMPTY_GROUP);
      q.executeUpdate();
    } catch (Exception e) {
      throw new DataBaseException(e);
    }
  }
}
