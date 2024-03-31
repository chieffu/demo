package com.chieffu.pocker.util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.LockSupport;


public class InputPanel
        extends JPanel
        implements ActionListener {
    private JTextField input;
    private JLabel imgLabel;
    private Thread thread;
    private String value = "";


    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame();
        BufferedImage image = ImageIO.read(new File("vc.png"));
        InputPanel p = new InputPanel(image);
        frame.getContentPane().add(p);
        frame.setDefaultCloseOperation(2);
        frame.pack();
        frame.setVisible(true);
    }


    public InputPanel(BufferedImage image) {
        initGUI();
        setImage(image);
    }

    private void initGUI() {
        try {
            GridBagLayout thisLayout = new GridBagLayout();
            setPreferredSize(new Dimension(301, 62));
            thisLayout.rowWeights = new double[]{0.1D};
            thisLayout.rowHeights = new int[]{7};
            thisLayout.columnWeights = new double[]{0.0D, 0.1D, 0.0D, 0.0D};
            thisLayout.columnWidths = new int[]{3, 200, 82, 3};
            setLayout(thisLayout);

            this.imgLabel = new JLabel();
            add(this.imgLabel, new GridBagConstraints(1, 0, 1, 1, 0.0D, 0.0D, 13, 1, new Insets(0, 0, 0, 0), 0, 0));
            this.imgLabel.setHorizontalAlignment(4);


            this.input = new JTextField();
            add(this.input, new GridBagConstraints(2, 0, 1, 1, 0.0D, 0.0D, 10, 2, new Insets(0, 0, 0, 0), 0, 0));
            this.input.setPreferredSize(new Dimension(100, 38));
            this.input.addActionListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setImage(BufferedImage image) {
        setImage(image, "");
    }

    public void setImage(BufferedImage image, String label) {
        this.input.setText("");
        if (image == null) {
            this.imgLabel.setIcon(null);
            this.imgLabel.setText("");
        } else {
            this.imgLabel.setIcon(new ImageIcon(image));
            this.imgLabel.setText((label == null) ? "" : label);
            this.thread = Thread.currentThread();
        }
    }


    public void actionPerformed(ActionEvent e) {
        this.value = this.input.getText().trim();
        setImage((BufferedImage) null);
        if (this.thread != null) {
            LockSupport.unpark(this.thread);
            this.thread = null;
        }
    }

    public String getValue() {
        return this.value;
    }
}


/* Location:              C:\Users\fred\Downloads\bet-server-1.0-SNAPSHOT\BOOT-INF\lib\bet-common-1.0.0-SNAPSHOT.jar!\com\chief\ww\\util\InputPanel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */