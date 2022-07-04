package org.example;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class EchoServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        String message = (String)msg;

        Channel channel = ctx.channel();

        if ("quit".equals(message)) {
            ctx.close();
        }
        else {
            channel.writeAndFlush("[Response from Server] '" + message + "' received!\n");
        }
    }
}
