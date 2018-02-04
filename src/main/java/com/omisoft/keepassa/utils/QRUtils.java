package com.omisoft.keepassa.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by leozhekov on 1/9/17.
 */
public class QRUtils {

  /**
   * Creates BASE64 encoded image
   *
   * @param size in px
   * @param fileType png,jpg
   */
  public static String createBase64QRCode(String qrCodeText, int size, String fileType)
      throws WriterException, IOException {
    Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<>();
    hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

    QRCodeWriter qrCodeWriter = new QRCodeWriter();
    BitMatrix bitMatrix =
        qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, size, size, hintMap);
    // write to file
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    MatrixToImageWriter.writeToStream(bitMatrix, fileType, baos);
    return org.apache.commons.codec.binary.Base64.encodeBase64String(baos.toByteArray());
  }
}
