package com.zym.bio;

import java.io.*;
import java.net.Socket;

public class ThreadServer implements Runnable {

    private Socket socket;


    public  ThreadServer(Socket socket){
        this.socket=socket;
        System.out.println(socket.getLocalPort());
    }

    @Override
    public void run() {
        System.out.println("开始运行");
        BufferedReader in=null;
        PrintWriter out=null;
        try {
             in=new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
             out=new PrintWriter(this.socket.getOutputStream(),true);
             System.out.println(in.readLine());
//            while ((data=in.readLine())!=null){
//             System.out.println("client:"+data);
//            }
            out.println("Hi,I am Server");
            for (int i=0;i<10;i++){
            out.println("response:"+i);
            }
        } catch (IOException e) {
            e.printStackTrace();
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if(out !=null) {
                    out.close();
            }
        }
    }
}
