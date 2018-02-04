package com.omisoft.keepassa.dao;

import static com.omisoft.keepassa.constants.Constants.LDAP;
import static com.omisoft.keepassa.constants.Constants.LDAP_DOMAIN_NAME;
import static com.omisoft.keepassa.constants.Constants.LDAP_GROUP;
import static com.omisoft.keepassa.constants.Constants.LDAP_PASSWORD;
import static com.omisoft.keepassa.constants.Constants.LDAP_SERVER;
import static com.omisoft.keepassa.constants.Constants.LDAP_USER;

import com.google.inject.Inject;
import com.omisoft.keepassa.entities.settings.Settings;
import com.omisoft.keepassa.exceptions.NotFoundException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by nslavov on 2/3/17.
 */
@Slf4j
public class LdapDAO {

  private static final String INITIAL_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
  private static final String SECURITY_AUTHENTICATION = "simple";
  private static final String ldapProtocol = "ldap://";
  private static final String DISPLAY_NAME = "displayName";
  private static final String GIVEN_NAME = "givenName";
  private static final String NAME = "name";
  private static final String MEMBER_OF = "memberOf";
  private static final String USER_PRINCIPAL_NAME = "userPrincipalName";
  private static final String S_AM_ACCOUNT_NAME = "sAMAcocountName";
  private static final String MAIL = "mail";
  // private static final String SEARCH_BASE = "CN=Users,DC=OMISOFT,DC=local";
  private static String SEARCH_BASE = "";
  private Settings ldap;
  private Settings ldapServer;
  private Settings ldapUser;
  private Settings ldapPassword;
  private Settings ldapGroup;
  private Settings ldapDomainName;
  private DirContext context;


  @Inject
  private SettingsDAO settingsDAO;

  private void fetchSettings() {
    try {
      List<Settings> settingss = settingsDAO.findLdapSettings();
      for (Settings s : settingss) {
        switch (s.getName()) {
          case LDAP:
            ldap = s;
            break;
          case LDAP_SERVER:
            ldapServer = s;
            break;
          case LDAP_USER:
            ldapUser = s;
            break;
          case LDAP_PASSWORD:
            ldapPassword = s;
            break;
          case LDAP_GROUP:
            ldapGroup = s;
            break;
          case LDAP_DOMAIN_NAME:
            ldapDomainName = s;
            break;
        }

      }
    } catch (NotFoundException e) {
      e.printStackTrace();
    }
  }

  public boolean ldapIsActive() {
    fetchSettings();
    if (ldap == null || ldap.getValue() == null || StringUtils.isBlank(ldap.getValue())) {
      log.info("WARNING LDAP SETTINGS not found !!");
      return false;
    } else {
      return ldap.getValue().equals("true");
    }

  }

  private void initContext() throws NamingException {
    fetchSettings();
    SEARCH_BASE = String.format("CN=Users,DC=%s,DC=local", ldapDomainName.getValue());
    Hashtable<String, String> env = new Hashtable<>();
    env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
    env.put(Context.SECURITY_AUTHENTICATION, SECURITY_AUTHENTICATION);
    env.put(Context.PROVIDER_URL, ldapProtocol + ldapServer.getValue());
    env.put(Context.SECURITY_PRINCIPAL, ldapUser.getValue());
    env.put(Context.SECURITY_CREDENTIALS, ldapPassword.getValue());
    this.context = new InitialDirContext(env);
  }

  public boolean isExist(String email) throws NamingException {
    if (context == null) {
      initContext();
    }
    SearchControls searchControls = new SearchControls();
    searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    NamingEnumeration<SearchResult> searchResults =
        context.search(SEARCH_BASE, String.format("(%s=%s)", MAIL, email), searchControls);
    if (!searchResults.hasMore()) {
      return false;
    }

    SearchResult searchResult = searchResults.next();
    Attributes attributes = searchResult.getAttributes();
    Attribute attribute = attributes.get(MAIL);
    String user = (String) attribute.get();
    return user != null && user.equals(email);
  }

  public boolean memberOf(String email) throws NamingException {
    if (context == null) {
      initContext();
    }

    List<String> result = new ArrayList<>();
    String emailFilter = String.format("(%s=%s)", MAIL, email);
    NamingEnumeration<SearchResult> searchResults = context.search(SEARCH_BASE, emailFilter, null);
    if (!searchResults.hasMore()) {
      return false;
    }
    SearchResult searchResult = searchResults.next();
    Attributes attributes = searchResult.getAttributes();
    Attribute attribute = attributes.get("distinguishedName");
    String user = (String) attribute.get();
    attributes = context.getAttributes(user, new String[]{MEMBER_OF});
    NamingEnumeration<? extends Attribute> allAttributes = attributes.getAll();
    while (allAttributes.hasMoreElements()) {
      attribute = allAttributes.nextElement();
      int size = attribute.size();
      for (int i = 0; i < size; i++) {
        String attributeValue = (String) attribute.get(i);
        result.add(attributeValue);
      }
    }
    for (String s : result) {
      if (s.matches(ldapGroup.getValue())) {
        return true;
      }
    }
    return false;
  }


  private SearchControls getSearchControll(String[] attrIDs) {
    SearchControls ctls = new SearchControls();
    ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    if (attrIDs != null) {
      ctls.setReturningAttributes(attrIDs);
    }
    return ctls;
  }


}
