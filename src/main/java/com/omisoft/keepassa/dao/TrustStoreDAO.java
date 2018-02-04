package com.omisoft.keepassa.dao;

import com.omisoft.keepassa.authority.UserAuthority;
import com.omisoft.keepassa.configuration.FileConfigService;
import com.omisoft.keepassa.constants.Constants;
import com.omisoft.keepassa.dto.LoggedUserInfo;
import com.omisoft.keepassa.dto.rest.CertInTrustStoreDTO;
import com.omisoft.keepassa.entities.users.User;
import com.omisoft.keepassa.exceptions.SecurityException;
import com.omisoft.keepassa.structures.SecureString;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;

/**
 * Modify truststore entries Created by dido on 24.01.17.
 */
@Slf4j
public class TrustStoreDAO {

  private final UserAuthority authority;
  private final UserDAO userDAO;
  private final CompanyDAO companyDAO;

  @Inject
  public TrustStoreDAO(
      UserAuthority authority, UserDAO userDAO, CompanyDAO companyDAO) throws SecurityException {
    this.authority = authority;
    this.userDAO = userDAO;
    this.companyDAO = companyDAO;
  }


//  /**
//   * Upload ordinary certificate
//   */
//  public X509Certificate uploadCsr(byte[] data) throws SecurityException {
//    X509Certificate certificate = generateCertificateFromCsr(data);
//    try {
//      storeCertificateInTrustedStore(certificate);
//    } catch (CertificateException | IOException | KeyStoreException | NoSuchAlgorithmException e) {
//      e.printStackTrace();
//      throw new SecurityException(e);
//    }
//    return certificate;
//
//  }

  public boolean checkIfCertificateIsValid(HttpServletRequest request,
      X509Certificate certificate) {
    try {
      certificate.checkValidity();
      X500Name x500name = new JcaX509CertificateHolder(certificate).getSubject();
      RDN cn = x500name.getRDNs(BCStyle.CN)[0];
      String certificateEmail = IETFUtils.valueToString(cn.getFirst().getValue());

      LoggedUserInfo dto = authority.getUser(request.getHeader(Constants.AUTHORIZATION_HEADER));
      if (!dto.getEmail().equalsIgnoreCase(certificateEmail)) {
        return false;
      }
      User user = userDAO.findUserByEmailWithNull(dto.getEmail());
      X509Certificate trustStoreCertificate = getCertificateByAlias(
          certificate.getSerialNumber().toString());
      trustStoreCertificate.checkValidity();
      if (!trustStoreCertificate.getSerialNumber().equals(certificate.getSerialNumber())) {
        return false;
      }
      if (user != null && StringUtils.isNotEmpty(user.getMutualSslOTPKey())) {
        return true;
      } else {
        return false;
      }
    } catch (Exception e) {
      log.error("ERROR VALIDATING CERT:", e);
      return false;
    }
  }

  private void storeCertificateInTrustedStore(X509Certificate certificate)
      throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
    addEntryToTrustStore(certificate);
  }

  private synchronized void addEntryToTrustStore(X509Certificate certificate)
      throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
    KeyStore ks = KeyStore.getInstance(Constants.TRUSTSTORE_FORMAT);
    ks.load(new FileInputStream(Constants.CLIENT_TRUSTSTORE),
        FileConfigService.getInstance().getConfig().getTruststorePassword().toCharArray());
    ks.setCertificateEntry(certificate.getSerialNumber().toString(), certificate);
    FileOutputStream fos = new FileOutputStream(Constants.CLIENT_TRUSTSTORE);

    ks.store(fos,
        FileConfigService.getInstance().getConfig().getTruststorePassword().toCharArray());
    fos.close();
  }

//  /**
//   * File must be der encoded
//   */
//  private X509Certificate generateCertificateFromCsr(byte[] crtFile)
//      throws SecurityException {
//    try {
//      PKCS10CertificationRequest certificationRequest = new PKCS10CertificationRequest(crtFile);
//
//      return CertAndKeyUtils
//          .signCsr(certificationRequest, secureKeystoreSystemService.getCaPrivateKey());
//
//
//    } catch (Exception e) {
//      log.error("ERROR IN TRUSTSTORE", e);
//      throw new SecurityException("Error working with truststore", e);
//    }


//  }

  /**
   * Get All certificates
   */
  public List<X509Certificate> getAllCertificates(SecureString password)
      throws SecurityException {
    KeyStore keystore;
    List<X509Certificate> certList = new ArrayList<>();

    try {
      keystore = KeyStore.getInstance(Constants.TRUSTSTORE_FORMAT);
      FileInputStream in = new FileInputStream(Constants.CLIENT_TRUSTSTORE);
      keystore.load(in, password.toCharArray());
      in.close();
      Enumeration<String> a = keystore.aliases();
      while (a.hasMoreElements()) {
        certList.add((X509Certificate) keystore.getCertificate(a.nextElement()));

      }

    } catch (Exception e) {
      log.error("Error listing truststore", e);
      throw new SecurityException(e);
    }
    return certList;


  }

  /**
   * Get all certificates aliases
   */
  public List<String> getAllCertificatesAliases(SecureString password)
      throws SecurityException {
    KeyStore keystore;
    List<String> certList = new ArrayList<>();

    try {
      keystore = KeyStore.getInstance(Constants.TRUSTSTORE_FORMAT);
      FileInputStream in = new FileInputStream(Constants.CLIENT_TRUSTSTORE);
      keystore.load(in, password.toCharArray());
      in.close();
      Enumeration<String> a = keystore.aliases();

      while (a.hasMoreElements()) {
        certList.add(a.nextElement());

      }

    } catch (Exception e) {
      log.error("Error listing truststore", e);
      throw new SecurityException(e);

    }

    return certList;
  }

  /**
   * Get All certificates
   */
  public List<CertInTrustStoreDTO> getAllCertificatesForWeb(SecureString password)
      throws SecurityException {
    KeyStore keystore;
    List<CertInTrustStoreDTO> certList = new ArrayList<>();
    try {
      keystore = KeyStore.getInstance(Constants.TRUSTSTORE_FORMAT);
      FileInputStream in = new FileInputStream(Constants.CLIENT_TRUSTSTORE);
      keystore.load(in, password.toCharArray());
      in.close();
      Enumeration<String> a = keystore.aliases();
      while (a.hasMoreElements()) {
        String alias = a.nextElement();
        log.info(alias);
        X509Certificate cert = (X509Certificate) keystore.getCertificate(alias);
        X500Name x500name = new JcaX509CertificateHolder(cert).getSubject();
        RDN cn = x500name.getRDNs(BCStyle.CN)[0];
        String certCN = IETFUtils.valueToString(cn.getFirst().getValue());
        certList
            .add(new CertInTrustStoreDTO(certCN, cert.getNotBefore(), cert.getNotAfter(), alias));
      }
    } catch (Exception e) {
      log.error("Error listing truststore", e);
      throw new SecurityException(e);
    }
    return certList;
  }

  public void revokeCertificate(String alias, SecureString password) {
    KeyStore keystore;
    try {
      keystore = KeyStore.getInstance(Constants.TRUSTSTORE_FORMAT);
      FileInputStream in = new FileInputStream(Constants.CLIENT_TRUSTSTORE);
      keystore.load(in, password.toCharArray());
      log.info(alias);
      log.info(keystore.getCertificate(alias).toString());
      in.close();
      keystore.deleteEntry(alias);
      FileOutputStream fos = new FileOutputStream(Constants.CLIENT_TRUSTSTORE);
      keystore.store(fos, password.toCharArray());
      fos.close();
    } catch (Exception e) {
      log.error("Error removing from truststore", e);
    }
  }

  public X509Certificate getCertificateByAlias(String serialNumber)
      throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
    KeyStore ks = KeyStore.getInstance(Constants.TRUSTSTORE_FORMAT);
    ks.load(new FileInputStream(Constants.CLIENT_TRUSTSTORE),
        FileConfigService.getInstance().getConfig().getTruststorePassword().toCharArray());
    X509Certificate certificate = (X509Certificate) ks.getCertificate(serialNumber);
    return certificate;
  }
}
