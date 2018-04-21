//package file.share.socket;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public void run() throws IOException {
        ServerSocket server = new ServerSocket(9000);
        try {
            while (true) {
                Socket client = server.accept();
                new Thread(new ServerThread(client)).start();
            }
        }finally {
            server.close();
        }
    }
}
