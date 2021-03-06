package helpers;

import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Получение хэша файла
 */
public class FileHelper {
    /**
     * Взять хэш от файла
     * @param filePath Путь до файла
     * @return хэш
     */
    public static String getHash(String filePath){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            FileInputStream fis = new FileInputStream(filePath);
            byte[] dataBytes = new byte[1024];
            int nread = 0;
            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            }
            byte[] mdbytes = md.digest();

            //
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mdbytes.length; i++) {
                sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Взять хэш от файла
     * @param stream Input stream файла
     * @return хэш
     */
    public static String getHash(InputStream stream){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] dataBytes = new byte[1024];
            int nread = 0;
            while ((nread = stream.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            }
            byte[] mdbytes = md.digest();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mdbytes.length; i++) {
                sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Взять хэш от файла
     * @param bytes Массив байт
     * @return хэш
     */
    public static String getHash(byte[] bytes){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(bytes);

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getHashPath(String hash) {
        String firstDir = hash.substring(0, 2);
        String secondDir = hash.substring(2, 4);
        return firstDir + File.separator + secondDir;
    }

    /**
     * Кодирование файла в Base64
     */
    public static void encodeBase64(InputStream inputStream, String outPath) throws IOException {
        int BUFFER_SIZE = 4096;
        byte[] buffer = new byte[BUFFER_SIZE];
        OutputStream output = new Base64OutputStream(new FileOutputStream(outPath));
        int n = inputStream.read(buffer, 0, BUFFER_SIZE);
        while (n >= 0) {
            output.write(buffer, 0, n);
            n = inputStream.read(buffer, 0, BUFFER_SIZE);
        }
        output.close();
    }

    /**
     * Декодирование файла из Base64
     */
    public static void decodeBase64(String base64FileName, String outPath) throws IOException {
        int BUFFER_SIZE = 4096;
        byte[] buffer = new byte[BUFFER_SIZE];
        InputStream input = new Base64InputStream(new FileInputStream(base64FileName));
        OutputStream output = new FileOutputStream(outPath);
        int n = input.read(buffer, 0, BUFFER_SIZE);
        while (n >= 0) {
            output.write(buffer, 0, n);
            n = input.read(buffer, 0, BUFFER_SIZE);
        }
        input.close();
        output.close();
    }
}
