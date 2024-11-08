
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public abstract class StringUtils {
    private static final Map<String, String> entityEscapeMap = new HashMap<>();
    private static final Map<String, String> escapeEntityMap = new HashMap<>();


    private static final String _BR = "<br/>";

    private static final List<String> COMMON_WORDS = new ArrayList<>();




    public static boolean hasLength(String str) {
        return (str != null && str.length() > 0);
    }


    public static boolean hasText(String str) {
        if (!hasLength(str)) {
            return false;
        }
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }


    public static boolean containsWhitespace(String str) {
        if (!hasLength(str)) {
            return false;
        }
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }


    public static String trimWhitespace(String str) {
        if (!hasLength(str)) {
            return str;
        }
        StringBuffer buf = new StringBuffer(str);
        while (buf.length() > 0 && Character.isWhitespace(buf.charAt(0))) {
            buf.deleteCharAt(0);
        }
        while (buf.length() > 0 && Character.isWhitespace(buf.charAt(buf.length() - 1))) {
            buf.deleteCharAt(buf.length() - 1);
        }
        return buf.toString();
    }


    public static String trimLeadingWhitespace(String str) {
        if (!hasLength(str)) {
            return str;
        }
        StringBuffer buf = new StringBuffer(str);
        while (buf.length() > 0 && Character.isWhitespace(buf.charAt(0))) {
            buf.deleteCharAt(0);
        }
        return buf.toString();
    }


    public static String trimLeadingCharacter(String str, char leadingCharacter) {
        if (!hasLength(str)) {
            return str;
        }
        StringBuffer buf = new StringBuffer(str);
        while (buf.length() > 0 && buf.charAt(0) == leadingCharacter) {
            buf.deleteCharAt(0);
        }
        return buf.toString();
    }


    public static String trimTrailingWhitespace(String str) {
        if (!hasLength(str)) {
            return str;
        }
        StringBuffer buf = new StringBuffer(str);
        while (buf.length() > 0 && Character.isWhitespace(buf.charAt(buf.length() - 1))) {
            buf.deleteCharAt(buf.length() - 1);
        }
        return buf.toString();
    }


    public static String trimAllWhitespace(String str) {
        if (!hasLength(str)) {
            return str;
        }
        StringBuffer buf = new StringBuffer(str);
        int index = 0;
        while (buf.length() > index) {
            if (Character.isWhitespace(buf.charAt(index))) {
                buf.deleteCharAt(index);
                continue;
            }
            index++;
        }

        return buf.toString();
    }


    public static boolean startsWithIgnoreCase(String str, String prefix) {
        if (str == null || prefix == null) {
            return false;
        }
        if (str.startsWith(prefix)) {
            return true;
        }
        if (str.length() < prefix.length()) {
            return false;
        }
        String lcStr = str.substring(0, prefix.length()).toLowerCase();
        String lcPrefix = prefix.toLowerCase();
        return lcStr.equals(lcPrefix);
    }


    public static boolean endsWithIgnoreCase(String str, String suffix) {
        if (str == null || suffix == null) {
            return false;
        }
        if (str.endsWith(suffix)) {
            return true;
        }
        if (str.length() < suffix.length()) {
            return false;
        }

        String lcStr = str.substring(str.length() - suffix.length()).toLowerCase();
        String lcSuffix = suffix.toLowerCase();
        return lcStr.equals(lcSuffix);
    }


    public static int countOccurrencesOf(String str, String sub) {
        if (str == null || sub == null || str.length() == 0 || sub.length() == 0) {
            return 0;
        }
        int count = 0, pos = 0, idx = 0;
        while ((idx = str.indexOf(sub, pos)) != -1) {
            count++;
            pos = idx + sub.length();
        }
        return count;
    }


    public static String replace(String inString, String oldPattern, String newPattern) {
        if (inString == null) {
            return null;
        }
        if (oldPattern == null || newPattern == null) {
            return inString;
        }

        StringBuffer sbuf = new StringBuffer();

        int pos = 0;
        int index = inString.indexOf(oldPattern);

        int patLen = oldPattern.length();
        while (index >= 0) {
            sbuf.append(inString, pos, index);
            sbuf.append(newPattern);
            pos = index + patLen;
            index = inString.indexOf(oldPattern, pos);
        }
        sbuf.append(inString.substring(pos));


        return sbuf.toString();
    }


    public static String delete(String inString, String pattern) {
        return replace(inString, pattern, "");
    }


    public static String deleteAny(String inString, String charsToDelete) {
        if (!hasLength(inString) || !hasLength(charsToDelete)) {
            return inString;
        }
        StringBuffer out = new StringBuffer();
        for (int i = 0; i < inString.length(); i++) {
            char c = inString.charAt(i);
            if (charsToDelete.indexOf(c) == -1) {
                out.append(c);
            }
        }
        return out.toString();
    }


    public static String quote(String str) {
        return (str != null) ? ("'" + str + "'") : null;
    }


    public static Object quoteIfString(Object obj) {
        return (obj instanceof String) ? quote((String) obj) : obj;
    }


    public static String unqualify(String qualifiedName) {
        return unqualify(qualifiedName, '.');
    }


    public static String unqualify(String qualifiedName, char separator) {
        return qualifiedName.substring(qualifiedName.lastIndexOf(separator) + 1);
    }


    public static String capitalize(String str) {
        return changeFirstCharacterCase(str, true);
    }


    public static String uncapitalize(String str) {
        return changeFirstCharacterCase(str, false);
    }

    private static String changeFirstCharacterCase(String str, boolean capitalize) {
        if (str == null || str.length() == 0) {
            return str;
        }
        StringBuffer buf = new StringBuffer(str.length());
        if (capitalize) {
            buf.append(Character.toUpperCase(str.charAt(0)));
        } else {
            buf.append(Character.toLowerCase(str.charAt(0)));
        }
        buf.append(str.substring(1));
        return buf.toString();
    }


    public static String getFilename(String path) {
        if (path == null) {
            return null;
        }
        int separatorIndex = path.lastIndexOf("/");
        return (separatorIndex != -1) ? path.substring(separatorIndex + 1) : path;
    }


    public static String getFilenameExtension(String path) {
        if (path == null) {
            return null;
        }
        int sepIndex = path.lastIndexOf('.');
        return (sepIndex != -1) ? path.substring(sepIndex + 1) : null;
    }


    public static String stripFilenameExtension(String path) {
        if (path == null) {
            return null;
        }
        int sepIndex = path.lastIndexOf('.');
        return (sepIndex != -1) ? path.substring(0, sepIndex) : path;
    }


    public static String applyRelativePath(String path, String relativePath) {
        int separatorIndex = path.lastIndexOf("/");
        if (separatorIndex != -1) {
            String newPath = path.substring(0, separatorIndex);
            if (!relativePath.startsWith("/")) {
                newPath = newPath + "/";
            }
            return newPath + relativePath;
        }
        return relativePath;
    }


    public static Locale parseLocaleString(String localeString) {
        String[] parts = tokenizeToStringArray(localeString, "_ ", false, false);
        String language = (parts.length > 0) ? parts[0] : "";
        String country = (parts.length > 1) ? parts[1] : "";
        String variant = "";
        if (parts.length >= 2) {


            int endIndexOfCountryCode = localeString.indexOf(country) + country.length();


            variant = trimLeadingWhitespace(localeString.substring(endIndexOfCountryCode));
            if (variant.startsWith("_")) {
                variant = trimLeadingCharacter(variant, '_');
            }
        }
        return (language.length() > 0) ? new Locale(language, country, variant) : null;
    }



    public static String[] toStringArray(Collection collection) {
        if (collection == null) {
            return null;
        }
        return (String[]) collection.toArray(new String[collection.size()]);
    }


    public static String[] split(String toSplit, String delimiter) {
        if (!hasLength(toSplit) || !hasLength(delimiter)) {
            return null;
        }
        int offset = toSplit.indexOf(delimiter);
        if (offset < 0) {
            return null;
        }
        String beforeDelimiter = toSplit.substring(0, offset);
        String afterDelimiter = toSplit.substring(offset + delimiter.length());
        return new String[]{beforeDelimiter, afterDelimiter};
    }


    public static String[] tokenizeToStringArray(String str, String delimiters) {
        return tokenizeToStringArray(str, delimiters, true, true);
    }


    public static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {
        if (str == null) {
            return null;
        }
        StringTokenizer st = new StringTokenizer(str, delimiters);
        List<String> tokens = new ArrayList();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trimTokens) {
                token = token.trim();
            }
            if (!ignoreEmptyTokens || token.length() > 0) {
                tokens.add(token);
            }
        }
        return toStringArray(tokens);
    }


    public static String[] delimitedListToStringArray(String str, String delimiter) {
        return delimitedListToStringArray(str, delimiter, null);
    }


    public static String[] delimitedListToStringArray(String str, String delimiter, String charsToDelete) {
        if (str == null) {
            return new String[0];
        }
        if (delimiter == null) {
            return new String[]{str};
        }
        List<String> result = new ArrayList();
        if ("".equals(delimiter)) {
            for (int i = 0; i < str.length(); i++) {
                result.add(deleteAny(str.substring(i, i + 1), charsToDelete));
            }
        } else {
            int pos = 0;
            int delPos = 0;
            while ((delPos = str.indexOf(delimiter, pos)) != -1) {
                result.add(deleteAny(str.substring(pos, delPos), charsToDelete));
                pos = delPos + delimiter.length();
            }
            if (str.length() > 0 && pos <= str.length()) {
                result.add(deleteAny(str.substring(pos), charsToDelete));
            }
        }
        return toStringArray(result);
    }


    public static String[] commaDelimitedListToStringArray(String str) {
        return delimitedListToStringArray(str, ",");
    }


    public static Set commaDelimitedListToSet(String str) {
        Set<String> set = new TreeSet();
        String[] tokens = commaDelimitedListToStringArray(str);
        for (int i = 0; i < tokens.length; i++) {
            set.add(tokens[i]);
        }
        return set;
    }


    public static String[] splitBy(String str, char sp) {
        if (str == null) {
            return new String[0];
        }
        str = str.trim();
        if (str.equals("")) {
            return new String[0];
        }
        List<String> list = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        boolean inQuote = false;

        int len = str.length();
        for (int i = 0; i < len; i++) {
            char ch = str.charAt(i);
            if (ch == '\\' && i + 1 < len) {
                i++;
                ch = str.charAt(i);
                sb.append(ch);
            } else if (sb.length() != 0 || (ch != ' ' && ch != '\t' && ch != '\r' && ch != '\n')) {

                if (i + 1 == len) {
                    if (ch == '"' && inQuote) {
                        inQuote = false;
                        String str1 = sb.toString().trim();
                        sb.delete(0, sb.length());
                        list.add(str1);
                        break;
                    }
                    if (ch != sp) {
                        sb.append(ch);
                        String str1 = sb.toString().trim();
                        sb.delete(0, sb.length());
                        list.add(str1);
                        break;
                    }
                    String s = sb.toString().trim();
                    sb.delete(0, sb.length());
                    list.add(s);
                    list.add("");
                    break;
                }
                if (ch == sp && !inQuote) {
                    String s = sb.toString().trim();
                    sb.delete(0, sb.length());
                    list.add(s);


                } else if (ch == '"' && sb.length() == 0 && str.indexOf('"', i + 1) > 0) {
                    inQuote = true;
                } else if (ch == '"' && inQuote) {
                    inQuote = false;
                } else {
                    sb.append(ch);
                }
            }
        }
        String[] ret = new String[list.size()];
        list.toArray(ret);
        return ret;
    }

    public static String escape(String original) {
        StringBuffer buf = new StringBuffer(original);
        escape(buf);
        return buf.toString();
    }

    public static void escape(StringBuffer original) {
        int index = 0;

        while (index < original.length()) {
            String escaped = entityEscapeMap.get(original.substring(index, index + 1));
            if (null != escaped) {
                original.replace(index, index + 1, escaped);
                index += escaped.length();
                continue;
            }
            index++;
        }
    }


    public static String unescape(String original) {
        StringBuffer buf = new StringBuffer(original);
        unescape(buf);
        return buf.toString();
    }

    public static void unescape(StringBuffer original) {
        int index = 0;
        int semicolonIndex = 0;


        while (index < original.length()) {
            index = original.indexOf("&", index);
            if (-1 == index) {
                break;
            }
            semicolonIndex = original.indexOf(";", index);
            if (-1 != semicolonIndex && 10 > semicolonIndex - index) {
                String escaped = original.substring(index, semicolonIndex + 1);
                String entity = escapeEntityMap.get(escaped);
                if (null != entity) {
                    original.replace(index, semicolonIndex + 1, entity);
                }
                index++;
            }
        }
    }


    public static String htmlDecode(String str) {
        String s = str;
        Pattern p = Pattern.compile("(?i)&#x?(\\d|[a-f]){1,4};");
        Matcher m = p.matcher(s);
        StringBuffer sb = new StringBuffer();
        int start = 0;
        while (m.find()) {
            sb.append(s, start, m.start());
            String m1 = m.group(1);
            if (m.group().toLowerCase().startsWith("&#x")) {
                sb.append((char) Integer.parseInt(m1, 16));
            } else {
                sb.append((char) Integer.parseInt(m1));
            }
            start = m.end();
        }
        sb.append(s.substring(start));
        return unescape(sb.toString());
    }


    public static String htmlShow(String str) {
        if (str == null) {
            return null;
        }

        str = replace("<", "&lt;", str);
        str = replace(" ", "&nbsp;", str);
        str = replace("\r\n", "<br/>", str);
        str = replace("\n", "<br/>", str);
        str = replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;", str);
        return str;
    }


    public static String toLength(String str, int length) {
        if (str == null) {
            return null;
        }
        if (length <= 0) {
            return "";
        }
        try {
            if ((str.getBytes("GBK")).length <= length) {
                return str;
            }
        } catch (Exception exception) {
        }

        StringBuffer buff = new StringBuffer();

        int index = 0;

        length -= 3;
        while (length > 0) {
            char c = str.charAt(index);
            if (c < '') {
                length--;
            } else {
                length--;
                length--;
            }
            buff.append(c);
            index++;
        }
        buff.append("...");
        return buff.toString();
    }


    public static boolean isInteger(String str) {
        if (str == null || str.trim().length() == 0) return false;
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }


    public static boolean isDouble(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static boolean isNumber(String str) {
        if (str == null || str.length() == 0) return false;
        for (int i = 0; i < str.length(); ) {
            char c = str.charAt(i);
            if ((c >= '0' && c <= '9') || (
                    i == 0 && (c == '-' || c == '+'))) {
                i++;
                continue;
            }
            return false;
        }

        return true;
    }


    public static boolean isEmail(String str) {
        Pattern pattern = Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
        return pattern.matcher(str).matches();
    }


    public static boolean isChinese(String str) {
        Pattern pattern = Pattern.compile("[Α-￥]+$");
        return pattern.matcher(str).matches();
    }


    public static boolean isBlank(String str) {
        return (str == null || str.trim().length() == 0);
    }


    public static boolean isPrime(int x) {
        if (x <= 7 && (
                x == 2 || x == 3 || x == 5 || x == 7)) {
            return true;
        }
        int c = 7;
        if (x % 2 == 0)
            return false;
        if (x % 3 == 0)
            return false;
        if (x % 5 == 0)
            return false;
        int end = (int) Math.sqrt(x);
        while (c <= end) {
            if (x % c == 0) {
                return false;
            }
            c += 4;
            if (x % c == 0) {
                return false;
            }
            c += 2;
            if (x % c == 0) {
                return false;
            }
            c += 4;
            if (x % c == 0) {
                return false;
            }
            c += 2;
            if (x % c == 0) {
                return false;
            }
            c += 4;
            if (x % c == 0) {
                return false;
            }
            c += 6;
            if (x % c == 0) {
                return false;
            }
            c += 2;
            if (x % c == 0) {
                return false;
            }
            c += 6;
        }
        return true;
    }


    public static int newRandomInt(int min, int max) {
        int result = min + (new Double(Math.random() * (max - min))).intValue();

        return result;
    }

    public static String newRandomNumStr(int length) {
        StringBuffer sb = new StringBuffer();
        sb.append(newRandomInt(1, 10));
        while (--length > 0) {
            sb.append(newRandomInt(0, 10));
        }


        return sb.toString();
    }


    public static long newRandomLong(long min, long max) {
        long result = min + (new Double(Math.random() * (max - min))).longValue();

        return result;
    }

    public static byte[] newRandomByteArray(int size) {
        if (size < 0) return null;
        byte[] bytes = new byte[size];
        for (int i = 0; i < size; i++) {
            bytes[i] = (byte) (newRandomInt(0, 256) & 0xFF);
        }
        return bytes;
    }


    public static int[] newRandomIntArray(int min, int max, int size) {
        int[] result = new int[size];

        int arraySize = max - min;
        int[] intArray = new int[arraySize];
        int i;
        for (i = 0; i < intArray.length; i++) {
            intArray[i] = i + min;
        }

        for (i = 0; i < size; i++) {
            int c = newRandomInt(min, max - i);
            int index = c - min;
            swap(intArray, index, arraySize - 1 - i);
            result[i] = intArray[arraySize - 1 - i];
        }

        return result;
    }

    private static void swap(int[] array, int x, int y) {
        int temp = array[x];
        array[x] = array[y];
        array[y] = temp;
    }


    public static double newRandomDouble(double min, double max) {
        double result = min + Math.random() * (max - min);
        return result;
    }


    public static char newRandomChar() {
        int firstChar = 33;
        int lastChar = 126;
        char result = (char) newRandomInt(firstChar, lastChar + 1);
        return result;
    }


    public static char newRandomPrintableChar() {
        int number = newRandomInt(0, 62);
        int zeroChar = 48;
        int nineChar = 57;
        int aChar = 97;
        int zChar = 122;
        int AChar = 65;
        int ZChar = 90;


        if (number < 10) {
            char result = (char) newRandomInt(zeroChar, nineChar + 1);
            return result;
        }
        if (number >= 10 && number < 36) {
            char result = (char) newRandomInt(AChar, ZChar + 1);
            return result;
        }
        if (number >= 36 && number < 62) {
            char result = (char) newRandomInt(aChar, zChar + 1);
            return result;
        }
        return Character.MIN_VALUE;
    }


    public static String newRandomString(int length) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < length; i++) {
            result.append(newRandomChar());
        }
        return result.toString();
    }

    public static String newRandomXStr(int length) {
        StringBuffer sb = new StringBuffer();
        double p = 0.625D;
        for (int i = 0; i < length; i++) {
            if (Math.random() <= p) {
                sb.append((char) newRandomInt(48, 58));
            } else {
                sb.append((char) newRandomInt(65, 71));
            }
        }
        return sb.toString();
    }


    public static String newRandomPrintableString(int length) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < length; i++) {
            result.append(newRandomPrintableChar());
        }
        return result.toString();
    }

    public static String newRandomWord() {
        if (COMMON_WORDS.isEmpty()) return newRandomWord(newRandomInt(3, 12));
        return COMMON_WORDS.get(newRandomInt(0, COMMON_WORDS.size()));
    }

    public static String newRandomSentence(int n) {
        StringBuffer sb = new StringBuffer(newRandomWord());
        for (int i = 1; i < n; i++) {
            sb.append(' ').append(newRandomWord());
        }
        return sb.toString();
    }

    public static String newRandomWord(int length) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            char a;
            int t = newRandomInt(0, 26);


            if (Math.random() > 0.8D) {
                a = (char) (65 + t);
            } else {
                a = (char) (97 + t);
            }
            sb.append(a);
        }
        return sb.toString();
    }

    public static String byteArrayToString(byte[] bts, String delimiter) {
        if (bts == null || bts.length == 0) return "";
        if (delimiter == null) delimiter = "";
        StringBuffer sb = new StringBuffer();
        for (byte b : bts) {
            int t = b;
            if (t < 0) t += 256;
            if (t < 16) sb.append(0);
            sb.append(Integer.toHexString(t));
            sb.append(delimiter);
        }
        if (delimiter.length() > 0 && sb.length() > 0) sb.delete(sb.length() - delimiter.length(), sb.length());
        return sb.toString();
    }


    public static String newRandomTWID() {
        String[] ss = {"A 台北市 10", "B 台中市 11", "C 基隆市 12", "D 台南市 13", "E 高雄市 14", "F 台北县 15", "G 宜兰县 16", "H 桃园县 17", "I 嘉义市 34", "J 新竹县 18", "K 苗栗县 19", "L 台中县 20", "M 南投县 21", "N 彰化县 22", "O 新竹市 35", "P 云林县 23", "Q 嘉义县 24", "R 台南县 25", "S 高雄县 26", "T 屏东县 27", "U 花莲县 28", "V 台东县 29", "W 金门县 32", "X 澎湖县 30", "Y 阳明山 31", "Z 连江县 33"};


        int n = newRandomInt(0, ss.length);
        StringBuffer sb = new StringBuffer();
        sb.append(ss[n].charAt(0));
        if (Math.random() > 0.5D) {
            sb.append(2);
        } else {
            sb.append(1);
        }

        sb.append(newRandomInt(1000000, 10000000));

        String dizhiduiying = ss[n].substring(ss[n].length() - 2);
        int sum = Integer.parseInt(dizhiduiying.substring(0, 1)) + 9 * Integer.parseInt(dizhiduiying.substring(1, 2));
        for (int i = 1; i < 9; i++) {
            sum += Integer.parseInt(sb.substring(i, i + 1)) * (9 - i);
        }
        int mod = sum % 10;
        sb.append(10 - mod);
        return sb.toString();
    }

    public static String parseUnicode(String src) {
        if (src == null) return null;
        int start = 0;
        Pattern p = Pattern.compile("(?i)\\\\u([\\d|\\w]{4})");
        Matcher m = p.matcher(src);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            sb.append(src, start, m.start());
            try {
                char a = (char) Integer.parseInt(m.group(1), 16);
                sb.append(a);
            } catch (Exception e) {
                sb.append(m.group(0));
            }
            start = m.end();
        }
        sb.append(src.substring(start));
        return sb.toString();
    }

    public static String newRandomPassword(int min, int max) {
        String password = newRandomWord();
        if (Math.random() > 0.9D && password.length() < max - 4) password = password + newRandomWord();
        if (Math.random() > 0.6D && password.length() < max - 4)
            password = password + newRandomWord(newRandomInt(2, 4));
        if (Math.random() > 0.3D && password.length() < max - 4) password = password + newRandomInt(0, 1000);
        if (password.length() < min)
            password = password + newRandomPrintableString(min + (max - min) / 2 - password.length());
        if (password.length() > max) password = password.substring(max - newRandomInt(0, max - min));
        if (!password.matches(".*?\\d+.*?")) {
            int in = newRandomInt(0, password.length());
            password = password.substring(0, in) + newRandomInt(0, 10) + password.substring(in + 1);
        }
        return password;
    }


    public static String bytesToHex(byte[] bytes) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < bytes.length; i++) {
            int t = bytes[i];
            if (t < 0)
                t += 256;
            sb.append(hexDigits[t >>> 4]);
            sb.append(hexDigits[t % 16]);
        }
        return sb.toString();
    }


    public static int calculateStringDistance(String strA, String strB) {
        short lenA = (short) strA.length();
        short lenB = (short) strB.length();
        short[][] c = new short[lenA + 1][lenB + 1];


        for (int k = 0; k < lenA; k++)
            c[k][lenB] = (short) (lenA - k);
        for (int j = 0; j < lenB; j++)
            c[lenA][j] = (short) (lenB - j);
        c[lenA][lenB] = 0;
        for (int i = lenA - 1; i >= 0; i--) {
            for (int m = lenB - 1; m >= 0; m--) {
                if (strB.charAt(m) == strA.charAt(i)) {
                    c[i][m] = c[i + 1][m + 1];
                } else {
                    c[i][m] = (short) (Math.min(Math.min(c[i][m + 1], c[i + 1][m]), c[i + 1][m + 1]) + 1);
                }
            }
        }

        return c[0][0];
    }


    public static String getLCString(String strA, String strB) {
        int len1 = strA.length();
        int len2 = strB.length();
        int maxLen = (len1 > len2) ? len1 : len2;
        int[] max = new int[maxLen];
        int[] maxIndex = new int[maxLen];
        int[] c = new int[maxLen];
        int i;
        for (i = 0; i < len2; i++) {
            for (int k = len1 - 1; k >= 0; k--) {
                if (strB.charAt(i) == strA.charAt(k)) {
                    if (i == 0 || k == 0) {
                        c[k] = 1;
                    } else {
                        c[k] = c[k - 1] + 1;
                    }
                } else {
                    c[k] = 0;
                }

                if (c[k] > max[0]) {
                    max[0] = c[k];
                    maxIndex[0] = k;
                    for (int m = 1; m < maxLen; m++) {
                        max[m] = 0;
                        maxIndex[m] = 0;
                    }
                } else if (c[k] == max[0]) {
                    for (int m = 1; m < maxLen; m++) {
                        if (max[m] == 0) {
                            max[m] = c[k];
                            maxIndex[m] = k;

                            break;
                        }
                    }
                }
            }
        }

        for (int j = 0; j < maxLen; j++) {
            if (max[j] > 0) {

                StringBuffer sb = new StringBuffer();
                for (i = maxIndex[j] - max[j] + 1; i <= maxIndex[j]; i++) {
                    sb.append(strA.charAt(i));
                }

                return sb.toString();
            }
        }
        return "";
    }

    public static void main(String[] args) {
        String str1 = "adbbda1234";
        String str2 = "adbbdf1234sa";
        System.out.println(getLCString(str1, str2));
        System.out.println(calculateStringDistance(str1, str2));
    }
}


/* Location:              C:\Users\fred\Downloads\bet-server-1.0-SNAPSHOT\BOOT-INF\lib\bet-common-1.0.0-SNAPSHOT.jar!\com\chief\ww\\util\StringUtils.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */