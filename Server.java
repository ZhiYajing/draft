/*
 * Multithread foe supporting multiple users
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	//multithread 
	class ServerThread implements Runnable {		  
	    private Socket client = null;
	    public ServerThread(Socket client){  
	        this.client = client;  
	    }  
	      
	    @Override  
	    public void run() {
	        PrintStream printStream = null;
	        try{
	            printStream = new PrintStream(client.getOutputStream());
	            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
	            boolean passwordValid = false;
	            while(true){
	                String line =  reader.readLine();
	                if(line == null){
	                    continue;
	                }
	                if(line.startsWith("password:") && !passwordValid){
	                    String password = line.replaceFirst("password:","");
	                    BufferedReader passwordReader = new BufferedReader(new FileReader(new File("password.txt")));
	                    String correctPassword = passwordReader.readLine();
	                    if((password).equals(correctPassword)){
	                        passwordValid = true;
	                    }
	                }
	                if(line.startsWith("list:") && passwordValid){
	                    String dir = line.replaceFirst("list:","");
	                    BufferedReader pathReader = new BufferedReader(new FileReader("path.txt"));
	                    String path = pathReader.readLine();
	                    path = path + dir;
	                    File temp = new File(path);
	                    if(temp.isDirectory()){
	                        StringBuffer stringBuffer = new StringBuffer();
	                        File files[] = temp.listFiles();
	                        for(File file : files){
	                            String type = "file";
	                            String size = file.length()+"";
	                            if(file.isDirectory()){
	                                type = "dir";
	                                size = getDirSize(file)+"";
	                            }
	                            stringBuffer.append(file.getName()+"?"+type+"?"+size+"|");
	                        }
	                        if(stringBuffer.length()-1>=0) {
	                            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
	                        }
	                        printStream.println(stringBuffer.toString());
	                    }
	                }
	                if(line.startsWith("download:") && passwordValid){
	                    String dir = line.replaceFirst("download:","");
	                    BufferedReader pathReader = new BufferedReader(new FileReader("path.txt"));
	                    String path = pathReader.readLine();
	                    path = path + dir;
	                    File temp = new File(path);
	                    if(temp.isFile()){
	                        byte[] tempbytes = new byte[100];
	                        int byteread = 0;
	                        FileInputStream in = new FileInputStream(temp);

	                        while ((byteread = in.read(tempbytes)) != -1) {
	                            client.getOutputStream().write(tempbytes,0,byteread);
	                        }
	                    }
	                }
	                if(!passwordValid){
	                    printStream.println("InvalidPassword");
	                    break;
	                }
	            }

	        }catch(Exception e){
	            e.printStackTrace();
	        }finally {
	                if(printStream!=null){
	                printStream.close();
	            }
	            try {
	                client.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }

// Get the direction size (sum of all its files' size)
	    private long getDirSize(File file) {
	        if(file.isFile()){
	            return file.length();
	        }else{
	            File arr[] = file.listFiles();
	            if(arr !=null) {
	                long size = 0;
	                for (File t : arr) {
	                    size += getDirSize(t);
	                }
	                return size;
	            }else{
	                return 0;
	            }
	        }
	    }
	}
	
//Establish connection with client
    public void serve() throws IOException {
        ServerSocket server = new ServerSocket(9000);
        try {
            while (true) {
                Socket client = server.accept();
               Thread t= new Thread(new ServerThread(client));
               t.start();
            }
        }finally {
            server.close();
        }
    }
}
