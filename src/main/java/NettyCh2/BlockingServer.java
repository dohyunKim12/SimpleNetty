package NettyCh2;

import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class BlockingServer {
    public static void main(String[] args) throws Exception {
        BlockingServer server = new BlockingServer();
        server.run();
    }

    private void run() throws IOException {
        ServerSocket server = new ServerSocket(8888);
        System.out.println("접속 대기중");

        while(true){
            Socket sock = server.accept();
            System.out.println("Client Connected.");

            OutputStream out = sock.getOutputStream();
            InputStream in = sock.getInputStream();

            while(true){
                try {
                    int request = in.read();
                    out.write(request);
                } catch(IOException e){
                    break;
                }
            }
        }
    }
}
