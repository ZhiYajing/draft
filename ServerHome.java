/*
 * GUI interface for server
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ServerHome {
    public static void main(String args[]) throws IOException {
        new ServerHome().init();
        new Server().serve();
    }
    //Set share directory and password
    public void init(){
        JFrame frame = new JFrame("File Sharing Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(300,100,400,400);
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(null);
        JButton setFolderBt = new JButton("Set Sharing Folder");
        setFolderBt.setBounds(120,50,150,40);
        setFolderBt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int flag = fc.showOpenDialog(null);
                if(flag == JFileChooser.APPROVE_OPTION){
                    File file=fc.getSelectedFile();
                    String path=file.getPath();
                    File pathFile = new File("path.txt");
                    try {
                        BufferedWriter writer = new BufferedWriter(new FileWriter(pathFile));
                        writer.append(path);
                        writer.flush();
                        writer.close();
                        JOptionPane.showMessageDialog(null,"Select File Sharing Successfully!");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        JButton setPasswordBt = new JButton("Set Password");
        setPasswordBt.setBounds(120,120,150,40);
        setPasswordBt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog dialog = new JDialog(frame,"Set Password Dialog");
                dialog.setBounds(50,50,400,300);
                dialog.setLayout(null);
                JLabel labelOne = new JLabel("Password:");
                labelOne.setBounds(50,50,120,20);
                JPasswordField passwordFieldOne = new JPasswordField();
                passwordFieldOne.setBounds(180,50,100,20);
                dialog.add(labelOne);
                dialog.add(passwordFieldOne);

                JLabel labelTwo = new JLabel("Password Again:");
                labelTwo.setBounds(50,100,120,20);
                JPasswordField passwordFieldTwo = new JPasswordField();
                passwordFieldTwo.setBounds(180,100,100,20);
                dialog.add(labelTwo);
                dialog.add(passwordFieldTwo);

                JButton confirmBt = new JButton("Confirm");
                JButton cancelBt = new JButton("Cancel");
                confirmBt.setBounds(50,140,100,30);
                cancelBt.setBounds(180,140,100,30);
                dialog.add(confirmBt);
                dialog.add(cancelBt);

                cancelBt.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        dialog.dispose();
                    }
                });

                confirmBt.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String passwordOne =  passwordFieldOne.getText();
                        String passwordTwo = passwordFieldTwo.getText();
                        if(passwordOne.equals(passwordTwo)){
                            File pathFile = new File("password.txt");
                            try {
                                BufferedWriter writer = new BufferedWriter(new FileWriter(pathFile));
                                writer.append(passwordOne);
                                writer.flush();
                                writer.close();
                                JOptionPane.showMessageDialog(null,"Set Password Successfully!");
                                dialog.dispose();
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }else{
                            JOptionPane.showMessageDialog(null,"Inconsistent Passwords!");
                        }
                    }
                });

                dialog.setVisible(true);
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            }
        });
        centerPanel.add(setFolderBt);
        centerPanel.add(setPasswordBt);
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}
