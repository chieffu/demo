package com.chieffu.pocker.util;

import org.apache.http.client.HttpClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class Ruokuai {
    static final Semaphore available = new Semaphore(100, true);
    static Browser br = new Browser();
    static HttpClient client;

    public static String inputVcode(InputStream input) throws IOException {
        return inputVcode(input, null);
    }

    public static String inputVcode(InputStream input, Browser browser) throws IOException {
        try {
            byte[] image;
            if (browser == null) {
                browser = br;
            }
            if (input == null) return null;
            try {
                available.acquire();
            } catch (InterruptedException interruptedException) {
            }

            Map<String, Object> params = new HashMap<>();


            String username = ConfigUtil.getSetting("ruokuai.username", "fhrj0001");
            String password = ConfigUtil.getSetting("ruokuai.password", "fahairuanjian1");
            String saveCode = ConfigUtil.getSetting("ruokuai.image.save", "false");
            Link link = new Link("http://api.ruokuai.com/create.txt");


            if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
                throw new IOException("No ruokuai.username or ruokuai.password setted.");
            }
            params.put("username", username);
            params.put("password", password);
            params.put("typeid", "5000");
            params.put("softid", "17014");
            params.put("softkey", "7a0f2adb459fabbf091c180e7ca495e1");

            try {
                image = FileCopyUtils.copyToByteArray(input);
            } finally {
                try {
                    input.close();
                } catch (Exception exception) {
                }
            }

            params.put("image", image);
            browser.setAttribute("__ID__", null);
            String content = browser.postDataAndFileWithSelfDefineHeaders(client, link, params, null, new HashMap<>());
            if ("true".equalsIgnoreCase(saveCode)) {
                try {
                    if (content.contains("|")) {
                        String[] ss = StringUtils.delimitedListToStringArray(content, "|");
                        if (!"-1".equals(ss[1])) {
                            File folder = new File(ConfigUtil.getSetting("ruokuai.image.save.path", "vcode"));
                            if (!folder.exists()) {
                                folder.mkdirs();
                            }
                            FileCopyUtils.copy(image, new File(folder, ss[0] + "_" + ss[1] + ".jpg"));
                        }
                    }
                } catch (Exception exception) {
                }
            }

            return content;
        } finally {
            available.release();
        }
    }

    public static void reportError(String id) throws IOException {
        Browser browser = new Browser();
        Map<String, Object> params = new HashMap<>();


        Link link = new Link("http://api.ruokuai.com/reporterror.txt");


        String username = ConfigUtil.getSetting("ruokuai.username");
        String password = ConfigUtil.getSetting("ruokuai.password");
        params.put("username", username);
        params.put("password", password);
        params.put("id", id);
        params.put("softid", "1867");
        params.put("softkey", "7b1cc3e2bd77954f61a092ad4a5cb009");
        String content = browser.postDataAndFileWithSelfDefineHeaders(link, params, null, new HashMap<>());
        System.out.println("ReportError:" + content);
    }
}


/* Location:              C:\Users\fred\Downloads\bet-server-1.0-SNAPSHOT\BOOT-INF\lib\bet-common-1.0.0-SNAPSHOT.jar!\com\chief\ww\\util\Ruokuai.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */