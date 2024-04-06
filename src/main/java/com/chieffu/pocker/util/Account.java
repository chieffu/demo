package com.chieffu.pocker.util;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class Account implements Serializable, Cloneable {

    private static String DELIMITER = "\t";
    private String username = "";

    public String getUsername() {
        return this.username;
    }

    private double ballance;
    private double money;
    private double maxWin;
    private double maxLost;
    private String password = "";
    private double totalBet;
    private Map<String, String> cookie;
    private String memberString;


    public Account(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static Account newAccount(String a) {
        if (a.startsWith("{") && a.endsWith("}")) {
            return JSONObject.parseObject(a, Account.class);
        }
        if (a == null) {
            return null;
        }
        try {
            String[] aa = StringUtils.delimitedListToStringArray(a, DELIMITER);
            if (aa.length == 1) {
                aa = a.split(",|\t");
            }
            if (aa.length < 2) {
                if (a.contains("@")) {
                    Account account = new Account();
                    account.setUsername(a.trim());
                    account.setPassword("");
                    return account;
                }
                return null;
            }
            Account acc = new Account();
            int i = 0;
            acc.setUsername(aa[i++].trim());
            acc.setPassword(aa[i++].replaceAll("[\r\n]+", ""));
            if (aa.length > 2) {
                acc.setMemberString(a.substring(a.indexOf(acc.getPassword()) + acc.getPassword().length()).trim());
            }
            return acc;
        } catch (Exception e) {
            System.err.println("Unkown Account :" + a);

            return null;
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(this.username);
        sb.append(DELIMITER).append(this.password);
        if (this.memberString != null && this.memberString.length() > 0) {
            sb.append(DELIMITER).append(this.memberString);
        }
        return sb.toString();
    }

    public Account() {
    }
}


/* Location:              C:\Users\fred\Downloads\bet-server-1.0-SNAPSHOT\BOOT-INF\lib\bet-common-1.0.0-SNAPSHOT.jar!\com\chief\ww\\util\Account.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */