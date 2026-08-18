package com.financia.kash.auth.domain.model;

import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.util.Base64;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import de.taimos.totp.TOTP;

@Service
public class TwoFactorAuth {
    // 1. Genera un secreto aleatorio Base32 para el usuario
    public String generateNewSecret() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        return base32.encodeToString(bytes);
    }

    // 2. Genera la URL que leerá Google Authenticator
    public String getQRBarcodeURL(String secret, String username) {
        return "otpauth://totp/MiApp:" + username + "?secret=" + secret + "&issuer=MiApp";
    }

    // 3. Convierte la URL en una imagen QR en formato Base64 para el Frontend
    public String generateQRCodeBase64(String qrCodeText) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, 250, 250);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Error al generar código QR", e);
        }
    }

    // 4. Valida si el código de 6 dígitos es correcto en el tiempo actual
    public boolean verifyCode(String secret, String code) {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secret);
        String hexSecret = Hex.encodeHexString(bytes);
        // Calcula el código TOTP actual basado en el secreto hexadecimal
        String currentCode = TOTP.getOTP(hexSecret);
        return currentCode.equals(code);
    }
}
