package com.chieffu.pocker.blackjack;

import com.alibaba.fastjson.JSON;
import com.chieffu.pocker.util.CoderUtil;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class Base64Parser {

    public static void main(String[] args) throws UnsupportedEncodingException {
        long[] key =  new long[]{1127692082,1144402481,1094268994,860042040};
        String theKey = "C772D621A98B3C38";
        WordArray wordArray = parse(CoderUtil.encryptBASE64(theKey.getBytes("utf-8")));
       System.out.println("预期："+JSON.toJSONString(key));
       System.out.println("结果："+JSON.toJSONString(wordArray.getWords()));
        System.out.println("Parsed WordArray: " + wordArray);

    }

    public static WordArray parse(String t) {
        int e = t.length();
        char[] map = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();

        // 构建反向映射表
        int[] reverseMap = new int[256];
        for (int a = 0; a < map.length; a++) {
            reverseMap[map[a]] = a;
        }

        char s = map[64]; // 填充字符 '='
        if (s != 0) {
            int sIndex = t.indexOf(s);
            if (sIndex != -1) {
                e = sIndex;
            }
        }

        // 解析 Base64 字符串
        long[] words = new long[e * 3 / 16];
        int o=0;
        for (int n, a, _ = 0; _ < e; _++) {
            if (_ % 4 != 0) {
                n = reverseMap[t.charAt(_ - 1)] << (_ % 4) * 2;
                a = reverseMap[t.charAt(_)] >>> 6 - (_ % 4) * 2;
                words[o >>> 2] |= (n | a) << 24 - o % 4 * 8;
                o++;
            }
        }

        return new WordArray(words, o);
    }

    public static class WordArray {
        private long[] words;
        private int sigBytes;

        public WordArray(long[] words,  int sigBytes) {
            this.words = words;
            this.sigBytes = sigBytes;
        }

        public long[] getWords() {
            return words;
        }

        public int getSigBytes() {
            return sigBytes;
        }

        @Override
        public String toString() {
            return "WordArray{" +
                    "words=" + Arrays.toString(words) +
                    ", sigBytes=" + sigBytes +
                    '}';
        }
    }
}