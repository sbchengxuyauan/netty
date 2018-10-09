package com.zym.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class TimeClientHandle implements Runnable {
    private String host;
    private int port;
    private Selector selector;
    private SocketChannel socketChannel;
    private volatile  Boolean stop;

    public TimeClientHandle(String host,int port)  {
        this.host=host;
        this.port=port;
        try {
            selector=Selector.open();
            socketChannel=SocketChannel.open();
            socketChannel.configureBlocking(false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            doConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (!stop){
            try {
                selector.select(1000);
                Set<SelectionKey> keys=selector.selectedKeys();
                Iterator<SelectionKey> iterkeys=keys.iterator();
                SelectionKey key=null;
                while (iterkeys.hasNext()){
                key=iterkeys.next();
                iterkeys.remove();
                handleInput(key);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void doConnection() throws IOException {
        if(socketChannel.connect(new InetSocketAddress(host,port))){
           socketChannel.register(selector,SelectionKey.OP_READ);
           doWrite(socketChannel);
        } else
            socketChannel.register(selector,SelectionKey.OP_CONNECT);


    }
    private void doWrite(SocketChannel socketChannel) throws IOException {
        byte[] req="连接成功".getBytes();
        ByteBuffer buffer=ByteBuffer.allocate(req.length);
        buffer.put(req);
        buffer.flip();
        socketChannel.write(buffer);
    }
    private  void handleInput(SelectionKey key) throws IOException {
        if(key.isValid()){ // 判断是否连接成功
         SocketChannel sc=(SocketChannel) key.channel();
         if(key.isConnectable()){
           if(sc.finishConnect()){
              sc.register(selector,SelectionKey.OP_READ);
              doWrite(sc);
           }else
               System.exit(1);
         }
         if(key.isReadable()){
           ByteBuffer buffer=ByteBuffer.allocate(1024);
           int readbytes=sc.read(buffer);
           if(readbytes>0){
               buffer.flip();
               byte[] bytes=new byte[buffer.remaining()];
               String body=new String(bytes,"UTF-8");
               System.out.println(body);
           }
           if(readbytes<0){
              key.cancel();
              key.channel().close();
           }
         }
        }

    }
}
