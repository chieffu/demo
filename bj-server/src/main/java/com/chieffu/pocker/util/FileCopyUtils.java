package com.chieffu.pocker.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public abstract class FileCopyUtils {
    private static final Log logger = LogFactory.getLog(FileCopyUtils.class);


    public static final int BUFFER_SIZE = 4096;


    public static int copy(File in, File out) throws IOException {
        Assert.notNull(in, "No input File specified");
        Assert.notNull(out, "No output File specified");
        return copy(new BufferedInputStream(new FileInputStream(in)), new BufferedOutputStream(new FileOutputStream(out)));
    }


    public static void copy(byte[] in, File out) throws IOException {
        Assert.notNull(in, "No input byte array specified");
        Assert.notNull(out, "No output File specified");
        ByteArrayInputStream inStream = new ByteArrayInputStream(in);
        OutputStream outStream = new BufferedOutputStream(new FileOutputStream(out));
        copy(inStream, outStream);
    }


    public static byte[] copyToByteArray(File in) throws IOException {
        Assert.notNull(in, "No input File specified");
        return copyToByteArray(new BufferedInputStream(new FileInputStream(in)));
    }


    public static int copy(InputStream in, OutputStream out) throws IOException {
        Assert.notNull(in, "No InputStream specified");
        Assert.notNull(out, "No OutputStream specified");
        try {
            int byteCount = 0;
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                byteCount += bytesRead;
            }
            out.flush();
            return byteCount;
        } finally {

            try {
                in.close();
            } catch (IOException ex) {
                logger.warn("Could not close InputStream", ex);
            }
            try {
                out.close();
            } catch (IOException ex) {
                logger.warn("Could not close OutputStream", ex);
            }
        }
    }


    public static void copy(byte[] in, OutputStream out) throws IOException {
        Assert.notNull(in, "No input byte array specified");
        Assert.notNull(out, "No OutputStream specified");
        try {
            out.write(in);
        } finally {

            try {
                out.close();
            } catch (IOException ex) {
                logger.warn("Could not close OutputStream", ex);
            }
        }
    }


    public static byte[] copyToByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
        copy(in, out);
        return out.toByteArray();
    }


    public static int copy(Reader in, Writer out) throws IOException {
        Assert.notNull(in, "No Reader specified");
        Assert.notNull(out, "No Writer specified");
        try {
            int byteCount = 0;
            char[] buffer = new char[4096];
            int bytesRead = -1;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                byteCount += bytesRead;
            }
            out.flush();
            return byteCount;
        } finally {

            try {
                in.close();
            } catch (IOException ex) {
                logger.warn("Could not close Reader", ex);
            }
            try {
                out.close();
            } catch (IOException ex) {
                logger.warn("Could not close Writer", ex);
            }
        }
    }


    public static void copy(String in, Writer out) throws IOException {
        Assert.notNull(in, "No input String specified");
        Assert.notNull(out, "No Writer specified");
        try {
            out.write(in);
        } finally {

            try {
                out.close();
            } catch (IOException ex) {
                logger.warn("Could not close Writer", ex);
            }
        }
    }


    public static String copyToString(Reader in) throws IOException {
        StringWriter out = new StringWriter();
        copy(in, out);
        return out.toString();
    }

    public static void deleteAll(File f) {
        if (f.isDirectory()) {
            File[] files = f.listFiles();
            for (File file : files) {
                deleteAll(file);
            }
        }
        f.delete();
    }

    public static List<File> getAllFiles(File f) {
        return getAllFiles(f, null);
    }

    public static List<File> getAllFiles(File f, FileFilter filter) {
        List<File> list = new ArrayList<>();
        if (f.isDirectory()) {
            File[] files = f.listFiles();
            for (File file : files) {
                list.addAll(getAllFiles(file, filter));
            }
        } else if (filter == null || filter.accept(f)) {
            list.add(f);
        }

        return list;
    }


    public static List<String> readContent(File file) {
        List<String> contents = new ArrayList<>();
        if (file.exists()) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
                String tmp = null;
                while ((tmp = br.readLine()) != null) {
                    contents.add(tmp);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException iOException) {
                    }
                }
            }
        }

        return contents;
    }
}


/* Location:              C:\Users\fred\Downloads\bet-server-1.0-SNAPSHOT\BOOT-INF\lib\bet-common-1.0.0-SNAPSHOT.jar!\com\chief\ww\\util\FileCopyUtils.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */