package com.chieffu.pocker.util;

import java.lang.reflect.Array;
import java.util.Arrays;


public abstract class ObjectUtils {
    private static final int INITIAL_HASH = 7;
    private static final int MULTIPLIER = 31;
    private static final String EMPTY_STRING = "";
    private static final String NULL_STRING = "null";
    private static final String ARRAY_START = "{";
    private static final String ARRAY_END = "}";
    private static final String EMPTY_ARRAY = "{}";
    private static final String ARRAY_ELEMENT_SEPARATOR = ", ";

    public static boolean isCheckedException(Throwable ex) {
        return (!(ex instanceof RuntimeException) && !(ex instanceof Error));
    }


    public static boolean isCompatibleWithThrowsClause(Throwable ex, Class[] declaredExceptions) {
        if (!isCheckedException(ex)) {
            return true;
        }
        if (declaredExceptions != null) {
            for (int i = 0; i < declaredExceptions.length; i++) {
                if (declaredExceptions[i].isAssignableFrom(ex.getClass())) {
                    return true;
                }
            }
        }
        return false;
    }


    public static boolean isEmpty(Object[] array) {
        return (array == null || array.length == 0);
    }


    public static Object[] addObjectToArray(Object[] array, Object obj) {
        Class<Object> compType = Object.class;
        if (array != null) {
            compType = (Class) array.getClass().getComponentType();
        } else if (obj != null) {
            compType = (Class) obj.getClass();
        }
        int newArrLength = (array != null) ? (array.length + 1) : 1;
        Object[] newArr = (Object[]) Array.newInstance(compType, newArrLength);
        if (array != null) {
            System.arraycopy(array, 0, newArr, 0, array.length);
        }
        newArr[newArr.length - 1] = obj;
        return newArr;
    }


    public static Object[] toObjectArray(Object source) {
        if (source instanceof Object[]) {
            return (Object[]) source;
        }
        if (source == null) {
            return new Object[0];
        }
        if (!source.getClass().isArray()) {
            throw new IllegalArgumentException("Source is not an array: " + source);
        }
        int length = Array.getLength(source);
        if (length == 0) {
            return new Object[0];
        }
        Class<?> wrapperType = Array.get(source, 0).getClass();
        Object[] newArray = (Object[]) Array.newInstance(wrapperType, length);
        for (int i = 0; i < length; i++) {
            newArray[i] = Array.get(source, i);
        }
        return newArray;
    }


    public static boolean nullSafeEquals(Object o1, Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        if (o1.equals(o2)) {
            return true;
        }
        if (o1 instanceof Object[] && o2 instanceof Object[]) {
            return Arrays.equals((Object[]) o1, (Object[]) o2);
        }
        if (o1 instanceof boolean[] && o2 instanceof boolean[]) {
            return Arrays.equals((boolean[]) o1, (boolean[]) o2);
        }
        if (o1 instanceof byte[] && o2 instanceof byte[]) {
            return Arrays.equals((byte[]) o1, (byte[]) o2);
        }
        if (o1 instanceof char[] && o2 instanceof char[]) {
            return Arrays.equals((char[]) o1, (char[]) o2);
        }
        if (o1 instanceof double[] && o2 instanceof double[]) {
            return Arrays.equals((double[]) o1, (double[]) o2);
        }
        if (o1 instanceof float[] && o2 instanceof float[]) {
            return Arrays.equals((float[]) o1, (float[]) o2);
        }
        if (o1 instanceof int[] && o2 instanceof int[]) {
            return Arrays.equals((int[]) o1, (int[]) o2);
        }
        if (o1 instanceof long[] && o2 instanceof long[]) {
            return Arrays.equals((long[]) o1, (long[]) o2);
        }
        if (o1 instanceof short[] && o2 instanceof short[]) {
            return Arrays.equals((short[]) o1, (short[]) o2);
        }
        return false;
    }


