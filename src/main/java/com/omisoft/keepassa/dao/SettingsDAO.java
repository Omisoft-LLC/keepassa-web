package com.omisoft.keepassa.dao;

import static com.omisoft.keepassa.constants.Constants.LDAP;
import static com.omisoft.keepassa.constants.Constants.LDAP_DOMAIN_NAME;
import static com.omisoft.keepassa.constants.Constants.LDAP_GROUP;
import static com.omisoft.keepassa.constants.Constants.LDAP_PASSWORD;
import static com.omisoft.keepassa.constants.Constants.LDAP_SERVER;
import static com.omisoft.keepassa.constants.Constants.LDAP_USER;
import static com.omisoft.keepassa.entities.settings.Settings.FIND_SETTINGS_BY_NAME;
import static com.omisoft.keepassa.entities.settings.Settings.LDAP_SETTINGS;

import com.omisoft.keepassa.entities.settings.Settings;
import com.omisoft.keepassa.exceptions.NotFoundException;
import com.omisoft.server.common.dao.BaseDAO;
import java.util.List;
import javax.persistence.Query;

/**
 * Settings Created by leozhekov on 10/28/16.
 */
public class SettingsDAO extends BaseDAO<Settings> {

  public SettingsDAO() {

    super(Settings.class);
  }


  public Settings findSettingByName(String name) throws NotFoundException {
    Query q = getEntityManager().createNamedQuery(FIND_SETTINGS_BY_NAME);
    q.setParameter("name", name);
    List<Settings> result = q.getResultList();
    if (result.size() > 0) {
      return result.get(0);
    } else {
      throw new NotFoundException("Cannot find setting with name: " + name);
    }
  }


  public List<Settings> findLdapSettings() throws NotFoundException {
    Query q = getEntityManager().createNamedQuery(LDAP_SETTINGS);
    q.setParameter("ldap", LDAP);
    q.setParameter("ldapServer", LDAP_SERVER);
    q.setParameter("ldapUser", LDAP_USER);
    q.setParameter("ldapPass", LDAP_PASSWORD);
    q.setParameter("ldapGroup", LDAP_GROUP);
    q.setParameter("ldapDomainName", LDAP_DOMAIN_NAME);
    List<Settings> result = q.getResultList();
    if (result.size() > 0) {
      return result;
    } else {
      throw new NotFoundException("Cannot find setting with name: ");
    }
  }

}
