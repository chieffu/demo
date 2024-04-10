package com.chieffu.pocker.util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.LockSupport;

public class CaptchaPanel extends JFrame {
    private static final CaptchaPanel instance = new CaptchaPanel(6);
    Map<String, String> map = new HashMap<>();
    private final BlockingQueue<InputPanel> queue = new LinkedBlockingQueue<>();
    private JPanel content;

    public CaptchaPanel() {
        this(6);
    }

    public CaptchaPanel(int size) {
        initGUI();
        for (int i = 0; i < size; i++) {
            InputPanel input = new InputPanel(null);
            this.queue.add(input);
            this.content.add(input);
        }
        setTitle("输入验证码");
        setDefaultCloseOperation(1);
        pack();
    }

    public static CaptchaPanel newInstance() {
        return instance;
    }

    public static void main(String[] args) {
        instance.setVisible(true);
    }

    public String inputCaptcha(BufferedImage image) {
        return inputCaptcha(image, null);
    }

    public String inputCaptcha(BufferedImage image, String msg) {
        if (image == null) return null;
        setPreferredSize(getSize());
        setVisible(true);
        InputPanel p = nextInputPanel();
        if (p == null) return StringUtils.newRandomWord(6);
        p.setImage(image);
        LockSupport.park(new Object());
        String result = p.getValue();
        putBack(p);
        return result;
    }

    private InputPanel nextInputPanel() {
        try {
            return this.queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();

            return null;
        }
    }

    private void putBack(InputPanel p) {
        this.queue.add(p);
    }

    private void initGUI() {
        try {
            setPreferredSize(new Dimension(537, 229));

            this.content = new JPanel();
            getContentPane().add(this.content, "Center");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


/* Location:              C:\Users\fred\Downloads\bet-server-1.0-SNAPSHOT\BOOT-INF\lib\bet-common-1.0.0-SNAPSHOT.jar!\com\chief\ww\\util\CaptchaPanel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */