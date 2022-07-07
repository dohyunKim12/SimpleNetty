package NgirlNetty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.EventExecutorGroup;

import java.nio.charset.Charset;

public class EchoClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx){
        String sendMessage = "Hello, Netty!";

        ByteBuf messageBuffer = Unpooled.buffer();
        messageBuffer.writeBytes(sendMessage.getBytes());

        StringBuilder builder = new StringBuilder();
        builder.append("Message Sent : ");
        builder.append(sendMessage);

        System.out.println(builder.toString());

        ctx.writeAndFlush(messageBuffer);
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        String readMessage = ((ByteBuf) msg).toString(Charset.defaultCharset());

        StringBuilder builder = new StringBuilder();
        builder.append("Recieved message : ");
        builder.append(readMessage);

        System.out.println(builder.toString());
    }
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx){
        ctx.close();
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }
}
