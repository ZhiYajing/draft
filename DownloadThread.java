/*
 * File download function
 */

import java.io.*;
import java.net.Socket;

public class DownloadThread implements Runnable{
    private String ip;
    private String port;
    private String password;
    private String filePath;
    private String dir;
    private int size;

    public DownloadThread(String ip, String port, String password, String filePath,String dir,int size) {
        this.ip = ip;
        this.port = port;
        this.password = password;
        this.filePath = filePath;
        this.dir = dir;
        this.size = size;

    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(ip,Integer.parseInt(port));
            DataInputStream in = new DataInputStream(socket.getInputStream());
            
            socket.shutdownOutput();
            int begin = filePath.lastIndexOf("/")+1;
            String fileName = filePath.substring(begin);
            File temp = new File(dir);
            if(!temp.exists()){
                temp.mkdirs();
            }

            FileOutputStream fileOut = new FileOutputStream(dir+"/"+fileName);    
           
            byte[] buf = new byte[4096];
            long total = 0;
            int length = in.read(buf);

            while(total<size)
            {
            	length=in.read(buf,0,(int)Math.min(buf.length,size-total));
                total+=length;
                fileOut.write(buf, 0, length);
            }
            System.out.println("Done");
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
