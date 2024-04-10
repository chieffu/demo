package com.chieffu.pocker.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public abstract class CoderUtil {
    private static final String EMAIL = "fred.fu1234@gmail.com";
    private static final String ALGORITHM = "DES";
    static String mac;
    static String enMac;
    private static String MAC;

    public static byte[] encryptMD5(byte[] data) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(data);
            return md5.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    public static byte[] encryptSha256(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(data);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    public static byte[] encryptSHA(byte[] data) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA");
            sha.update(data);
            return sha.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    public static byte[] decryptBASE64(String key) {
        return Base64.decode(key);
    }


    public static String encryptBASE64(byte[] key) {
        return Base64.encode(key).trim();
    }


    private static Key toKey(byte[] key) throws Exception {
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(dks);


        return secretKey;
    }


    public static byte[] decrypt(byte[] data, String key) throws Exception {
        Key k = toKey(decryptBASE64(key));

        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(2, k);

        return cipher.doFinal(data);
    }


    public static byte[] encrypt(byte[] data, String key) throws Exception {
        Key k = toKey(decryptBASE64(key));
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(1, k);

        return cipher.doFinal(data);
    }


    public static String initKey() throws Exception {
        return initKey((String) null);
    }


    public static String initKey(String seed) throws Exception {
        SecureRandom secureRandom = null;

        if (seed != null) {
            secureRandom = new SecureRandom(decryptBASE64(seed));
        } else {
            secureRandom = new SecureRandom();
        }

        KeyGenerator kg = KeyGenerator.getInstance("DES");
        kg.init(secureRandom);

        SecretKey secretKey = kg.generateKey();

        return encryptBASE64(secretKey.getEncoded());
    }


    public static String initKey(byte[] seed) throws Exception {
        SecureRandom secureRandom = null;

        if (seed != null) {
            secureRandom = new SecureRandom(seed);
        } else {
            secureRandom = new SecureRandom();
        }

        KeyGenerator kg = KeyGenerator.getInstance("DES");
        kg.init(secureRandom);

        SecretKey secretKey = kg.generateKey();

        return encryptBASE64(secretKey.getEncoded());
    }

    public static byte[] easyEncodeDecode(byte[] bytes, byte[] keys) {
        byte[] results = new byte[bytes.length];
        int length = keys.length;
        for (int i = 0; i < bytes.length; i++) {
            int k = i % length;
            results[i] = (byte) (bytes[i] ^ keys[k] & 0xFF);
        }
        return results;
    }

    public static String arrayToDelimitedString(Object[] arr, String delim) {
        if (arr == null || arr.length == 0) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) {
                sb.append(delim);
            }
            sb.append(arr[i]);
        }
        return sb.toString();
    }

    public static byte[] getKey(String[] args) {
        byte[] default_key = {102, 114, 101, 100, 46, 102, 117, 49, 50, 51, 52, 64, 103, 109, 97, 105, 108, 46, 99, 111, 109};
        if (args == null || args.length == 0) {
            return default_key;
        }
        return arrayToDelimitedString(args, " ").getBytes();
    }

    public static List<String> getIPs() {
        Enumeration<NetworkInterface> netInterfaces;
        List<String> list = new ArrayList<>();

        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            return list;
        }
        InetAddress ip = null;
        while (netInterfaces.hasMoreElements()) {
            NetworkInterface ni = netInterfaces.nextElement();
            Enumeration<InetAddress> address = ni.getInetAddresses();
            while (address.hasMoreElements()) {
                ip = address.nextElement();
                if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {
                    list.add(ip.getHostAddress());
                    continue;
                }
                if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {
                    list.add(ip.getHostAddress());
                }
            }
        }
        return list;
    }

    public static String getMacOnWindow() {
        if (mac == null || mac.length() == 0)
            try {
                String tempCmd = CmdUtil.execCommand(false, System.getenv("SystemRoot") + "/System32/ipconfig.exe", "/all");
                Pattern p = Pattern.compile("([0-9A-F]{2}-[0-9A-F]{2}-[0-9A-F]{2}-[0-9A-F]{2}-[0-9A-F]{2}-[0-9A-F]{2})");
                Matcher m = p.matcher(tempCmd);
                if (m.find()) {
                    mac = m.group(1);


                }


            } catch (Exception e1) {
                e1.printStackTrace();
            }
        return mac;
    }

    public static List<String> getAllNetworkAddressInReg() {
        String tempCmd = CmdUtil.execCommand(false, "reg", "query", "HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Control\\Class\\{4D36E972-E325-11CE-BFC1-08002bE10318}", "/s");
        List<List<String>> mcs = PatternUtils.matches(tempCmd, "(?i)NetworkAddress\\s+REG_SZ\\s+([0-9a-fA-F]*)");
        return (mcs.size() != 0) ? mcs.get(1) : new ArrayList<>();
    }

    public static List<String> getAllNetworkAddress() {
        List<String> networks = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                printMsg(ni);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return networks;
    }


    static void printMsg(NetworkInterface ni) {
        if (ni == null)
            return;
        try {
            if (ni.isUp()) {
                byte[] mac = ni.getHardwareAddress();
                String m = "";
                if (mac != null && mac.length > 0) {
                    m = StringUtils.bytesToHex(mac);

                    System.out.println((new BigInteger(mac)));
                }
                System.out.println("DisplayName:\t" + ni.getDisplayName());
                System.out.println("Name:\t" + ni.getName());
                System.out.println("MTU:\t" + ni.getMTU());
                System.out.println("HardwareAddress:\t" + m);
                Enumeration<InetAddress> en = ni.getInetAddresses();
                System.out.print("InetAddresses:\t");
                while (en.hasMoreElements()) {
                    System.out.print((new StringBuilder()).append(en.nextElement()).append("\t"));
                }
                System.out.println();
                System.out.println("InterfaceAddresses:\t" + ni.getInterfaceAddresses());
                Enumeration<NetworkInterface> niss = ni.getSubInterfaces();
                System.out.print("SubInterfaces:\t");
                System.out.println("----");
                while (niss.hasMoreElements()) {
                    NetworkInterface nii = niss.nextElement();
                    printMsg(nii);
                }
                System.out.println("PointToPoint:\t" + ni.isPointToPoint());
                System.out.println("Loopback:\t" + ni.isLoopback());
                System.out.println("Up:\t" + ni.isUp());
                System.out.println("Parent:\t");
                printMsg(ni.getParent());
                System.out.println();
                System.out.println();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public static String getRealIp() {
        Enumeration<NetworkInterface> netInterfaces;
        String localip = null;
        String netip = null;


        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            return "";
        }
        InetAddress ip = null;
        boolean finded = false;
        while (netInterfaces.hasMoreElements() && !finded) {
            NetworkInterface ni = netInterfaces.nextElement();
            Enumeration<InetAddress> address = ni.getInetAddresses();
            while (address.hasMoreElements()) {
                ip = address.nextElement();
                if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {
                    netip = ip.getHostAddress();
                    finded = true;
                    break;
                }
                if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {
                    localip = ip.getHostAddress();
                }
            }
        }

        if (netip != null && !"".equals(netip)) {
            return netip;
        }
        return localip;
    }


    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();

            return "localhost";
        }
    }

    public static String getMACAddress(String ip) {
        String tempCmd = CmdUtil.execCommand(false, "nbtstat", "-A", ip);
        String macAddress = PatternUtils.matchFirst(tempCmd.toUpperCase(), "([0-9A-F]{2}-[0-9A-F]{2}-[0-9A-F]{2}-[0-9A-F]{2}-[0-9A-F]{2}-[0-9A-F]{2})", 1);
        return macAddress;
    }


    public static byte[] rsa(byte[] data, BigInteger base, BigInteger exp) {
        int size = (base.toByteArray()).length;
        if (data.length < size) {
            BigInteger b0 = new BigInteger(data);
            BigInteger r = b0.modPow(exp, base);
            return r.toByteArray();
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int n = data.length / size;
        int start = 0;
        while (start < data.length) {
            int len = Math.min(size, data.length - start);
            byte[] arr = new byte[len];
            System.arraycopy(data, start, arr, 0, len);
            BigInteger b0 = new BigInteger(arr);
            if (b0.compareTo(base) < 0) {
                start += len;
            } else {
                arr = new byte[len - 1];
                System.arraycopy(data, start, arr, 0, len - 1);
                b0 = new BigInteger(arr);
                start += len - 1;
            }
            BigInteger r = b0.modPow(exp, base);
            try {
                byte[] b1 = r.toByteArray();
                out.write(b1);
            } catch (IOException iOException) {
            }
        }

        if (start != data.length) {
            byte[] remaining = new byte[data.length - start];
            System.arraycopy(data, data.length - remaining.length, remaining, 0, remaining.length);
            BigInteger b0 = new BigInteger(remaining);
            BigInteger r = b0.modPow(exp, base);

            try {
                out.write(r.toByteArray());
            } catch (IOException iOException) {
            }
        }

        try {
            out.close();
        } catch (IOException iOException) {
        }

        return out.toByteArray();
    }


    public static String getMacWithOther() throws Exception {
        if (MAC == null) {
            String mac = getMacOnWindow();
            if (mac == null || !mac.matches("([0-9A-F]{2}-[0-9A-F]{2}-[0-9A-F]{2}-[0-9A-F]{2}-[0-9A-F]{2}-[0-9A-F]{2})")) {
                enMac = "";
                return null;
            }
            String cpuSn = CmdUtil.getCPUSerial();
            SecureRandom sr = new SecureRandom(mac.getBytes());
            if (cpuSn == null || cpuSn.length() == 0) {
                cpuSn = sr.nextLong() + "";
            }
            long cpuSnR = (new SecureRandom(cpuSn.getBytes())).nextLong();
            String boardSn = CmdUtil.getMotherboardSN();
            if (boardSn == null || boardSn.length() == 0) {
                boardSn = sr.nextLong() + "";
            }
            long boardSnR = (new SecureRandom(boardSn.getBytes())).nextLong();
            byte[] cpuBoardSn = easyEncodeDecode(Long.toHexString(cpuSnR).getBytes(), Long.toHexString(boardSnR).getBytes());
            byte[] cmdMd5 = encryptMD5(FileCopyUtils.copyToByteArray(new FileInputStream(System.getenv("SystemRoot") + "/System32/cmd.exe")));
            byte[] cscriptMd5 = encryptMD5(FileCopyUtils.copyToByteArray(new FileInputStream(System.getenv("SystemRoot") + "/System32/cscript.exe")));
            byte[] ipconfigMd5 = encryptMD5(FileCopyUtils.copyToByteArray(new FileInputStream(System.getenv("SystemRoot") + "/System32/ipconfig.exe")));
            byte[] regMd5 = encryptMD5(FileCopyUtils.copyToByteArray(new FileInputStream(System.getenv("SystemRoot") + "/System32/reg.exe")));
            byte[] bbs = easyEncodeDecode(easyEncodeDecode(cmdMd5, cscriptMd5), easyEncodeDecode(ipconfigMd5, regMd5));
            MAC = (mac + '\t' + StringUtils.bytesToHex(easyEncodeDecode(bbs, cpuBoardSn))).trim();
        }
        return MAC;
    }


    public static String getEncodedMac() {
        if (enMac == null)
            try {
                byte[] b = getMacWithOther().getBytes();
                CertificateFactory cff = CertificateFactory.getInstance("X.509");
                InputStream fis1 = ClassLoader.getSystemResourceAsStream("wow.cer");
                Certificate cf = cff.generateCertificate(fis1);
                Cipher c1 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                c1.init(1, cf.getPublicKey());
                byte[] b1 = decodeSign(b, c1);
                enMac = encryptBASE64(b1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        return enMac;
    }

    public static byte[] decodeSign(byte[] data, Cipher cipher) throws Exception {
        return cipher.doFinal(data);
    }

    public static long getNetworkTime() {
        Browser browser = null;

        try {
            browser = new Browser();
            browser.enableLog(false);
            String content = browser.get(new Link("http://www.time.ac.cn/timeflash.asp?user=flash"));
            String nyear = PatternUtils.matchFirst(content, "<year[^<>]*>\\s*(\\d+)\\s*</year>", 1);
            String nmonth = PatternUtils.matchFirst(content, "<month[^<>]*>\\s*(\\d+)\\s*</month>", 1);
            String nday = PatternUtils.matchFirst(content, "<day[^<>]*>\\s*(\\d+)\\s*</day>", 1);
            String nhrs = PatternUtils.matchFirst(content, "<hour[^<>]*>\\s*(\\d+)\\s*</hour>", 1);
            String nmin = PatternUtils.matchFirst(content, "<minite[^<>]*>\\s*(\\d+)\\s*</minite>", 1);
            String nsec = PatternUtils.matchFirst(content, "<second[^<>]*>\\s*(\\d+)\\s*</second>", 1);
            if (nyear.length() > 0 && nmonth.length() > 0 && nday.length() > 0 && nhrs.length() > 0 && nmin.length() > 0 && nsec.length() > 0) {
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                return sd.parse(nyear + "-" + nmonth + "-" + nday + " " + nhrs + ":" + nmin + ":" + nsec).getTime();
            }
        } catch (Exception exception) {
        } finally {
            browser.shutDown();
        }


        try {
            browser = new Browser();
            browser.enableLog(false);
            String content = browser.get(new Link("http://www.beijing-time.org/time.asp"));
            String nyear = PatternUtils.matchFirst(content, "nyear\\s*=\\s*(\\d+)", 1);
            String nmonth = PatternUtils.matchFirst(content, "nmonth\\s*=\\s*(\\d+)", 1);
            String nday = PatternUtils.matchFirst(content, "nday\\s*=\\s*(\\d+)", 1);
            String nhrs = PatternUtils.matchFirst(content, "nhrs\\s*=\\s*(\\d+)", 1);
            String nmin = PatternUtils.matchFirst(content, "nmin\\s*=\\s*(\\d+)", 1);
            String nsec = PatternUtils.matchFirst(content, "nsec\\s*=\\s*(\\d+)", 1);
            if (nyear.length() > 0 && nmonth.length() > 0 && nday.length() > 0 && nhrs.length() > 0 && nmin.length() > 0 && nsec.length() > 0) {
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                return sd.parse(nyear + "-" + nmonth + "-" + nday + " " + nhrs + ":" + nmin + ":" + nsec).getTime();
            }
        } catch (Exception exception) {
        } finally {
            browser.shutDown();
        }


        return -1L;
    }


    public static RSAPublicKey getPublicKey(String modulus, String exponent, int jinzhi) {
        try {
            BigInteger b1 = new BigInteger(modulus, jinzhi);
            BigInteger b2 = new BigInteger(exponent, jinzhi);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(b1, b2);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static RSAPrivateKey getPrivateKey(String modulus, String exponent, int jinzhi) {
        try {
            BigInteger b1 = new BigInteger(modulus, jinzhi);
            BigInteger b2 = new BigInteger(exponent, jinzhi);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(b1, b2);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String encryptByPublicKey(String data, RSAPublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(1, publicKey);

        int key_len = publicKey.getModulus().bitLength() + 7 >> 3;
        byte[] b = ASCII_To_BCD(data.getBytes(), key_len);
        BigInteger m = new BigInteger(b);
        BigInteger c = m.modPow(publicKey.getPublicExponent(), publicKey.getModulus());
        String h = c.toString(16);
        if ((h.length() & 0x1) == 0) {
            return h;
        }
        return "0" + h;
    }


    public static String decryptByPrivateKey(String data, RSAPrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(2, privateKey);

        int key_len = privateKey.getModulus().bitLength() / 8;
        byte[] bytes = data.getBytes();
        byte[] bcd = ASCII_To_BCD(bytes, bytes.length);
        System.err.println(bcd.length);

        String ming = "";
        byte[][] arrays = splitArray(bcd, key_len);
        for (byte[] arr : arrays) {
            ming = ming + new String(cipher.doFinal(arr));
        }
        return ming;
    }


    private static byte[] ASCII_To_BCD(byte[] ascii, int asc_len) {
        byte[] bcd = new byte[asc_len / 2];
        int j = 0;
        for (int i = 0; i < (asc_len + 1) / 2; i++) {
            bcd[i] = asc_to_bcd(ascii[j++]);
            bcd[i] = (byte) (((j >= asc_len) ? 0 : asc_to_bcd(ascii[j++])) + (bcd[i] << 4));
        }
        return bcd;
    }


    private static byte asc_to_bcd(byte asc) {
        byte bcd;
        if (asc >= 48 && asc <= 57) {
            bcd = (byte) (asc - 48);
        } else if (asc >= 65 && asc <= 70) {
            bcd = (byte) (asc - 65 + 10);
        } else if (asc >= 97 && asc <= 102) {
            bcd = (byte) (asc - 97 + 10);
        } else {
            bcd = (byte) (asc - 48);
        }
        return bcd;
    }


    private static String bcd2Str(byte[] bytes) {
        char[] temp = new char[bytes.length * 2];

        for (int i = 0; i < bytes.length; i++) {
            char val = (char) ((bytes[i] & 0xF0) >> 4 & 0xF);
            temp[i * 2] = (char) ((val > '\t') ? (val + 65 - 10) : (val + 48));

            val = (char) (bytes[i] & 0xF);
            temp[i * 2 + 1] = (char) ((val > '\t') ? (val + 65 - 10) : (val + 48));
        }
        return new String(temp);
    }


    private static String[] splitString(String string, int len) {
        int x = string.length() / len;
        int y = string.length() % len;
        int z = 0;
        if (y != 0) {
            z = 1;
        }
        String[] strings = new String[x + z];
        String str = "";
        for (int i = 0; i < x + z; i++) {
            if (i == x + z - 1 && y != 0) {
                str = string.substring(i * len, i * len + y);
            } else {
                str = string.substring(i * len, i * len + len);
            }
            strings[i] = str;
        }
        return strings;
    }


    private static byte[][] splitArray(byte[] data, int len) {
        int x = data.length / len;
        int y = data.length % len;
        int z = 0;
        if (y != 0) {
            z = 1;
        }
        byte[][] arrays = new byte[x + z][];

        for (int i = 0; i < x + z; i++) {
            byte[] arr = new byte[len];
            if (i == x + z - 1 && y != 0) {
                System.arraycopy(data, i * len, arr, 0, y);
            } else {
                System.arraycopy(data, i * len, arr, 0, len);
            }
            arrays[i] = arr;
        }
        return arrays;
    }

    public static void main(String[] args) throws Exception {
        RSAPublicKey publicKey = getPublicKey("D1EC51E7CEA07CB3233ADA6009006EF3EBF89EFD5CF77AAD211051D008077DC7142872B8C36EE971D4B368C79C13A6BBCB89B551A8308C68F71764C1519DEAD90B560E126B365375700CC5A2E6CF81E2A0FEEA31B53C1F8D3F3AE522DF9AB19B5C0C391D997D6DE56807328B9BBD5F6D08EA47614060177E12F65BDB95D5D6E3", "10001", 16);
        String data = "helloword";
        String en = encryptByPublicKey(data, publicKey);
        System.out.println(en);
    }
}


/* Location:              C:\Users\fred\Downloads\bet-server-1.0-SNAPSHOT\BOOT-INF\lib\bet-common-1.0.0-SNAPSHOT.jar!\com\chief\ww\\util\CoderUtil.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */