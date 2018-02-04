package com.omisoft.keepassa.entities.settings;

import com.omisoft.server.common.entities.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by dido on 13.12.16.
 */
@Entity
@Table(name = "settings")
@Getter
@Setter
@NamedQueries({
    @NamedQuery(name = Settings.LDAP_SETTINGS, query = "Select s from Settings s where s.name=:ldap or s.name=:ldapServer or s.name=:ldapUser or s.name=:ldapPass or s.name=:ldapGroup or s.name=:ldapDomainName"),
    @NamedQuery(name = Settings.FIND_SETTINGS_BY_NAME, query = "Select s from Settings s where s.name=:name")})
public class Settings extends BaseEntity {

  public static final String LDAP_SETTINGS = "LDAP_SETTINGS";
  public static final String FIND_SETTINGS_BY_NAME = "FIND_SETTINGS_BY_NAME";
  @Column(unique = true)
  private String name;
  private String value;
  private String defaultValue;
  private String comment;
  @Enumerated(EnumType.STRING)
  private SettingsTypeEnum typeEnum;

  public Settings() {

  }

  public Settings(String name, String value, String defaultValue, String comment,
      SettingsTypeEnum typeEnum) {
    this.name = name;
    this.value = value;
    this.defaultValue = defaultValue;
    this.comment = comment;
    this.typeEnum = typeEnum;
  }


}
