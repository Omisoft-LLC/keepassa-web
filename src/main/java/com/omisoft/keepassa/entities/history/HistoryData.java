package com.omisoft.keepassa.entities.history;

import com.omisoft.keepassa.listeners.HistoryRevisionListener;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

/**
 * Holds extra information for a revision like operation and username
 *
 * @author dido
 */
@RevisionEntity(value = HistoryRevisionListener.class)
@Entity
@Table(name = "historydata")
@Data
@NamedQueries({
    @NamedQuery(name = HistoryData.HISTORY_BY_USERNAME, query = "select h from HistoryData h where h.username=:username")})
public class HistoryData implements Serializable {

  public static final String HISTORY_BY_USERNAME = "HISTORY_BY_USERNAME";
  /**
   * No comment.
   */
  private static final transient long serialVersionUID = 145132464L;

  @RevisionTimestamp
  private long timestamp;

  @Id
  @SequenceGenerator(name = "generator", sequenceName = "S_REVISION_DATA", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
  @RevisionNumber
  private int id;


  /**
   * Username
   */
  @Column(name = "username", columnDefinition = "VARCHAR(150)", length = 150)
  private String username;
  /**
   * Real name
   */
  @Column(name = "name", columnDefinition = "VARCHAR(150)", length = 150)
  private String name;


  /**
   * operation type: create, update, delete etc.
   */
  @Column(name = "operation_type", columnDefinition = "VARCHAR(150)", length = 150)
  private String operationType;

  /**
   * user ip address
   */
  @Column(name = "ip_address", length = 50)
  private String ipAddress;

  @Column(name = "action", length = 254)
  private String action;

  /**
   * user login date
   */
  @Column(name = "login_date")
  @Temporal(TemporalType.TIMESTAMP)
  private Date loginDate;

  @Override
  public int hashCode() {
    int result = (int) (timestamp ^ (timestamp >>> 32));
    result = 31 * result + id;
    result = 31 * result + (username != null ? username.hashCode() : 0);
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (operationType != null ? operationType.hashCode() : 0);
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    HistoryData that = (HistoryData) o;

    if (timestamp != that.timestamp) {
      return false;
    }
    if (id != that.id) {
      return false;
    }
    return (username != null ? username.equals(that.username) : that.username == null) && (
        name != null ? name.equals(that.name) : that.name == null) && (operationType != null
        ? operationType.equals(that.operationType) : that.operationType == null);

  }

}
