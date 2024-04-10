package com.chieffu.pocker.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public abstract class ConfigUtil {
    static File file = new File("config.ini");
    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    public static String getDateString() {
        return df.format(new Date());
    }

    private static final Properties settings = new Properties();
    private static long configInilastModified;

    public static String getSetting(String key, String defaultValue) {
        loadConfigIni();
        String r = settings.getProperty(key);
        if (r != null) return r;
        return defaultValue;
    }

    public static String getSetting(String key) {
        loadConfigIni();
        return settings.getProperty(key);
    }

    private static void loadConfigIni() {
        BufferedReader br = null;

        synchronized (settings) {
            if (file.exists() && file.lastModified() > configInilastModified) {

                try {
                    br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
                    settings.load(br);
                } catch (Exception exception) {
                } finally {
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException iOException) {
                        }
                    }
                }


                configInilastModified = file.lastModified();
            }
        }
    }


    public static void addSetting(String key, String value) {
        settings.setProperty(key, value);
    }

    public static void saveConfigIni() {
        PrintWriter pw = null;

        try {
            pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File("config.ini")), StandardCharsets.UTF_8));
            settings.store(pw, "auto save config.ini");
        } catch (FileNotFoundException fileNotFoundException) {
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
        } catch (IOException iOException) {
        } finally {
            if (pw != null)
                pw.close();
        }

    }

    public static void main(String[] args) {
        System.out.println(getSetting("abc"));
        System.out.println(getSetting("abc"));
    }
}
