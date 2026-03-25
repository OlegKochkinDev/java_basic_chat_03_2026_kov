package test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AppTest {

    public static void main(String[] args) {
        String originalString = "hello world";
        System.out.println(cryptMessage("qwe"));
        System.out.println(cryptMessage("asd"));
        System.out.println(cryptMessage("zxc"));

    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private static String cryptMessage(String text) {
        String sha256hex = null;
        try {
            // Создаем экземпляр SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Вычисляем хэш (получаем байтовый массив)
            byte[] hashBytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));

            // Конвертируем байты в шестнадцатеричную строку
            sha256hex = bytesToHex(hashBytes);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sha256hex;
    }
}
