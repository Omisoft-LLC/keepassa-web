package com.omisoft.keepassa.utils;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.util.Date;
import org.joda.time.DateTime;


/**
 * Created by nslavov on 5/10/16.
 */
public final class AuthUtils {

  //  public static final String AUTH_HEADER_KEY = "Authorization";
  private static final JWSHeader JWT_HEADER = new JWSHeader(JWSAlgorithm.HS256);
  private static final String TOKEN_SECRET = "tarzanbezgashti";

  // public static String getSubject(String authHeader) throws ParseException, JOSEException {
  // return decodeToken(authHeader).getSubject();
  // }

  public static JWTClaimsSet decodeToken(String authHeader)
      throws ParseException, JOSEException {
    SignedJWT signedJWT = SignedJWT.parse(authHeader);
    if (signedJWT.verify(new MACVerifier(TOKEN_SECRET))) {
      return signedJWT.getJWTClaimsSet();
    } else {
      throw new JOSEException("Signature verification failed");
    }
  }

  public static Token createToken(String host, String sub) throws JOSEException {

//    JWTClaimsSet claim = new JWTClaimsSet();
    //    claim.setIssuer(host);
//    claim.setSubject(sub);
//    claim.setExpirationTime(DateTime.now().plusDays(1).toDate());
//    claim.setNotBeforeTime(new Date());
//    claim.setIssueTime(new Date());
//    claim.setJWTID(GenUtils.getUUID());

    JWTClaimsSet claim = new JWTClaimsSet.Builder()
        .issuer(host)
        .subject(sub)
        .expirationTime(DateTime.now().plusDays(1).toDate())
        .notBeforeTime(new Date())
        .issueTime(new Date())
        .jwtID(GenUtils.getUUID())
        .build();

    JWSSigner signer = new MACSigner(TOKEN_SECRET);
    SignedJWT jwt = new SignedJWT(JWT_HEADER, claim);
    jwt.sign(signer);

    return new Token(jwt.serialize());
  }

}
