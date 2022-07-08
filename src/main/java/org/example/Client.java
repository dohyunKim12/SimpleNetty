package org.example;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.net.InetSocketAddress;
import java.util.Scanner;

public class Client {
    private static final int SERVER_PORT = 3000;
    private final String host;
    private final int port;

    private Channel serverChannel;
    private EventLoopGroup eventLoopGroup;

    public Client(String host, int port){
        this.host = host;
        this.port = port;
    }

    public void connect() throws InterruptedException {
        eventLoopGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("client"));
        // 1. EventLoopGroup 객체 생성.

        Bootstrap bootstrap = new Bootstrap().group(eventLoopGroup);
        // 2. eventLoopGroup 객체를 이용해 Bootstrap 객체를 생성.

        bootstrap.channel(NioSocketChannel.class);
        // 3. bootstrap 객체에 Non-Blocking IO(Async) Socket 채널을 사용.
        bootstrap.remoteAddress(new InetSocketAddress(host, port));
        bootstrap.handler(new ClientInitializer());
        // 4. ClientInitializer

        serverChannel = bootstrap.connect().sync().channel();
    }

    private void start() throws InterruptedException {
        Scanner scanner = new Scanner(System.in);

        String message;
        ChannelFuture future;

        while(true) {
            // 사용자 입력.
            message = scanner.nextLine();

            // Server로 전송.
            future = serverChannel.writeAndFlush(message.concat("\n"));

            if("quit".equals(message)){
                serverChannel.closeFuture().sync();
                break;
            }
        }

        // 종료되기 전 모든 메세지가 flush 될 때 까지 대기.
        if(future != null){
            future.sync();
        }
    }

    public void close(){
        eventLoopGroup.shutdownGracefully();
    }

    public static void main(String[] args) throws Exception{
        System.out.println("[Request from CLIENT] Enter your menus : ");
        Client client = new Client("127.0.0.1", SERVER_PORT);

        try {
            client.connect();
            client.start();
        } finally {
            client.close();
        }
    }

}
