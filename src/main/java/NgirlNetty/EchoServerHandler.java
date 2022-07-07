package NgirlNetty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.Charset;

public class EchoServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // Return Echo msg
        String readMessage  = ((ByteBuf)msg).toString(Charset.defaultCharset());

        System.out.println("Message from client: " + readMessage);
//        Channel ch = ctx.channel();
//        ch.writeAndFlush( "msg from echo server");
//        현재 이 코드에서 이러한 방식이 안되는 이유는 Server Channel initializing 시
//        pipline.addLast(new LineBasedFrameDecoder(65536));
//        pipline.addLast(new StringDecoder());
//        pipline.addLast(new StringEncoder()); 이러한 것들이 pipline에 add 되어있어야 함. String decoder가 장착되지 않아서 client 측에서 메세지가 뜨지 않는다.

        ctx.write(msg); // 얘는 그냥 msg Object 자체를 다시 돌려보내는 것이므로 가능.
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx){
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }
}