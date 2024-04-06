package com.chieffu.pocker.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public abstract class PatternUtils {
    public static final Pattern HREF_PATTERN = Pattern.compile("(?i)(?<=<a[^<>]{1,300}href=['\"\\\\]?)[^\\s'\">\\\\#]+");

    public static List<List<String>> matches(String content, String pattern) {
        List<List<String>> matchs = new ArrayList<>();
        matchs.add(new ArrayList<>());
        try {
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(content);
            int groupCount = m.groupCount() + 1;
            int i;
            for (i = matchs.size(); i < groupCount; i++) {
                matchs.add(new ArrayList<>());
            }
            while (m.find()) {
                for (i = 0; i < groupCount; i++) {
                    matchs.get(i).add(m.group(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return matchs;
    }

    public static List<String> matchLinks(String src) {
        List<String> list = new ArrayList<>();
        Matcher m = HREF_PATTERN.matcher(src);
        while (m.find()) {
            list.add(m.group());
        }
        return list;
    }

    public static boolean findMatches(String src, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(src);
        return m.find();
    }

    public static String matchFirst(String src, String pattern, int group) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(src);
        if (m.find()) {
            return m.group(group);
        }
        return "";
    }
}


/* Location:              C:\Users\fred\Downloads\bet-server-1.0-SNAPSHOT\BOOT-INF\lib\bet-common-1.0.0-SNAPSHOT.jar!\com\chief\ww\\util\PatternUtils.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */