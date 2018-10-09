package com.zym.bio;

import java.io.BufferedReader;
import java.io.IOException;

public class ThreadClient implements  Runnable {


    private BufferedReader clientreader;


    public ThreadClient(BufferedReader reader){
        this.clientreader=reader;
    }

    @Override
    public void run() {
        String data=null;
        try {
            while ((data=clientreader.readLine())!=null){
                System.out.println(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(clientreader!=null){
                try {
                    clientreader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
