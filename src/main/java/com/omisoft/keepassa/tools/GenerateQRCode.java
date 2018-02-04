package com.omisoft.keepassa.tools;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.omisoft.keepassa.utils.QRUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Generate QR Code utility Created by dido on 06.01.17.
 */
public class GenerateQRCode {

  /**
   * @param args
   * @throws WriterException
   * @throws IOException
   */
  public static void main(String[] args) throws WriterException, IOException {
    String qrCodeText =
        "otpauth://totp/keepassa.eu:dido@omisoft.eu?secret=JBSWY3DPEHPK3PXP&issuer=Keepassa.eu\n";
    String filePath = "test.png";
    int size = 200;
    String fileType = "png";
    File qrFile = new File(filePath);
    createQRImage(qrFile, qrCodeText, size, fileType);
  }

  private static void createQRImage(File qrFile, String qrCodeText, int size, String fileType)
      throws WriterException, IOException {
    // Create the ByteMatrix for the QR-Code that encodes the given String
    Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<>();
    hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
    QRCodeWriter qrCodeWriter = new QRCodeWriter();
    BitMatrix bitMatrix =
        qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, size, size, hintMap);
    // write to file

    MatrixToImageWriter.writeToStream(bitMatrix, fileType, new FileOutputStream(qrFile));
    System.out.println("Written to:" + qrFile.getAbsolutePath());
    // generate base 64

    String out = createBase64QRCode(qrCodeText, size, fileType);
    System.out.println(out);
    System.out.println("done");
    // Make the BufferedImage that are to hold the QRCode

  }

  /**
   * Creates BASE64 encoded image
   *
   * @param size in px
   * @param fileType png,jpg
   */
  public static String createBase64QRCode(String qrCodeText, int size, String fileType)
      throws WriterException, IOException {
    return QRUtils.createBase64QRCode(qrCodeText, size, fileType);

  }


}
