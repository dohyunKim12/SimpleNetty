package org.example;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class EchoServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected  void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipline = ch.pipeline();

        pipline.addLast(new LineBasedFrameDecoder(65536));
        pipline.addLast(new StringDecoder());
        pipline.addLast(new StringEncoder());
        pipline.addLast(new EchoServerHandler());
    }
}
