package com.chieffu.pocker.util;

import java.text.SimpleDateFormat;
import java.util.Date;


public class NetWorkTimeUtil {
    public static void main(String[] args) {
        System.out.println("open.baidu.com:\t" + new Date(getBaiduTime()));
        System.out.println("timedate.cn:\t" + new Date(getTimedateTime()));
        System.out.println("bjtime.cn:\t" + new Date(getBeijinTime1()));
        System.out.println("www.beijing-time.org:\t" + new Date(getBeijinTime2()));
    }

    public static long getSysTime() {
        return System.currentTimeMillis();
    }

    public static long getBaiduTime() {
        Browser browser = new Browser();

        try {
            browser.enableLog(false);
            browser.click(new Link("http://open.baidu.com/special/time/"));
            String time = PatternUtils.matchFirst(browser.getCurrentContent(), "window\\.baidu_time\\((\\d+)\\);", 1);
            if (time.length() > 0) return Long.parseLong(time);
        } catch (Exception exception) {
        } finally {
            browser.shutDown();
        }

        return 0L;
    }

    public static long getBeijinTime1() {
        Browser browser = new Browser();

        try {
            browser.enableLog(false);
            browser.click(new Link("http://bjtime.cn/"));
            browser.click(new Link("http://bjtime.cn/header6.asp", browser.getCurrentLink()));
            String year = PatternUtils.matchFirst(browser.getCurrentContent(), "nyear\\s*=\\s*(\\d+)\\s*;", 1);
            String mon = PatternUtils.matchFirst(browser.getCurrentContent(), "nmonth\\s*=\\s*(\\d+)\\s*;", 1);
            String day = PatternUtils.matchFirst(browser.getCurrentContent(), "nday\\s*=\\s*(\\d+)\\s*;", 1);
            String hour = PatternUtils.matchFirst(browser.getCurrentContent(), "nhrs\\s*=\\s*(\\d+)\\s*;", 1);
            String minute = PatternUtils.matchFirst(browser.getCurrentContent(), "nmin\\s*=\\s*(\\d+)\\s*;", 1);
            String second = PatternUtils.matchFirst(browser.getCurrentContent(), "nsec\\s*=\\s*(\\d+)\\s*;", 1);
            if (year.length() > 0 && mon.length() > 0 && day.length() > 0 && hour.length() > 0 && minute.length() > 0 && second.length() > 0) {
                StringBuffer s = new StringBuffer(year);
                s.append("/").append(mon)
                        .append("/").append(day)
                        .append("/").append(hour)
                        .append("/").append(minute)
                        .append("/").append(second);

                SimpleDateFormat sdf = new SimpleDateFormat("yy/M/d/H/m/s");
                Date d = sdf.parse(s.toString());
                return d.getTime();
            }
        } catch (Exception exception) {
        } finally {
            browser.shutDown();
        }

        return 0L;
    }

    public static long getBeijinTime2() {
        Browser browser = new Browser();

        try {
            browser.enableLog(false);
            browser.click(new Link("http://www.beijing-time.org/time.asp"));
            String year = PatternUtils.matchFirst(browser.getCurrentContent(), "nyear\\s*=\\s*(\\d+)\\s*;", 1);
            String mon = PatternUtils.matchFirst(browser.getCurrentContent(), "nmonth\\s*=\\s*(\\d+)\\s*;", 1);
            String day = PatternUtils.matchFirst(browser.getCurrentContent(), "nday\\s*=\\s*(\\d+)\\s*;", 1);
            String hour = PatternUtils.matchFirst(browser.getCurrentContent(), "nhrs\\s*=\\s*(\\d+)\\s*;", 1);
            String minute = PatternUtils.matchFirst(browser.getCurrentContent(), "nmin\\s*=\\s*(\\d+)\\s*;", 1);
            String second = PatternUtils.matchFirst(browser.getCurrentContent(), "nsec\\s*=\\s*(\\d+)\\s*;", 1);
            if (year.length() > 0 && mon.length() > 0 && day.length() > 0 && hour.length() > 0 && minute.length() > 0 && second.length() > 0) {
                StringBuffer s = new StringBuffer(year);
                s.append("/").append(mon)
                        .append("/").append(day)
                        .append("/").append(hour)
                        .append("/").append(minute)
                        .append("/").append(second);

                SimpleDateFormat sdf = new SimpleDateFormat("yy/M/d/H/m/s");
                Date d = sdf.parse(s.toString());
                return d.getTime();
            }
        } catch (Exception exception) {
        } finally {
            browser.shutDown();
        }

        return 0L;
    }

    public static long getTimedateTime() {
        Browser browser = new Browser();

        try {
            browser.enableLog(false);
            browser.click(new Link("http://www.timedate.cn/worldclock/ti.asp"));
            String year = PatternUtils.matchFirst(browser.getCurrentContent(), "nyear\\s*=\\s*(\\d+)\\s*;", 1);
            String mon = PatternUtils.matchFirst(browser.getCurrentContent(), "nmonth\\s*=\\s*(\\d+)\\s*;", 1);
            String day = PatternUtils.matchFirst(browser.getCurrentContent(), "nday\\s*=\\s*(\\d+)\\s*;", 1);
            String hour = PatternUtils.matchFirst(browser.getCurrentContent(), "nhrs\\s*=\\s*(\\d+)\\s*;", 1);
            String minute = PatternUtils.matchFirst(browser.getCurrentContent(), "nmin\\s*=\\s*(\\d+)\\s*;", 1);
            String second = PatternUtils.matchFirst(browser.getCurrentContent(), "nsec\\s*=\\s*(\\d+)\\s*;", 1);
            if (year.length() > 0 && mon.length() > 0 && day.length() > 0 && hour.length() > 0 && minute.length() > 0 && second.length() > 0) {
                StringBuffer s = new StringBuffer(year);
                s.append("/").append(mon)
                        .append("/").append(day)
                        .append("/").append(hour)
                        .append("/").append(minute)
                        .append("/").append(second);

                SimpleDateFormat sdf = new SimpleDateFormat("yy/M/d/H/m/s");
                Date d = sdf.parse(s.toString());
                return d.getTime();
            }
        } catch (Exception exception) {
        } finally {
            browser.shutDown();
        }

        return 0L;
    }

    public static long getServerTime(String host) {
        Browser browser = new Browser();

        try {
            browser.enableLog(false);
            browser.click(new Link("http://" + host + ":13/"));
            String time = PatternUtils.matchFirst(browser.getCurrentContent(), "window\\.baidu_time\\((\\d+)\\);", 1);
            System.out.println(time);
            if (time.length() > 0) return Long.parseLong(time);
        } catch (Exception exception) {
        } finally {
            browser.shutDown();
        }

        return 0L;
    }
}


/* Location:              C:\Users\fred\Downloads\bet-server-1.0-SNAPSHOT\BOOT-INF\lib\bet-common-1.0.0-SNAPSHOT.jar!\com\chief\ww\\util\NetWorkTimeUtil.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */