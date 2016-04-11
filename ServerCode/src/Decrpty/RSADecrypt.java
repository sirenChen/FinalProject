package Decrpty;

import javax.crypto.Cipher;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.interfaces.RSAPrivateKey;

/**
 * RSA decryption
 */
public class RSADecrypt {
    private static RSAPrivateKey privateKey;

    public static void loadKey () {
        ObjectInputStream inputStream = null;

        try {
            inputStream = new ObjectInputStream(new FileInputStream("/home/ubuntu/Server/privatekey"));
            privateKey = (RSAPrivateKey) inputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static byte[] decrypt (byte[] srcBytes){
        byte[] resultBytes = null;

        if (privateKey != null) {
            Cipher cipher = null;

            try {
                cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipher.init(Cipher.DECRYPT_MODE, privateKey);

                resultBytes = cipher.doFinal(srcBytes);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return resultBytes;
        }
        return null;
    }

    /**
     * hex string to binary data
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStrToByte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        }

        byte[] result = new byte[hexStr.length()/2];

        for (int i=0; i<hexStr.length()/2;i++) {
            int high = Integer.parseInt(hexStr.substring(i*2,i*2+1),16);
            int low  = Integer.parseInt(hexStr.substring(i*2+1,i*2+2),16);

            result[i] = (byte) (high*16 + low);
        }

        return result;
    }

}
