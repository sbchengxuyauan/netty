package com.zym.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public  static void main(String[] args) throws IOException {
        int port=9090;
        ServerSocket server=null;
        try {
            server=new ServerSocket(port);
            Socket socket=null;
            while (true){
                socket=server.accept();
                System.out.println(socket.getLocalPort());
                Thread thread=new Thread(new ThreadServer(socket));
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
            if(server!=null){
                server.close();
            }
        }

    }
}