    public static int nullSafeHashCode(Object obj) {
        if (obj == null) {
            return 0;
        }
        if (obj instanceof Object[]) {
            return nullSafeHashCode((Object[]) obj);
        }
        if (obj instanceof boolean[]) {
            return nullSafeHashCode((boolean[]) obj);
        }
        if (obj instanceof byte[]) {
            return nullSafeHashCode((byte[]) obj);
        }
        if (obj instanceof char[]) {
            return nullSafeHashCode((char[]) obj);
        }
        if (obj instanceof double[]) {
            return nullSafeHashCode((double[]) obj);
        }
        if (obj instanceof float[]) {
            return nullSafeHashCode((float[]) obj);
        }
        if (obj instanceof int[]) {
            return nullSafeHashCode((int[]) obj);
        }
        if (obj instanceof long[]) {
            return nullSafeHashCode((long[]) obj);
        }
        if (obj instanceof short[]) {
            return nullSafeHashCode((short[]) obj);
        }
        return obj.hashCode();
    }


    public static int nullSafeHashCode(Object[] array) {
        if (array == null) {
            return 0;
        }
        int hash = 7;
        int arraySize = array.length;
        for (int i = 0; i < arraySize; i++) {
            hash = 31 * hash + nullSafeHashCode(array[i]);
        }
        return hash;
    }


    public static int nullSafeHashCode(boolean[] array) {
        if (array == null) {
            return 0;
        }
        int hash = 7;
        int arraySize = array.length;
        for (int i = 0; i < arraySize; i++) {
            hash = 31 * hash + hashCode(array[i]);
        }
        return hash;
    }


    public static int nullSafeHashCode(byte[] array) {
        if (array == null) {
            return 0;
        }
        int hash = 7;
        int arraySize = array.length;
        for (int i = 0; i < arraySize; i++) {
            hash = 31 * hash + array[i];
        }
        return hash;
    }


    public static int nullSafeHashCode(char[] array) {
        if (array == null) {
            return 0;
        }
        int hash = 7;
        int arraySize = array.length;
        for (int i = 0; i < arraySize; i++) {
            hash = 31 * hash + array[i];
        }
        return hash;
    }


    public static int nullSafeHashCode(double[] array) {
        if (array == null) {
            return 0;
        }
        int hash = 7;
        int arraySize = array.length;
        for (int i = 0; i < arraySize; i++) {
            hash = 31 * hash + hashCode(array[i]);
        }
        return hash;
    }


    public static int nullSafeHashCode(float[] array) {
        if (array == null) {
            return 0;
        }
        int hash = 7;
        int arraySize = array.length;
        for (int i = 0; i < arraySize; i++) {
            hash = 31 * hash + hashCode(array[i]);
        }
        return hash;
    }


    public static int nullSafeHashCode(int[] array) {
        if (array == null) {
            return 0;
        }
        int hash = 7;
        int arraySize = array.length;
        for (int i = 0; i < arraySize; i++) {
            hash = 31 * hash + array[i];
        }
        return hash;
    }


    public static int nullSafeHashCode(long[] array) {
        if (array == null) {
            return 0;
        }
        int hash = 7;
        int arraySize = array.length;
        for (int i = 0; i < arraySize; i++) {
            hash = 31 * hash + hashCode(array[i]);
        }
        return hash;
    }


    public static int nullSafeHashCode(short[] array) {
        if (array == null) {
            return 0;
        }
        int hash = 7;
        int arraySize = array.length;
        for (int i = 0; i < arraySize; i++) {
            hash = 31 * hash + array[i];
        }
        return hash;
    }


    public static int hashCode(boolean bool) {
        return bool ? 1231 : 1237;
    }


    public static int hashCode(double dbl) {
        long bits = Double.doubleToLongBits(dbl);
        return hashCode(bits);
    }


    public static int hashCode(float flt) {
        return Float.floatToIntBits(flt);
    }


    public static int hashCode(long lng) {
        return (int) (lng ^ lng >>> 32L);
    }


    public static String identityToString(Object obj) {
        if (obj == null) {
            return "";
        }
        return obj.getClass().getName() + "@" + getIdentityHexString(obj);
    }


    public static String getIdentityHexString(Object obj) {
        return Integer.toHexString(System.identityHashCode(obj));
    }


    public static String getDisplayString(Object obj) {
        if (obj == null) {
            return "";
        }
        return nullSafeToString(obj);
    }


    public static String nullSafeClassName(Object obj) {
        return (obj != null) ? obj.getClass().getName() : "null";
    }


