package com.omisoft.keepassa.dao;

import static com.omisoft.keepassa.entities.passwords.FileData.FILE_DATA_FOR_PASSWORD;

import com.omisoft.keepassa.entities.passwords.FileData;
import com.omisoft.server.common.dao.BaseDAO;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * File data Created by leozhekov on 10/28/16.
 */
public class FileDAO extends BaseDAO<FileData> {

  public FileDAO() {

    super(FileData.class);
  }

  public List<FileData> findFileDataByPasswordSafe(final UUID id) {
    EntityManager entityManager = getEntityManager() ;
    Query q = entityManager.createNamedQuery(FILE_DATA_FOR_PASSWORD);
//    Query q = session.createQuery("select new FileData(f.id,f.name,f.filename,f.mimeType) from PasswordSafe p  left join p.attachedFiles as f where p.id=:id and p.attachedFiles is not empty");
    q.setParameter("id", id);
    return q.getResultList();
  }
}
