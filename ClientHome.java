//package file.share.main;

import javax.swing.*;
import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientHome {
    public static void main(String args[]) throws IOException {
        new ClientHome().init();
    }
    private void init(){
        JFrame frame = new JFrame("MyFile Sharing Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(300,100,400,400);
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(null);
        JLabel labelOne = new JLabel("Server Ip:");
        JTextField serverIpTxt = new JTextField();
        labelOne.setBounds(50,50,100,20);
        serverIpTxt.setBounds(160,50,200,20);
        centerPanel.add(labelOne);
        centerPanel.add(serverIpTxt);

        JLabel labelTwo = new JLabel("Port:");
        JTextField portTxt = new JTextField();
        labelTwo.setBounds(50,100,100,20);
        portTxt.setBounds(160,100,200,20);
        centerPanel.add(labelTwo);
        centerPanel.add(portTxt);

        JLabel labelThree = new JLabel("Password:");
        JPasswordField passwordTxt = new JPasswordField();
        labelThree.setBounds(50,150,100,20);
        passwordTxt.setBounds(160,150,200,20);
        centerPanel.add(labelThree);
        centerPanel.add(passwordTxt);

        JButton confirmBt = new JButton("Confirm");
        JButton cancelBt = new JButton("Cancel");
        confirmBt.setBounds(50,200,100,30);
        cancelBt.setBounds(200,200,100,30);
        centerPanel.add(confirmBt);
        centerPanel.add(cancelBt);

        cancelBt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                serverIpTxt.setText("");
                portTxt.setText("");
                passwordTxt.setText("");
            }
        });

        confirmBt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FileViewer(frame,serverIpTxt.getText(),portTxt.getText(),passwordTxt.getText());
            }
        });

        frame.add(centerPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}
