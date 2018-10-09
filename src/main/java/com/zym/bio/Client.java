package com.zym.bio;

import java.io.*;
import java.net.Socket;

public class Client {
    public static void  main(String[] args) {
        Socket socket=null;
        BufferedReader in=null;
        PrintWriter out=null;
        try {
            socket=new Socket("127.0.0.1",9090);
            in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out=new PrintWriter(socket.getOutputStream(),true);
            out.println("Hi,I am Client");
            String data=null;
            while ((data=in.readLine())!=null){
            System.out.println(data);
            }
        } catch (IOException e) {
        }finally {
            try {
                if (out != null) {
                    out.close();
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        }
    }

