package com.zym.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class MultiplexerTimeServer implements  Runnable {
    private Selector selector;   //选择器
    private ServerSocketChannel serverchannel;
    private volatile  boolean stop;


    public MultiplexerTimeServer (int port){
        try {
            selector=Selector.open();                   //初始化选择器
            serverchannel=ServerSocketChannel.open();   //开启服务端通道
            serverchannel.configureBlocking(false);  //开启异步非阻塞模式
            serverchannel.socket().bind(new InetSocketAddress(port),1024); //绑定端口号
            serverchannel.register(selector,SelectionKey.OP_CONNECT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  void stop(){
        this.stop=true;
    }

    @Override
    public void run() {
    while (!stop){
        try {
            selector.select(10000);  //每隔1秒遍历一次
            Set<SelectionKey> keys=selector.selectedKeys();
            Iterator<SelectionKey> iterkeys=keys.iterator();
            SelectionKey key=null;
            while (iterkeys.hasNext()){
               key=iterkeys.next();
               iterkeys.remove();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    }


    private  void handleInput(SelectionKey key){
        try {
            if (key.isValid()) {
                if (key.isAcceptable()) {     // 如果连接就绪
                    ServerSocketChannel scc = (ServerSocketChannel) key.channel();
                    SocketChannel sc = scc.accept();            // 当连接准备就绪时,创建socketchannel连接实例
                    sc.configureBlocking(false);
                    sc.register(selector,SelectionKey.OP_READ); // 添加读事件通道
                }
                if(key.isReadable()) {
                    SocketChannel readchannel=(SocketChannel)key.channel();
                    ByteBuffer buffer=ByteBuffer.allocate(1024);
                    int readbytes=readchannel.read(buffer);
                    if(readbytes > 0){
                    buffer.flip();     // 翻转
                    byte[] bytes=new byte[buffer.remaining()];
                    buffer.get(bytes);
                    String body=new String(bytes,"UTF-8");
                    System.out.println("客户端发送消息:"+body);
                    }else  if(readbytes < 0){
                    key.cancel();
                    readchannel.close();
                    }
                }

            }
        }catch (IOException e) {

        }
    }

    /**
     * 返回写入的消息
     * @param response
     * @param channel
     */
    private  void dowrite(String response,SocketChannel channel){
    if(response!=null || response.trim().length() > 0){
      byte[] bytes=response.getBytes();
      ByteBuffer buffer=ByteBuffer.allocate(bytes.length);
      buffer.put(bytes);
        try {
            channel.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    }
}
