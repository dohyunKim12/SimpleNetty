package NgirlNetty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class EchoServer {
    public static void main(String[] args) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1); // bossGroup의 thread는 하나.
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // workerGroup의 thread는 여러개.
        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch){
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new EchoServerHandler());
                            // Channel Pipeline에 handler를 다는 방식. 그리고 그것을 initChannel 메서드에서 실행.
                        }
                    });
            ChannelFuture f = b.bind(8888).sync(); // Async하게 binding. sync()는 binding이 완료될 때 까지 대기.
            //// ChannelFuture는 작업이 완료되었을 때 그 결과에 접근할 수 있도록 하는 Interface
            f.channel().closeFuture().sync(); //채널의 CloseFutre를 얻고 완료될 때 까지 blocking..

        }
        finally {
            // EventLoopGroup을 종료하고 모든 resource 해제..
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}