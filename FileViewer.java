

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.net.Socket;

public class FileViewer extends JDialog{
    private String ip;
    private String port;
    private String password;

    public FileViewer(JFrame frame,String ip,String port,String password){
        super(frame,"MyFile Viewer");
        this.ip = ip;
        this.password = password;
        this.port = port;
        try {
            Socket client = new Socket(ip,Integer.parseInt(port));
            client.setSoTimeout(10000);
            PrintStream out = new PrintStream(client.getOutputStream());
            out.println("password:"+password);
            out.println("list:/");
            InputStream inputStream = client.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String listResult = reader.readLine();

            client.close();
            //System.out.println(listResult);
            DefaultMutableTreeNode root = new DefaultMutableTreeNode();
            String arr[] = listResult.split("\\|");
            long size = 0;
            for(String s : arr){
                String fileArr[] = s.split("\\?");
                root.add(new DefaultMutableTreeNode(new MyFile(fileArr[0],fileArr[1],fileArr[2])));
                size += Long.parseLong(fileArr[2]);
            }
            root.setUserObject(new MyFile("/","dir",size+""));
            final JTree tree = new JTree(root);
            tree.setCellRenderer(new MyTreeCellRenderer());

            JScrollPane scrollTree = new JScrollPane(tree);
            scrollTree.setViewportView(tree);
            this.add(scrollTree);
            JPanel panel = new JPanel();
            JLabel labelOne = new JLabel("FileName:");
            panel.add(labelOne);
            JTextField nameTxt = new JTextField(10);
            nameTxt.setEditable(false);
            panel.add(nameTxt);

            JLabel labelTwo = new JLabel("Size:");
            panel.add(labelTwo);
            JTextField sizeTxt = new JTextField(10);
            sizeTxt.setEditable(false);
            panel.add(sizeTxt);


            this.add(panel, BorderLayout.NORTH);
            this.setBounds(300,200,400,400);
            this.setVisible(true);
            this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

            tree.addTreeSelectionListener(new TreeSelectionListener() {

                @Override
                public void valueChanged(TreeSelectionEvent e) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
                            .getLastSelectedPathComponent();

                    if (node == null)
                        return;

                    Object object = node.getUserObject();
                    MyFile myFile = (MyFile) object;
                    nameTxt.setText(myFile.getName());
                    sizeTxt.setText(Integer.parseInt(myFile.getSize())/1024+"KB");
                }
            });
            JPopupMenu pop = new JPopupMenu();
            pop.add(new AbstractAction("Download") {
                public void actionPerformed(ActionEvent e) {
                    TreePath treePathList[] = tree.getSelectionPaths();
                    if(treePathList!=null && treePathList.length>0){
                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        int flag = fileChooser.showSaveDialog(null);
                        if (flag == JFileChooser.APPROVE_OPTION) {
                            File dir = fileChooser.getSelectedFile();
                            if (dir == null) {
                                return;
                            }

                            for(TreePath treePath : treePathList){
                                DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath.getLastPathComponent();
                                MyFile myFile = (MyFile) node.getUserObject();
                                StringBuffer stringBuffer = new StringBuffer(myFile.getName());
                                DefaultMutableTreeNode temp = (DefaultMutableTreeNode) node.getParent();
                                while (temp != null ) {
                                    stringBuffer.insert(0, ((MyFile) temp.getUserObject()).getName() + "/");
                                    temp = (DefaultMutableTreeNode) temp.getParent();
                                }

                                String filePath = stringBuffer.toString();
                                if(myFile.getType().equals("file")){
                                    new Thread(new DownloadThread(ip,port,password,filePath,dir.getAbsolutePath(),Integer.parseInt(myFile.getSize()))).start();
                                }else{
                                    try {
                                        downloadDir(filePath,dir.getAbsolutePath()+"/"+myFile.getName());
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }

                                }
                            }
                        }
                    }

                }
            });
            tree.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    TreePath path = tree.getPathForLocation(e.getX(), e.getY());
                    if(path!=null) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                        if(e.isMetaDown()){
                            pop.show(tree, e.getX(), e.getY());
                        }else {
                            if (e.getClickCount() == 2) {
                                MyFile myFile = (MyFile) node.getUserObject();
                                if (node.isLeaf() && (myFile.getType().equals("dir"))) {
                                    DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                                    try {
                                        Socket client = new Socket(ip, Integer.parseInt(port));
                                        PrintStream out = new PrintStream(client.getOutputStream());
                                        out.println("password:" + password);
                                        StringBuffer stringBuffer = new StringBuffer(myFile.getName());
                                        DefaultMutableTreeNode temp = (DefaultMutableTreeNode) node.getParent();
                                        while (temp != null) {
                                            stringBuffer.insert(0, ((MyFile) temp.getUserObject()).getName() + "/");
                                            temp = (DefaultMutableTreeNode) temp.getParent();
                                        }
                                        out.println("list:/" + stringBuffer.toString());
                                        InputStream inputStream = client.getInputStream();
                                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                                        String listResult = reader.readLine();
                                        System.out.println(listResult);
                                        client.close();
                                        if (listResult != null) {
                                            String arr[] = listResult.split("\\|");
                                            for (String s : arr) {
                                                String fileArr[] = s.split("\\?");
                                                model.insertNodeInto(new DefaultMutableTreeNode(new MyFile(fileArr[0], fileArr[1], fileArr[2])), node, 0);
                                                tree.expandPath(path);
                                            }
                                        }
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    } finally {
                                        try {
                                            client.close();
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }
                                    }

                                }

                            }
                        }
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void downloadDir(String dir,String filePath) throws IOException {
        if(dir.equals("/")){
            filePath = filePath + "/shareFolder";
        }
        Socket client = new Socket(ip,Integer.parseInt(port));
        client.setSoTimeout(100000);
        PrintStream out = new PrintStream(client.getOutputStream());
        out.println("password:"+password);
        out.println("list:"+dir);
        InputStream inputStream = client.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String listResult = reader.readLine();
        client.close();
        if(listResult != null && !listResult.equals("")) {
            String arr[] = listResult.split("\\|");
            for (String s : arr) {
                String fileArr[] = s.split("\\?");
                if (fileArr[1].equals("dir")) {
                    downloadDir(dir + "/" + fileArr[0], filePath +"/"+ fileArr[0]);
                } else {
                    new Thread(new DownloadThread(ip, port, password, dir + "/" + fileArr[0], filePath, Integer.parseInt(fileArr[2]))).start();
                }
            }
        }
    }

}
