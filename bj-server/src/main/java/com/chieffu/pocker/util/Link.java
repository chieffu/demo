package com.chieffu.pocker.util;

import java.net.URI;
import java.net.URISyntaxException;

public class Link {
    private String url;

    public Link(String url) {
        this.url = url;
        if (this.url.indexOf("?") == -1 && this.url.indexOf("&") != -1)
            this.url = this.url.replaceFirst("&", "?");
    }

    private Link referer;

    public Link(String url, Link referer) {
        this.referer = referer;
        if (url.toLowerCase().startsWith("http")) {
            this.url = url.replaceAll(" ", "+");
        } else {
            String refUrl = referer.getUrl();
            try {
                URI uri = new URI(refUrl);
                String path = uri.getPath();
                if (!url.startsWith("/")) {
                    int index = path.lastIndexOf("/");
                    this.url = uri.getScheme() + "://" + uri.getHost() + ((uri.getPort() > 0) ? (":" + uri.getPort()) : "") + path.substring(0, index + 1) + url;
                    this.url = this.url.replace("/./", "/");
                } else {
                    this.url = uri.getScheme() + "://" + uri.getHost() + ((uri.getPort() > 0) ? (":" + uri.getPort()) : "") + url;
                }
            } catch (URISyntaxException e) {
                this.url = url;
            }
        }
        if (this.url.indexOf("?") == -1 && this.url.indexOf("&") != -1)
            this.url = this.url.replaceFirst("&", "?");
    }

    public Link(String url, String refererPath) {
        this(url, new Link(refererPath));
    }

    public String getUrl() {
        return this.url;
    }

    public Link getReferer() {
        return this.referer;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String toString() {
        return this.url;
    }

    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof Link) {
            Link link = (Link) obj;
            if (link.getUrl() == getUrl()) return true;

            if (getUrl() == null) return false;
            return getUrl().equals(link.getUrl());
        }
        return false;
    }
}


/* Location:              C:\Users\fred\Downloads\bet-server-1.0-SNAPSHOT\BOOT-INF\lib\bet-common-1.0.0-SNAPSHOT.jar!\com\chief\ww\\util\Link.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */