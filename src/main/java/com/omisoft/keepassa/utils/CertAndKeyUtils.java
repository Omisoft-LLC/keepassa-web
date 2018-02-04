package com.omisoft.keepassa.utils;

import static com.omisoft.keepassa.structures.SecureKeystore.KEYSTORE_PROVIDER;

import com.omisoft.keepassa.constants.Constants;
import com.omisoft.keepassa.exceptions.SecurityException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.bc.BcX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.util.encoders.Base64;


/**
 * Created by leozhekov on 10/28/16. Utils for certificates, keys and keystores.
 */
@Slf4j
public class CertAndKeyUtils {


  private static final String BEGIN_CERT = "-----BEGIN CERTIFICATE-----";
  private static final String END_CERT = "-----END CERTIFICATE-----";
  private static final String PROVIDER_NAME = BouncyCastleProvider.PROVIDER_NAME;
  private static final String SIGNATURE_ALGORITHM = "SHA256WithRSAEncryption";
  private static final int BLOCK_SIZE = 256;
  private static final String KEY_GENERATION_ALGORITHM = "RSA";
  // One year before first run
  private static final Date CA_START_DATE = new Date(System.currentTimeMillis() - 86400000L * 365);
  // After 100 years
  private static final Date CA_END_DATE =
      new Date(System.currentTimeMillis() + 86400000L * 365 * 100);
  // 10 years in mills
  private static final long VALIDITY_PERIOD = 315360000000L;
  private static final X500Name issuerName =
      new X500Name("CN=keepassa.net, O=KeepassaServer, L=Sofia, ST=Sofia, C=Bulgaria");
  private static final Random random = new Random();
  // public static X509Certificate CA_CERTIFICATE;
  // public static KeyPair CA_KEY_PAIR;
  //
  //

  /**
   * Create subject key
   */
  private static SubjectKeyIdentifier createSubjectKeyIdentifier(Key key) throws IOException {
    ASN1InputStream is = null;
    try {
      is = new ASN1InputStream(new ByteArrayInputStream(key.getEncoded()));
      ASN1Sequence seq = (ASN1Sequence) is.readObject();
      SubjectPublicKeyInfo info = SubjectPublicKeyInfo.getInstance(seq);
      return new BcX509ExtensionUtils().createSubjectKeyIdentifier(info);
    } finally {
      IOUtils.closeQuietly(is);
    }
  }

  /**
   * Generates CA certificate.
   */
  public static X509Certificate generateCACertificate(KeyPair caKeyPair) {
    // Same subject as certificate is self-signed
    BigInteger serial = BigInteger.valueOf(random.nextLong());
    X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(issuerName, serial,
        CA_START_DATE, CA_END_DATE, issuerName, caKeyPair.getPublic());
    X509Certificate cert = null;
    try {
      builder.addExtension(Extension.subjectKeyIdentifier, false,
          createSubjectKeyIdentifier(caKeyPair.getPublic()));
      builder.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
      KeyUsage usage =
          new KeyUsage(KeyUsage.keyCertSign | KeyUsage.digitalSignature | KeyUsage.keyEncipherment
              | KeyUsage.dataEncipherment | KeyUsage.cRLSign | KeyUsage.keyAgreement);
      builder.addExtension(Extension.keyUsage, false, usage);
      ASN1EncodableVector purposes = new ASN1EncodableVector();
      purposes.add(KeyPurposeId.id_kp_serverAuth);
      purposes.add(KeyPurposeId.id_kp_clientAuth);
      purposes.add(KeyPurposeId.anyExtendedKeyUsage);
      builder.addExtension(Extension.extendedKeyUsage, false, new DERSequence(purposes));
      cert = signCertificate(builder, caKeyPair.getPrivate());

    } catch (IOException | OperatorCreationException | CertificateException e) {
      e.printStackTrace();
    }

    return cert;
  }

  /**
   * Sign certificate
   */
  public static X509Certificate signCertificate(X509v3CertificateBuilder certificateBuilder,
      PrivateKey caPrivateKey) throws OperatorCreationException, CertificateException {
    ContentSigner signer = new JcaContentSignerBuilder(SIGNATURE_ALGORITHM)
        .setProvider(KEYSTORE_PROVIDER).build(caPrivateKey);
    return new JcaX509CertificateConverter().setProvider(KEYSTORE_PROVIDER)
        .getCertificate(certificateBuilder.build(signer));
  }

  /**
   * Generate IV
   */
  public static byte[] constructIV() {
    byte[] iv = new byte[Constants.IV_BITS];
    SecureRandom secureRandom = new SecureRandom();
    secureRandom.nextBytes(iv);
    log.info("GENERATED IV");
    return iv;
  }

  /**
   * Generates a V3 user certificate.
   */
  public static X509Certificate generateV3Certificate(X509Certificate caCert, KeyPair pair,
      PrivateKey caPrivateKey, String userEmail, String uuid) throws InvalidKeyException,
      NoSuchProviderException, SignatureException, CertificateParsingException,
      CertificateEncodingException, IllegalStateException, NoSuchAlgorithmException {

    X500Name subjectName = new X500Name(
        "CN=" + userEmail + ",O=" + uuid + ", L=Earth, ST=Alpha Centaurus, C=Milky Way");

    X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuerName,
        BigInteger.valueOf(System.nanoTime()), new Date(System.currentTimeMillis() - 1000),
        new Date(System.currentTimeMillis() + VALIDITY_PERIOD), subjectName, pair.getPublic());
    X509Certificate certificate = null;
    try {
      certificate = signCertificate(certGen, caPrivateKey);
    } catch (OperatorCreationException e) {
      log.error("OPERATOR CREATED EXCEPTION:", e);
    } catch (CertificateException e) {
      log.error("CERTIFICATE EXCEPTION:", e);
    }

    return certificate;
  }


  public static String encodeCertInPEM(X509Certificate cert) throws SecurityException {
    String base64String = null;
    try {
      base64String = Base64.toBase64String(cert.getEncoded());
    } catch (CertificateEncodingException e) {
      throw new SecurityException(e);
    }

    return CertAndKeyUtils.BEGIN_CERT + "\n" + base64String + "\n" + CertAndKeyUtils.END_CERT;
  }

  public static X509Certificate decodeCertFromPEM(String cert) throws Exception {
    byte[] decoded = Base64.decode(
        cert.replaceAll(CertAndKeyUtils.BEGIN_CERT, "").replaceAll(CertAndKeyUtils.END_CERT, ""));
    return (X509Certificate) CertificateFactory.getInstance("X.509")
        .generateCertificate(new ByteArrayInputStream(decoded));
  }


  public static X509Certificate signCsr(PKCS10CertificationRequest inputCSR, PrivateKey caPrivate)
      throws InvalidKeyException, NoSuchAlgorithmException,
      NoSuchProviderException, SignatureException, IOException,
      OperatorCreationException, CertificateException {

    AlgorithmIdentifier sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder()
        .find("SHA1withRSA");
    AlgorithmIdentifier digAlgId = new DefaultDigestAlgorithmIdentifierFinder()
        .find(sigAlgId);

    AsymmetricKeyParameter foo = PrivateKeyFactory.createKey(caPrivate
        .getEncoded());
    SubjectPublicKeyInfo keyInfo = inputCSR.getSubjectPublicKeyInfo();
    PKCS10CertificationRequest pk10Holder = new PKCS10CertificationRequest(
        inputCSR.toASN1Structure());

    X509v3CertificateBuilder myCertificateGenerator = new X509v3CertificateBuilder(
        new X500Name("CN=Keepassa"), new BigInteger("1"), new Date(
        System.currentTimeMillis()), new Date(
        System.currentTimeMillis() + 10 * 365 * 24 * 60 * 60
            * 1000), pk10Holder.getSubject(), keyInfo);

    ContentSigner sigGen = new BcRSAContentSignerBuilder(sigAlgId, digAlgId)
        .build(foo);

    X509CertificateHolder holder = myCertificateGenerator.build(sigGen);
    org.bouncycastle.asn1.x509.Certificate eeX509CertificateStructure = holder.toASN1Structure();

    CertificateFactory cf = CertificateFactory.getInstance("X.509", "BC");

    // Read Certificate
    InputStream is1 = new ByteArrayInputStream(eeX509CertificateStructure.getEncoded());
    X509Certificate theCert = (X509Certificate) cf.generateCertificate(is1);
    is1.close();
    return theCert;
    //return null;
  }

  /**
   * Return certificate from encoded form
   */
  public static X509Certificate getCertificateFromBytes(byte[] encodedCert) {
    CertificateFactory cf = null;
    try {
      cf = CertificateFactory.getInstance("X.509", "BC");
      // Read Certificate
      InputStream is1 = new ByteArrayInputStream(encodedCert);
      X509Certificate theCert = (X509Certificate) cf.generateCertificate(is1);
      return theCert;
    } catch (CertificateException e) {
      e.printStackTrace();
    } catch (NoSuchProviderException e) {
      e.printStackTrace();
    }
    return null;

  }
}