    public static String nullSafeToString(Object obj) {
        if (obj == null) {
            return "";
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        if (obj instanceof Object[]) {
            return nullSafeToString((Object[]) obj);
        }
        if (obj instanceof boolean[]) {
            return nullSafeToString((boolean[]) obj);
        }
        if (obj instanceof byte[]) {
            return nullSafeToString((byte[]) obj);
        }
        if (obj instanceof char[]) {
            return nullSafeToString((char[]) obj);
        }
        if (obj instanceof double[]) {
            return nullSafeToString((double[]) obj);
        }
        if (obj instanceof float[]) {
            return nullSafeToString((float[]) obj);
        }
        if (obj instanceof int[]) {
            return nullSafeToString((int[]) obj);
        }
        if (obj instanceof long[]) {
            return nullSafeToString((long[]) obj);
        }
        if (obj instanceof short[]) {
            return nullSafeToString((short[]) obj);
        }
        String str = obj.toString();
        return (str != null) ? str : "";
    }


    public static String nullSafeToString(Object[] array) {
        if (array == null) {
            return "null";
        }
        int length = array.length;
        if (length == 0) {
            return "{}";
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            if (i == 0) {
                buffer.append("{");
            } else {

                buffer.append(", ");
            }
            buffer.append(array[i]);
        }
        buffer.append("}");
        return buffer.toString();
    }


    public static String nullSafeToString(boolean[] array) {
        if (array == null) {
            return "null";
        }
        int length = array.length;
        if (length == 0) {
            return "{}";
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            if (i == 0) {
                buffer.append("{");
            } else {

                buffer.append(", ");
            }

            buffer.append(array[i]);
        }
        buffer.append("}");
        return buffer.toString();
    }


    public static String nullSafeToString(byte[] array) {
        if (array == null) {
            return "null";
        }
        int length = array.length;
        if (length == 0) {
            return "{}";
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            if (i == 0) {
                buffer.append("{");
            } else {

                buffer.append(", ");
            }
            buffer.append(array[i]);
        }
        buffer.append("}");
        return buffer.toString();
    }


    public static String nullSafeToString(char[] array) {
        if (array == null) {
            return "null";
        }
        int length = array.length;
        if (length == 0) {
            return "{}";
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            if (i == 0) {
                buffer.append("{");
            } else {

                buffer.append(", ");
            }
            buffer.append("'").append(array[i]).append("'");
        }
        buffer.append("}");
        return buffer.toString();
    }


    public static String nullSafeToString(double[] array) {
        if (array == null) {
            return "null";
        }
        int length = array.length;
        if (length == 0) {
            return "{}";
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            if (i == 0) {
                buffer.append("{");
            } else {

                buffer.append(", ");
            }

            buffer.append(array[i]);
        }
        buffer.append("}");
        return buffer.toString();
    }


    public static String nullSafeToString(float[] array) {
        if (array == null) {
            return "null";
        }
        int length = array.length;
        if (length == 0) {
            return "{}";
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            if (i == 0) {
                buffer.append("{");
            } else {

                buffer.append(", ");
            }

            buffer.append(array[i]);
        }
        buffer.append("}");
        return buffer.toString();
    }


    public static String nullSafeToString(int[] array) {
        if (array == null) {
            return "null";
        }
        int length = array.length;
        if (length == 0) {
            return "{}";
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            if (i == 0) {
                buffer.append("{");
            } else {

                buffer.append(", ");
            }
            buffer.append(array[i]);
        }
        buffer.append("}");
        return buffer.toString();
    }


    public static String nullSafeToString(long[] array) {
        if (array == null) {
            return "null";
        }
        int length = array.length;
        if (length == 0) {
            return "{}";
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            if (i == 0) {
                buffer.append("{");
            } else {

                buffer.append(", ");
            }
            buffer.append(array[i]);
        }
        buffer.append("}");
        return buffer.toString();
    }


    public static String nullSafeToString(short[] array) {
        if (array == null) {
            return "null";
        }
        int length = array.length;
        if (length == 0) {
            return "{}";
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            if (i == 0) {
                buffer.append("{");
            } else {

                buffer.append(", ");
            }
            buffer.append(array[i]);
        }
        buffer.append("}");
        return buffer.toString();
    }
}


/* Location:              C:\Users\fred\Downloads\bet-server-1.0-SNAPSHOT\BOOT-INF\lib\bet-common-1.0.0-SNAPSHOT.jar!\com\chief\ww\\util\ObjectUtils.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */