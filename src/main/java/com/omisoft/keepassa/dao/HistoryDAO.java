package com.omisoft.keepassa.dao;

import com.google.inject.Inject;
import com.omisoft.keepassa.entities.history.HistoryData;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * Created by dido on 27.01.17.
 */
public class HistoryDAO {


  private final EntityManager entityManager;

  @Inject
  public HistoryDAO(EntityManager entityManagerProvider) {
    this.entityManager = entityManagerProvider;
  }

  public List<HistoryData> getHistorybyUsername(String username) {
    Query q = entityManager.createNamedQuery(HistoryData.HISTORY_BY_USERNAME);
    q.setParameter("username", username);
    return q.getResultList();
  }

}
