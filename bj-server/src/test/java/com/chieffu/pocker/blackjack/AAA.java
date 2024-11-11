package com.chieffu.pocker.blackjack;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chieffu.pocker.util.CoderUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class AAA {
    public static void main(String[] args) throws Exception {

        String key0 = "C772D621A98B3C38";
        String keyBase64 = CoderUtil.encryptBASE64(key0.getBytes("utf-8"));
        String base64Data = "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";
        byte[] data = CoderUtil.decryptBASE64(base64Data);
        byte[] keyBytes = keyBase64.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

        // 将密钥也用作 IV
        IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);

        // 解密模式
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);

        // 将 Base64 编码的加密文本解码为字节数组
        byte[] encryptedBytes = Base64.getDecoder().decode(data);

        // 解密
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);


        // 将解密后的字节数组转换为字符串
    }

        private static long[] hexStringToLongArray(String hexString) {
            long[] result = new long[4];
            for (int i = 0; i < 4; i++) {
                int start = i * 8;
                int end = start + 8;
                String part = hexString.substring(start, end);
                result[i] = Long.parseLong(part, 16);
            }
            return result;
        }

        private static boolean compareArrays(long[] longArray, List<Long> jsonArray) {
            if (longArray.length != jsonArray.size()) {
                return false;
            }
            for (int i = 0; i < longArray.length; i++) {
                if (longArray[i] != jsonArray.get(i).longValue()) {
                    return false;
                }
            }
            return true;
        }

    public static String aesDecrypt(String encryptedText, String key) throws Exception {
        // 将密钥转换为字节数组
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        // 将密钥转换为 SecretKeySpec
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

        // 将密钥也用作 IV
        IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);

        // 解密模式
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);

        // 将 Base64 编码的加密文本解码为字节数组
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);

        // 解密
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        // 将解密后的字节数组转换为字符串
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }



    private static void test0() {
        String json = "{\"a49\":36,\"000\":0,\"N35\":23,\"313\":3,\"A21\":10,\"H15\":17,\"E25\":14,\"C23\":12,\"M44\":22,\"623\":6,\"421\":4,\"X39\":33,\"913\":9,\"G24\":16,\"R39\":27,\"Q48\":26,\"F16\":15,\"111\":1,\"J34\":19,\"P37\":25,\"O46\":24,\"T38\":29,\"222\":2,\"U49\":30,\"K45\":20,\"512\":5,\"V37\":31,\"W48\":32,\"711\":7,\"313\":3,\"D14\":13,\"L36\":21,\"913\":9,\"B12\":11,\"711\":7,\"U49\":30,\"a49\":36,\"822\":8,\"Y47\":34,\"Z38\":35,\"A21\":10,\"V37\":31,\"Q48\":26,\"E25\":14,\"R39\":27,\"X39\":33,\"C23\":12,\"111\":1,\"G24\":16,\"J34\":19,\"I26\":18,\"F16\":15,\"H15\":17,\"S47\":28,\"W48\":32}";
        JSONObject jsonObject = JSONObject.parseObject(json);
        Map<Integer,String> map = new TreeMap<>();
        Map<String,Integer> map1 = new TreeMap<>();
        jsonObject.forEach((k, v) ->{ map.put(Integer.parseInt(v.toString()),k);map1.put(k,Integer.parseInt(v.toString()));});
        map.keySet().forEach(k->{System.out.println(k+"\t:\t"+map.get(k));});
        System.out.println(JSON.toJSONString(map));
        System.out.println(JSON.toJSONString(map1));
    }
}
