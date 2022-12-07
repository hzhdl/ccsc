package com.ccsc.ccsc.util;

import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.springframework.util.Base64Utils;

@Getter
@Setter
public class RSACipher {

    public static  String publicKeyString="";

    public static String privateKeyString="";

    /**
     * 生成秘钥对
     * @return 公私钥对
     */
    public static void generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        SecureRandom secureRandom=new SecureRandom();
        secureRandom.setSeed(new Date().getTime()+ new Random().nextLong());
        keyPairGenerator.initialize(1024,secureRandom);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
//        String publicKeyString = Base64.encodeBase64String(rsaPublicKey.getEncoded());
//        String privateKeyString = Base64.encodeBase64String(rsaPrivateKey.getEncoded());
        String publicKeyString = Base64Utils.encodeToString(rsaPublicKey.getEncoded());
        String privateKeyString = Base64Utils.encodeToString(rsaPrivateKey.getEncoded());
//        System.out.println(publicKeyString);
//        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(rsaPublicKey.getEncoded());
//        System.out.println(Base64Utils.encodeToString(keySpec.getEncoded()));
//        System.out.println(privateKeyString);
//        PKCS8EncodedKeySpec keySpec1 = new PKCS8EncodedKeySpec(rsaPrivateKey.getEncoded());
//        System.out.println(Base64Utils.encodeToString(keySpec1.getEncoded()));
//        System.out.println(privateKeyString);
        RSACipher.publicKeyString=publicKeyString;
        RSACipher.privateKeyString=privateKeyString;
    }

    /**
     * 获取公钥
     * @param key 密钥字符串（经过base64编码）
     * @return 公钥
     */
    public static PublicKey getPublicKey(String key) throws Exception {
        // 按照X.509标准对其进行编码的密钥
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64Utils.decode(key.getBytes()));
//        System.out.println(Base64Utils.encodeToString(keySpec.getEncoded()));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        // 生成公钥
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    /**
     * 获取私钥
     * @param key 密钥字符串（经过base64编码）
     * @return 私钥
     */
    public static PrivateKey getPrivateKey(String key) throws Exception {
        // 按照PKCS8格式标准对其进行编码的密钥，首先要将key进行base64解码
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64Utils.decode(key.getBytes()));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        // 生成私钥
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    /**
     * 加密方法
     * @param publicKey 公钥
     * @param raw       待加密明文
     * @return 加密后的密文
     */
    public static byte[] encrypt(String publicKey, byte[] raw) throws Exception {
        Key key = getPublicKey(publicKey);
        Cipher cipher = Cipher.getInstance(Config.RSA_ALGORITHM);
        // 初始化
        cipher.init(Cipher.ENCRYPT_MODE, key, new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT));
        byte[] encryption = cipher.doFinal(raw);
        // 最后将加密后的数据进行base64编码
        return Base64Utils.encode(encryption);
    }

    /**
     * 解密方法
     * @param privateKey 私钥
     * @param enc  待解密密文
     * @return 解密后的明文
     */
    public static byte[] decrypt(String privateKey, byte[] enc) throws Exception {
        Key key = getPrivateKey(privateKey);
        Cipher cipher = Cipher.getInstance(Config.RSA_ALGORITHM);
        // 初始化
        cipher.init(Cipher.DECRYPT_MODE, key, new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT));
        // 先进行base64解密，然后解码
        return cipher.doFinal(Base64Utils.decode(enc));
    }

    /**
     * 签名
     * @param privateKey 私钥
     * @param content    要进行签名的内容
     * @return 签名
     */
    public static String sign(String privateKey, byte[] content) {
        try {
            // privateKey进行base64编码，然后生成PKCS8格式私钥
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64Utils.decode(privateKey.getBytes()));
            KeyFactory key = KeyFactory.getInstance("RSA");
            PrivateKey priKey = key.generatePrivate(priPKCS8);
            // 签名摘要算法
            Signature signature = Signature.getInstance("SHA256WithRSA");
            // 用私钥初始化此对象以进行签名
            signature.initSign(priKey);
            // 使用指定的字节数组更新签名或验证
            signature.update(content);
            // 获得签名字节
            byte[] signed = signature.sign();
            // 进行HEX编码返回
            return Base64Utils.encodeToString(signed);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 验签
     * @param publicKey 公钥
     * @param content   要验签的内容
     * @param sign      签名
     * @return 验签结果
     */
    public static boolean checkSign(String publicKey, byte[] content, String sign) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            // 进行base64解码
            byte[] encodedKey = Base64Utils.decodeFromString(publicKey);
            // 生成公钥
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
            // 签名摘要算法
            Signature signature = Signature.getInstance("SHA256WithRSA");
            // 用公钥初始化签名
            signature.initVerify(pubKey);
            // 使用指定的字节数组更新签名或验证
            signature.update(content);
            // base64解码后进行验证
            return signature.verify(Base64Utils.decodeFromString(sign));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
//        //客户端代码
//        String text = "hello";
//        //使用服务端公钥加密
//        byte[] encryptText = RSACipher.encrypt(Config.SERVER_PUBLIC_KEY, text.getBytes());
//        System.out.println("加密后:\n" + new String(encryptText));
//        //使用客户端私钥签名
//        String signature = RSACipher.sign(Config.CLIENT_PRIVATE_KEY, encryptText);
//        System.out.println("签名:\n" + signature);
//        //服务端代码
//        //使用客户端公钥验签
//        boolean result = RSACipher.checkSign(Config.CLIENT_PUBLIC_KEY, encryptText, signature);
//        System.out.println("验签:\n" + result);
//        //使用服务端私钥解密
//        byte[] decryptText = RSACipher.decrypt(Config.SERVER_PRIVATE_KEY, encryptText);
//        System.out.println("解密后:\n" + new String(decryptText));
        generateKeyPair();
        generateKeyPair();
    }
}

