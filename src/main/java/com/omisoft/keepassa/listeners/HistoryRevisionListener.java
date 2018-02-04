package com.omisoft.keepassa.listeners;

import static com.omisoft.keepassa.utils.Utils.getSystemUser;

import com.omisoft.keepassa.dto.UserInfoDTO;
import com.omisoft.keepassa.entities.history.HistoryData;
import com.omisoft.keepassa.utils.InjectorHolder;
import java.io.Serializable;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.EntityTrackingRevisionListener;
import org.hibernate.envers.RevisionType;


/**
 * Revision Listener in order to show historic data and populate RevisionData entity
 */
@Slf4j
public class HistoryRevisionListener implements EntityTrackingRevisionListener {


  public HistoryRevisionListener() {
    super();
    log.info("INITING AUDIT");
    // this is not created by guice but somehow instatiated by hibernate envers
    // therefore injection does not work out of the box
    // the following statement injects manually all members in this.
    // TODO find a better way to integrate this in guice

  }

  /**
   * This method is invoked when a new revision is saved to the DB. It should populate the main
   * revision entity bean with additional user history data.
   *
   * @param revEntity The main revision entity bean
   */
  @Override
  public void newRevision(Object revEntity) {

    UserInfoDTO userInfoDTO = null;
    if (InjectorHolder.getInjector() != null) {
      try {
        userInfoDTO = InjectorHolder.getInjector().getInstance(UserInfoDTO.class);
      } catch (com.google.inject.ProvisionException e) {
        // do nothing, we dont have request or session
      }
    }
    HistoryData historyData = (HistoryData) revEntity;

    if (userInfoDTO == null) { // We have system process
      userInfoDTO = getSystemUser();
    }
    populateAdditionalRevisionData(historyData, userInfoDTO);
    userInfoDTO.setLastAction(null);

  }


  @SuppressWarnings("rawtypes")
  @Override
  public void entityChanged(Class arg0, String entityName, Serializable entityId,
      RevisionType revisionType, Object revEntity) {
    UserInfoDTO userInfoDTO = null;
    if (InjectorHolder.getInjector() != null) {
      try {
        userInfoDTO = InjectorHolder.getInjector().getInstance(UserInfoDTO.class);
      } catch (com.google.inject.ProvisionException e) {
        // do nothing, we dont have request or session
      }
    }
    HistoryData revData = (HistoryData) revEntity;
    revData.setOperationType(revisionType.toString());

    try {
      if (userInfoDTO == null) {
        userInfoDTO = getSystemUser();
      }
      populateAdditionalRevisionData(revData, userInfoDTO);
      userInfoDTO.setLastAction(null);
    } catch (Exception e) {
      log.error("GENERIC EXCEPTION", e);

      log.error("Exception while populating revision data", e);
    }

  }

  private void populateAdditionalRevisionData(HistoryData revData, UserInfoDTO user) {
    revData.setUsername(user.getEmail());
    revData.setIpAddress(user.getIpAddress());
    revData.setLoginDate(user.getLoginDate());
    revData.setAction(user.getLastAction());

  }
}


