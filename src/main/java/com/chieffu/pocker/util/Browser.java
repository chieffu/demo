package com.chieffu.pocker.util;

import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Browser {
    private static final Logger log = LoggerFactory.getLogger(Browser.class);

    private static final String HTML_META_CHARSET_REGEX = "(?i)(<meta\\s*http-equiv\\s*=\\s*(\"|')content-type(\"|')\\s*content\\s*=\\s*(\"|')text/?\\w+;\\s*charset\\s*=\\s*(.*?)(\"|')\\s*/?>)";
    private static boolean pause = false;
    private static final ReentrantLock pauseLock = new ReentrantLock();
    private static final String REFERER = "Referer";
    private static final Condition unpaused = pauseLock.newCondition();
    private static final Map<String, String> DNT_HEADER = new HashMap<String, String>() {
    };
    private Account account;
    private String currentContent;
    private Link currentLink;

    public static void pause() {
        pauseLock.lock();
        try {
            pause = true;
        } finally {
            pauseLock.unlock();
        }
    }

    public static void resume() {
        pauseLock.lock();
        try {
            pause = false;
            unpaused.signalAll();
        } finally {
            pauseLock.unlock();
        }
    }

    public static String getAUserAgent() {
        String userAgent = ConfigUtil.getSetting("User-Agent");
        if (userAgent != null) {
            return userAgent;
        }
        String agentFile = ConfigUtil.getSetting("User-Agent-List-File", "user-agent.list");
        if ((new File(agentFile)).exists() &&
                ConfigUtil.getSetting("User-Agents") == null) {
            try {
                String agents = FileCopyUtils.copyToString(new InputStreamReader(new FileInputStream(agentFile), StandardCharsets.UTF_8));
                ConfigUtil.addSetting("User-Agents", agents);
            } catch (IOException e) {

                e.printStackTrace();
            }
        }

        if (ConfigUtil.getSetting("User-Agents") != null && ConfigUtil.getSetting("User-Agents").trim().length() > 0) {
            String[] array = StringUtils.delimitedListToStringArray(ConfigUtil.getSetting("User-Agents"), "\n");
            List<String> ags = new ArrayList<>();
            for (String a : array) {
                if (a != null && a.trim().length() > 0) {
                    ags.add(a.trim());
                }
            }
            if (ags.size() > 0) {
                return ags.get(StringUtils.newRandomInt(0, array.length));
            }
        }
        File userAgentsFile = new File("User-Agent.txt");
        if (userAgentsFile.exists()) {
            List<String> contents = FileCopyUtils.readContent(userAgentsFile);
            for (int i = contents.size() - 1; i >= 0; i--) {
                if (StringUtils.isBlank(contents.get(i))) {
                    contents.remove(i);
                }
            }
            if (contents.size() > 0) {
                return contents.get(StringUtils.newRandomInt(0, contents.size()));
            }
        }
        return "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0";
    }


    private final BasicCookieStore cookieStore = new BasicCookieStore();

    private final HttpClient httpClient;
    private HttpHost proxy;
    private boolean enableLog = true;
    private StatusLine statusLine;
    private int redirectCount;
    private boolean isShutDown;
    private String charset = "UTF-8";
    private Object attachement;
    private final String userAgent = getAUserAgent();
    private final Map<String, Object> attributes = new ConcurrentHashMap<>();
    private int connectionTimeout = 6000;
    private int readTimeout = 6000;

    public CloseableHttpClient newHttpClient() {
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
        manager.setMaxTotal(1000);
        manager.setDefaultMaxPerRoute(100);

        return HttpClientBuilder.create()
                .setRedirectStrategy(new LaxRedirectStrategy())
                .setConnectionManager(manager)
                .setDefaultCookieStore(this.cookieStore)
                .setRetryHandler(new HttpRequestRetryHandler() {
                    public boolean retryRequest(IOException e, int ec, HttpContext context) {
                        if (ec > 3) {
                            Browser.log.warn("Maximun tries reached for client http pool");
                            return false;
                        }
                        if (!(e instanceof org.apache.http.NoHttpResponseException)) {
                            if (e instanceof java.net.SocketException && e.getMessage() != null && e.getMessage().contains("Connection reset")) {
                                Browser.log.warn(e.getMessage() + ". Retry count:" + ec + " call");
                                return true;
                            }
                            return false;
                        }

                        Browser.log.info("NoHttpResponseException on " + ec + " call");
                        return true;
                    }
                }).build();
    }


    public Object getAttachement() {
        return this.attachement;
    }

    public void setAttribute(String name, Object value) {
        this.attributes.put(name, value);
    }

    public Object getAttribute(String name) {
        return this.attributes.get(name);
    }

    public void setAttachement(Object attachement) {
        this.attachement = attachement;
    }

    public Browser() {
        this.httpClient = newHttpClient();
    }

    public Browser(Account acc) {
        this();
        setAccount(acc);
    }


    public Browser(String proxyHost, int proxyPort) {
        this();
        setProxy(proxyHost, proxyPort);
    }


    protected void afterClick() {
    }


    public void setCookie(String name, String value) {
        setCookie(name, value, getCurrentHost(), "/", false, null);
    }

    public void setCookie(String name, String value, String domain, String path) {
        setCookie(name, value, domain, path, false, null);
    }

    public void setCookie(String name, String value, String domain, String path, Date expiry) {
        setCookie(name, value, domain, path, false, expiry);
    }

    public void setCookie(String name, String value, String domain, String path, boolean secure, Date expiry) {
        BasicClientCookie cookie = new BasicClientCookie(name, value);
        cookie.setDomain(domain);
        cookie.setPath(path);
        cookie.setSecure(secure);
        if (expiry != null) {
            cookie.setExpiryDate(expiry);
        }
        this.cookieStore.addCookie(cookie);
    }

    public void removeCookie(String name) {
        List<Cookie> cookies = this.cookieStore.getCookies();
        for (Cookie cook : cookies) {
            if (cook.getName().equals(name)) {
                BasicClientCookie cookie = (BasicClientCookie) cook;
                cookie.setExpiryDate(new Date(System.currentTimeMillis() - 10000L));
            }
        }
        this.cookieStore.clearExpired(new Date());
    }

    public String getAllCookieStr() {
        List<Cookie> cookies = this.cookieStore.getCookies();
        StringBuffer sb = new StringBuffer();
        for (Cookie cookie : cookies) {
            if (sb.length() > 0) {
                sb.append(";");
            }
            sb.append(cookie.getName()).append("=").append(cookie.getValue());
        }
        return sb.toString();
    }


    public String getCookie(String name) {
        if (getCurrentLink() == null) return null;
        try {
            List<Cookie> cookies = this.cookieStore.getCookies();
            URL url = new URL(getCurrentLink().getUrl());
            String host = url.getHost();
            String path = url.getPath();
            if (path == null || path.length() == 0) {
                path = "/";
            }
            Date date = new Date();
            List<Cookie> posibleCookies = new ArrayList<>();
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    posibleCookies.add(cookie);
                }
            }
            if (posibleCookies.size() == 1) {
                return posibleCookies.get(0).getValue();
            }
            if (posibleCookies.size() == 0) {
                return null;
            }
            for (Cookie cookie : posibleCookies) {
                if (host.contains(cookie.getDomain()) && path.contains(cookie.getPath())) {
                    return cookie.getValue();
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public void clearCookie() {
        this.cookieStore.clear();
    }

    protected void beforeClick() {
        pauseLock.lock();
        try {
            while (pause)
                unpaused.await();
        } catch (InterruptedException interruptedException) {

        } finally {
            pauseLock.unlock();
        }
    }

    private String checkRedirect(String url, HttpResponse response) throws IOException {
        if (this.redirectCount >= 20) {
            this.redirectCount = 0;
            throw new IOException("Circle redirection ! url:" + url);
        }
        this.redirectCount++;
        int statusCode = response.getStatusLine().getStatusCode();
        if (isRedicrected(response)) {
            Header[] headers = response.getHeaders("location");
            String location = null;
            if (headers != null && headers.length > 0) {
                Header locationHeader = headers[headers.length - 1];
                location = locationHeader.getValue();
                String[] urls = location.split(",");
                location = urls[urls.length - 1];
                if (location.equals(url)) {
                    log.warn("Circle redirection ! url:" + location);
                }
                return click(new Link(location, this.currentLink));
            }
            log.warn("Redirect Faild! Location field value is null.");
        } else if (isRefresh(response)) {
            Header[] headers = response.getHeaders("Refresh");
            if (headers != null && headers.length > 0) {
                Header refreshHeader = headers[headers.length - 1];
                String refresh = refreshHeader.getValue();

                String[] urls = refresh.split(";");
                refresh = urls[urls.length - 1].trim();
                if (refresh.toLowerCase().startsWith("url=")) {
                    refresh = refresh.substring(4);
                }
                return click(new Link(refresh, this.currentLink));
            }
            log.warn("Refresh Faild! url not found.");
        } else if (statusCode != 200) {
            log.warn("Method failed: " + response.getStatusLine());
            return response.getStatusLine().toString();
        }
        return "";
    }


    public String click(Link link, Map<String, String> selfHeader) throws IOException {
        beforeClick();
        this.currentLink = link;

        this.currentContent = get(link, selfHeader);
        afterClick();
        return this.currentContent;
    }


    public String click(Link link) throws IOException {
        return click(link, DNT_HEADER);
    }

    public String get(String link) throws IOException {
        return get(new Link(link));
    }

    public String get(Link link) throws IOException {
        return get(link, null);
    }

    public String get(Link link, Map<String, String> selfHeader) throws IOException {
        String url = parseUri(link);
        if (this.enableLog) {
            log.info("Connection url : " + url);
        } else {
            log.debug("Connection url : " + url);
        }
        HttpResponse response = null;
        HttpGet get = new HttpGet(url);
        if (this.proxy != null) {
            get.setConfig(RequestConfig.custom()
                    .setProxy(this.proxy).build());
        }
        try {
            get.addHeader("User-Agent", this.userAgent);
            if (selfHeader != null) {
                for (Map.Entry<String, String> en : selfHeader.entrySet()) {
                    get.addHeader(en.getKey(), en.getValue());
                }
                if (!selfHeader.containsKey("DNT")) {
                    selfHeader.put("DNT", "1");
                }
            }

            if (link.getReferer() != null) {
                get.addHeader("Referer", link.getReferer().getUrl());
            }

            RequestConfig requestConfig = newRequestConfig();
            get.setConfig(requestConfig);
            HttpCoreContext context = new HttpCoreContext();
            response = this.httpClient.execute(get, context);
            HttpRequestWrapper wrapper = (HttpRequestWrapper) context.getAttribute("http.request");
            if (wrapper != null) {
                HttpGet original = (HttpGet) wrapper.getOriginal();
                if (original != get) {
                    this.currentLink = new Link(original.getURI().toString(), getCurrentLink());
                }
            }
            this.statusLine = response.getStatusLine();
            if (!isRedicrected(response)) {
                return parseToString(response.getEntity());
            }
        } finally {
            if (response != null && response.getEntity() != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (Exception exception) {
                }
            }
        }

        return checkRedirect(url, response);
    }


    public Account getAccount() {
        return this.account;
    }

    public String getCurrentContent() {
        return (this.currentContent == null) ? "" : this.currentContent;
    }

    public Link getCurrentLink() {
        return this.currentLink;
    }

    public String getCurrentHost() {
        if (getCurrentLink() == null) return null;
        String url = getCurrentLink().getUrl();
        try {
            return (new URL(url)).getHost();
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public HttpClient getHttpClient() {
        return this.httpClient;
    }

    public InputStream getInputStreamWithSelfHeader(Link link, Map<String, String> headers) throws IOException {
        return getInputStreamWithSelfHeader(this.httpClient, link, headers);
    }

    public InputStream getInputStreamWithSelfHeader(HttpClient client, Link link, Map<String, String> headers) throws IOException {
        if (this.enableLog) {
            log.info("get InputStream : " + link.getUrl());
        } else {
            log.debug("get InputStream : " + link.getUrl());
        }
        HttpEntity entity = null;
        HttpGet get = new HttpGet(link.getUrl());
        if (headers != null) {
            for (Map.Entry<String, String> en : headers.entrySet()) {
                get.addHeader(en.getKey(), en.getValue());
            }
        }
        HttpCoreContext context = new HttpCoreContext();
        HttpResponse response = this.httpClient.execute(get, context);
        HttpRequestWrapper wrapper = (HttpRequestWrapper) context.getAttribute("http.request");
        if (wrapper != null) {
            HttpGet original = (HttpGet) wrapper.getOriginal();
            if (original != get) {
                this.currentLink = new Link(original.getURI().toString(), getCurrentLink());
            }
        }
        this.statusLine = response.getStatusLine();
        if (!isRedicrected(response)) {
            entity = response.getEntity();
            return entity.getContent();
        }
        Header[] hds = response.getHeaders("location");
        String location = null;
        if (hds != null && hds.length > 0) {
            Header locationHeader = hds[hds.length - 1];
            location = locationHeader.getValue();
            String[] urls = location.split(",");
            location = urls[urls.length - 1];
            if (location.equals(link.toString())) {
                throw new IOException("Circle redirection ! url:" + location);
            }
            return getInputStream(new Link(location, this.currentLink));
        }
        log.warn("Redirect Faild! Location field value is null.");
        entity = response.getEntity();
        return entity.getContent();
    }


    public InputStream getInputStream(HttpClient httpClient, String url) throws IOException {
        return getInputStreamWithSelfHeader(httpClient, new Link(url), null);
    }


    public InputStream getInputStream(Link link, Map<String, String> headers) throws IOException {
        return getInputStreamWithSelfHeader(this.httpClient, link, headers);
    }

    public InputStream getInputStream(String link) throws IOException {
        return getInputStream(new Link(link));
    }


    public InputStream getInputStream(Link link) throws IOException {
        return getInputStreamWithSelfHeader(this.httpClient, link, null);
    }

    private boolean isRedicrected(HttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        return (300 <= statusCode && statusCode < 400);
    }

    private String parseToString(HttpEntity entity) throws IOException {
        this.redirectCount = 0;
        byte[] byts = EntityUtils.toByteArray(entity);

        String mimeType = EntityUtils.getContentMimeType(entity);
        String charset = EntityUtils.getContentCharSet(entity);
        if (mimeType == null || mimeType.contains("text")) {
            String firstBodyKb = new String(byts, 0, Math.min(byts.length, 1024), StandardCharsets.US_ASCII);
            Matcher matcher = Pattern.compile("(?i)(<meta\\s*http-equiv\\s*=\\s*(\"|')content-type(\"|')\\s*content\\s*=\\s*(\"|')text/?\\w+;\\s*charset\\s*=\\s*(.*?)(\"|')\\s*/?>)", 2).matcher(firstBodyKb);
            if (matcher.find()) {
                String foundCharset = matcher.group(5);
                try {
                    if (Charset.isSupported(foundCharset)) {
                        charset = foundCharset;
                    }
                } catch (IllegalCharsetNameException illegalCharsetNameException) {
                }
            }
        }

        String result = null;
        if (charset == null || charset.equals("")) {
            try {
                result = new String(byts, StandardCharsets.UTF_8);
            } catch (Exception e) {
                result = new String(byts);
            }
        } else {
            result = new String(byts, charset);
        }
        return result;
    }


    public String post(Link link, Map<String, ? extends Object> params) throws IOException {
        beforeClick();
        this.currentLink = link;
        this.currentContent = postData(link, params);
        afterClick();
        return this.currentContent;
    }

    public String getCurrentCharset() {
        if (this.currentContent == null) {
            return this.charset;
        }
        String foundCharset = PatternUtils.matchFirst(this.currentContent, "(?i)(<meta\\s*http-equiv\\s*=\\s*(\"|')content-type(\"|')\\s*content\\s*=\\s*(\"|')text/?\\w+;\\s*charset\\s*=\\s*(.*?)(\"|')\\s*/?>)", 5);
        try {
            if (foundCharset != null && foundCharset.length() > 0 && Charset.isSupported(foundCharset)) {
                this.charset = foundCharset;
            }
        } catch (IllegalCharsetNameException illegalCharsetNameException) {
        }

        return this.charset;
    }

    public String postDataWithFile(Link link, Map<String, ? extends Object> params, Map<String, File> files) throws IOException {
        return postDataAndFileWithSelfDefineHeaders(link, params, null, files);
    }

    public String postData(Link link, Map<String, ? extends Object> params) throws IOException {
        return postDataWithSelfDefineHeaders(link, params, null);
    }

    public String postDataWithSelfDefineHeaders(Link link, Map<String, ? extends Object> params, Map<String, String> headers) throws IOException {
        return postDataAndFileWithSelfDefineHeaders(link, params, headers, null);
    }

    public String postDataAndFileWithSelfDefineHeaders(Link link, Map<String, ? extends Object> params, Map<String, String> headers, Map<String, File> files) throws IOException {
        return postDataAndFileWithSelfDefineHeaders(this.httpClient, link, params, headers, files);
    }

    public String getJson(Link link) throws IOException {
        Map<String, String> jsonHeader = new HashMap<>();

        jsonHeader.put("X-Requested-With", "XMLHttpRequest");
        return get(link, jsonHeader);
    }

    public String postJson(Link link, String json) throws IOException {
        return postJson(this.httpClient, link, json, null);
    }

    public String postJson(Link link, String json, Map<String, String> header) throws IOException {
        return postJson(this.httpClient, link, json, header);
    }

    public String postJson(HttpClient httpClient, Link link, String json, Map<String, String> headers) throws IOException {
        String url = link.getUrl();
        if (this.enableLog) {
            log.info("Post json to url {} : {}", url, json);
        } else {
            log.debug("Post json to url " + url + " : " + json);
        }
        HttpResponse response = null;
        HttpPost httpost = new HttpPost(url);
        if (this.proxy != null) {
            httpost.setConfig(RequestConfig.custom()
                    .setProxy(this.proxy)
                    .build());
        }

        try {
            StringEntity s = new StringEntity(json);
            if (headers == null) {
                headers = new HashMap<>(0);
            }
            httpost.addHeader("Accept", "application/json");
            httpost.addHeader("Content-Type", "application/json; charset=UTF-8");
            httpost.addHeader("Pragma", "no-cache");
            if (link.getReferer() != null) {
                httpost.addHeader("Referer", link.getReferer().getUrl());
            }
            httpost.addHeader("User-Agent", this.userAgent);
            httpost.addHeader("X-Requested-With", "XMLHttpRequest");

            Set<String> keys = headers.keySet();
            for (String k : keys) {
                httpost.addHeader(k, headers.get(k));
            }
            httpost.setEntity(s);

            HttpCoreContext context = new HttpCoreContext();
            response = httpClient.execute(httpost, context);
            HttpRequestWrapper wrapper = (HttpRequestWrapper) context.getAttribute("http.request");
            if (wrapper != null) {
                HttpRequestBase original = (HttpRequestBase) wrapper.getOriginal();
                if (original != httpost) {
                    this.currentLink = new Link(original.getURI().toString(), getCurrentLink());
                }
            }
            this.statusLine = response.getStatusLine();
            if (!isRedicrected(response) && !isRefresh(response)) {
                return parseToString(response.getEntity());
            }
        } finally {
            if (response != null && response.getEntity() != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (Exception exception) {
                }
            }
        }

        return checkRedirect(url, response);
    }

    public String postForm(Link link, String params) throws IOException {
        return postForm(this.httpClient, link, params, null);
    }

    public String postString(Link link, String str) throws IOException {
        return postString(this.httpClient, link, str, null);
    }

    public String postString(Link link, String str, Map<String, String> headers) throws IOException {
        return postString(this.httpClient, link, str, headers);
    }

    public String postForm(Link link, String params, Map<String, String> headers) throws IOException {
        return postForm(this.httpClient, link, params, headers);
    }

    public String postForm(HttpClient httpClient, Link link, String params, Map<String, String> headers) throws IOException {
        String url = link.getUrl();
        if (this.enableLog) {
            log.info("Post str to url " + url + " : " + params);
        } else {
            log.debug("Post str to url " + url + " : " + params);
        }
        HttpResponse response = null;
        HttpPost httpost = new HttpPost(url);
        if (this.proxy != null) {
            httpost.setConfig(RequestConfig.custom()
                    .setProxy(this.proxy)
                    .build());
        }


        try {
            if (headers == null) {
                headers = new HashMap<>(0);
            }
            httpost.addHeader("User-Agent", this.userAgent);
            if (headers != null) {
                for (Map.Entry<String, String> en : headers.entrySet()) {
                    httpost.addHeader(en.getKey(), en.getValue());
                }
            }
            if (link.getReferer() != null) {
                httpost.addHeader("Referer", link.getReferer().getUrl());
            }

            String[] ss = StringUtils.delimitedListToStringArray(params, "&");
            List<NameValuePair> nvps = new ArrayList<>();
            for (String s : ss) {
                int index = s.indexOf("=");
                if (index > 0) {
                    nvps.add(new BasicNameValuePair(s.substring(0, index), s.substring(index + 1)));
                }
            }
            httpost.setEntity(new UrlEncodedFormEntity(nvps, getCurrentCharset()));
            HttpCoreContext context = new HttpCoreContext();
            response = httpClient.execute(httpost, context);
            HttpRequestWrapper wrapper = (HttpRequestWrapper) context.getAttribute("http.request");
            if (wrapper != null) {
                HttpRequestBase original = (HttpRequestBase) wrapper.getOriginal();
                if (original != httpost) {
                    this.currentLink = new Link(original.getURI().toString(), getCurrentLink());
                }
            }
            this.statusLine = response.getStatusLine();
            if (!isRedicrected(response) && !isRefresh(response)) {
                return parseToString(response.getEntity());
            }
        } finally {
            if (response != null && response.getEntity() != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (Exception exception) {
                }
            }
        }

        return checkRedirect(url, response);
    }

    public String postString(HttpClient httpClient, Link link, String str, Map<String, String> headers) throws IOException {
        String url = link.getUrl();
        if (this.enableLog) {
            log.info("Post str to url " + url + " : " + str);
        } else {
            log.debug("Post str to url " + url + " : " + str);
        }
        HttpResponse response = null;
        HttpPost httpost = new HttpPost(url);
        if (this.proxy != null) {
            httpost.setConfig(RequestConfig.custom()
                    .setProxy(this.proxy)
                    .build());
        }

        try {
            StringEntity s = new StringEntity(str);
            httpost.addHeader("User-Agent", this.userAgent);
            if (headers != null) {
                for (Map.Entry<String, String> en : headers.entrySet()) {
                    httpost.addHeader(en.getKey(), en.getValue());
                }
            }
            if (link.getReferer() != null) {
                httpost.addHeader("Referer", link.getReferer().getUrl());
            }
            httpost.removeHeaders("Cookie2");
            httpost.setEntity(s);

            HttpCoreContext context = new HttpCoreContext();
            response = httpClient.execute(httpost, context);
            HttpRequestWrapper wrapper = (HttpRequestWrapper) context.getAttribute("http.request");
            if (wrapper != null) {
                HttpRequestBase original = (HttpRequestBase) wrapper.getOriginal();
                if (original != httpost) {
                    this.currentLink = new Link(original.getURI().toString(), getCurrentLink());
                }
            }
            this.statusLine = response.getStatusLine();
            if (!isRedicrected(response) && !isRefresh(response)) {
                return parseToString(response.getEntity());
            }
        } finally {
            if (response != null && response.getEntity() != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (Exception exception) {
                }
            }
        }

        return checkRedirect(url, response);
    }

    public String postDataAndFileWithSelfDefineHeaders(HttpClient httpClient, Link link, Map<String, ? extends Object> params, Map<String, String> headers, Map<String, File> files) throws IOException {
        if (params == null) {
            params = new HashMap<>(0);
        }
        String url = link.getUrl();

        StringBuffer qs = new StringBuffer();
        if (params != null) {
            Set<String> names = params.keySet();
            Iterator<String> iter = names.iterator();
            while (iter.hasNext()) {
                String name = iter.next();
                qs.append("&").append(name).append("=").append(params.get(name));
            }
        }
        if (this.enableLog) {
            log.info("Post to url : " + url + " || parame : " + ((qs.length() > 0) ? qs.substring(1) : ""));
        } else {
            log.debug("Post to url : " + url + " || parame : " + ((qs.length() > 0) ? qs.substring(1) : ""));
        }
        HttpResponse response = null;
        HttpPost httpost = new HttpPost(url);
        if (this.proxy != null) {
            httpost.setConfig(RequestConfig.custom()
                    .setProxy(this.proxy)
                    .build());
        }

        try {
            httpost.addHeader("User-Agent", this.userAgent);
            if (headers != null) {
                for (Map.Entry<String, String> en : headers.entrySet()) {
                    httpost.addHeader(en.getKey(), en.getValue());
                }
                httpost.addHeader("Accept-Encoding", "gzip, deflate");
                httpost.addHeader("Content-Type", "application/x-www-form-urlencoded");
            }
            if (link.getReferer() != null) {
                httpost.addHeader("Referer", link.getReferer().getUrl());
            }
            httpost.removeHeaders("Cookie2");
            if (files == null) {
                List<NameValuePair> nvps = new ArrayList<>();
                for (Map.Entry<String, ? extends Object> en : params.entrySet()) {
                    if (en.getValue() instanceof String) {
                        nvps.add(new BasicNameValuePair(en.getKey(), (String) en.getValue()));
                        continue;
                    }
                    if (en.getValue() instanceof Collection) {
                        Collection con = (Collection) en.getValue();
                        for (Object obj : con) {
                            nvps.add(new BasicNameValuePair(en.getKey(), obj.toString()));
                        }
                    }
                }
                httpost.setEntity(new UrlEncodedFormEntity(nvps, getCurrentCharset()));
            } else {
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                if (files != null && !files.isEmpty())
                    for (Map.Entry<String, File> en : files.entrySet()) {
                        if (en.getValue().exists()) {
                            builder.addPart(en.getKey(), new FileBody(en.getValue()));
                        }
                    }
                for (Map.Entry<String, ? extends Object> en : params.entrySet()) {
                    if (en.getValue() instanceof File) {
                        builder.addPart(en.getKey(), new FileBody((File) en.getValue()));
                        continue;
                    }
                    if (en.getValue() instanceof byte[]) {
                        builder.addPart(en.getKey(), new ByteArrayBody((byte[]) en.getValue(), en.getKey()));
                        continue;
                    }
                    if (en.getValue() instanceof String) {
                        builder.addPart(en.getKey(), new StringBody(en.getValue().toString()));
                        continue;
                    }
                    if (en.getValue() instanceof Collection) {
                        Collection con = (Collection) en.getValue();
                        for (Object obj : con) {
                            if (obj instanceof File) {
                                builder.addPart(en.getKey(), new FileBody((File) obj));
                                continue;
                            }
                            if (obj instanceof byte[]) {
                                builder.addPart(en.getKey(), new ByteArrayBody((byte[]) obj, en.getKey()));
                                continue;
                            }
                            if (obj instanceof String) {
                                builder.addTextBody(en.getKey(), obj.toString());
                            }
                        }
                    }
                }
                httpost.setEntity(builder.build());
            }

            RequestConfig requestConfig = newRequestConfig();
            httpost.setConfig(requestConfig);
            HttpCoreContext context = new HttpCoreContext();
            response = httpClient.execute(httpost, context);
            HttpRequestWrapper wrapper = (HttpRequestWrapper) context.getAttribute("http.request");
            if (wrapper != null) {
                HttpRequestBase original = (HttpRequestBase) wrapper.getOriginal();
                if (original != httpost) {
                    this.currentLink = new Link(original.getURI().toString(), getCurrentLink());
                }
            }

            this.statusLine = response.getStatusLine();
            if (!isRedicrected(response) && !isRefresh(response)) {
                return parseToString(response.getEntity());
            }
        } finally {
            if (response != null && response.getEntity() != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (Exception exception) {
                }
            }
        }

        return checkRedirect(url, response);
    }

    private RequestConfig newRequestConfig() {
        return RequestConfig.custom()
                .setConnectTimeout(getConnectionTimeout())
                .setConnectionRequestTimeout(2000)
                .setSocketTimeout(getReadTimeout())
                .setCookieSpec("compatibility").build();
    }

    private boolean isRefresh(HttpResponse response) {
        Header[] headers = response.getHeaders("Refresh");
        return (headers != null && headers.length > 0);
    }


    public void setAccount(Account account) {
        this.account = account;
        parseCookie();
    }

    private void parseCookie() {
        if (this.account != null &&
                this.account.getMemberString() != null) {
            String[] cookies = this.account.getMemberString().split(";");
            for (String str : cookies) {
                String name = PatternUtils.matchFirst(str, "\\[name:\\s*(.*?)\\]", 1);
                String value = PatternUtils.matchFirst(str, "\\[value:\\s*(.*?)\\]", 1);
                String domain = PatternUtils.matchFirst(str, "\\[domain:\\s*(.*?)\\]", 1);
                String path = PatternUtils.matchFirst(str, "\\[path:\\s*(.*?)\\]", 1);
                Date expiry = null;
                try {
                    String _expiry = PatternUtils.matchFirst(str, "\\[expiry:\\s*(.*?)\\]", 1);
                    if (!"null".equals(_expiry)) {
                        DateFormat df = DateFormat.getDateInstance();
                        expiry = df.parse(_expiry);
                    }
                } catch (Exception exception) {
                }

                if (name.length() > 0 && value.length() > 0 && domain.length() > 0 && path.length() > 0) {
                    setCookie(name, value, domain, path, expiry);
                }
            }
        }
    }


    public void setProxy(String proxyHost, int proxyPort) {
        this.proxy = new HttpHost(proxyHost, proxyPort);
    }

    public Browser clone() {
        Browser browser = new Browser();
        browser.currentContent = this.currentContent;
        browser.currentLink = this.currentLink;
        browser.account = this.account;
        List<Cookie> cookies = this.cookieStore.getCookies();
        for (Cookie cookie : cookies) {
            browser.cookieStore.addCookie(cookie);
        }
        browser.proxy = this.proxy;
        return browser;
    }

    public void setCurrentContent(String currentContent) {
        this.currentContent = currentContent;
    }

    public void setCurrentLink(Link currentLink) {
        this.currentLink = currentLink;
    }

    public int getStatusCode() {
        if (this.statusLine == null) return 200;
        return this.statusLine.getStatusCode();
    }

    public String getReasonPhrase() {
        if (this.statusLine == null) return "Ok";
        return this.statusLine.getReasonPhrase();
    }

    public String parseUri(Link link) {
        String url = link.getUrl();


        url = url.trim().replaceAll("&lt;", "<").replaceAll("&quot;", "\"").replaceAll("&amp;", "&").replaceAll("\\|", "%7C");
        try {
            URI uri = new URI(url);
            if (uri.isAbsolute()) {
                if ("".equals(uri.getPath())) {
                    url = url + "/";
                }
                return url;
            }
        } catch (URISyntaxException uRISyntaxException) {
        }

        Link ref = link.getReferer();
        if (ref == null) {
            return url;
        }
        String refUrl = ref.getUrl();
        try {
            URI uri = new URI(refUrl);
            if (uri.isAbsolute()) {
                String path = uri.getPath();
                if (url.startsWith("/")) {
                    String str = refUrl.substring(0, refUrl.indexOf(path)) + url;
                    link.setUrl(str);
                    return str;
                }
                String result = refUrl.substring(0, refUrl.indexOf(path)) + path.substring(0, path.lastIndexOf("/") + 1) + url;
                link.setUrl(result);
                return result;
            }

        } catch (URISyntaxException uRISyntaxException) {
        }

        return url;
    }


    public void enableLog(boolean enableLog) {
        this.enableLog = enableLog;
    }

    public void shutDown() {
        this.isShutDown = true;
        this.httpClient.getConnectionManager().shutdown();
    }

    public boolean isShutDown() {
        return this.isShutDown;
    }

    public void saveCookieToAccount() {
        String cookieStr = getAllCookieStr();
        String[] cc = cookieStr.split(";");
        Map<String, String> cookieMap = new HashMap<>();
        for (String c : cc) {
            int idx = c.indexOf("=");
            if (idx > 0) {
                String key = c.substring(0, idx).trim();
                String value = c.substring(idx + 1).trim();
                cookieMap.put(key, value);
            }
        }
        getAccount().setCookie(cookieMap);
    }

    public void initCookieFromAccount(String domain) {
        String memberString = getAccount().getMemberString();
        if (memberString != null && memberString.length() > 0) {
            String[] ss = memberString.split("\t");
            String[] cookies = ss[0].split(";");
            for (String cookie : cookies) {
                int idx = cookie.indexOf("=");
                if (idx > 0) {
                    String key = cookie.substring(0, idx).trim();
                    String value = cookie.substring(idx + 1).trim();
                    setCookie(key, value, domain, "/");
                }
            }
        }
    }


    public int getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getReadTimeout() {
        return this.readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }
}


/* Location:              C:\Users\fred\Downloads\bet-server-1.0-SNAPSHOT\BOOT-INF\lib\bet-common-1.0.0-SNAPSHOT.jar!\com\chief\ww\\util\Browser.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */