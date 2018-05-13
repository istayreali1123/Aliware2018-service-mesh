package com.alibaba.dubbo.performance.demo.agent.provideragent;


import com.alibaba.dubbo.performance.demo.agent.provideragent.model.RpcRequest;

import io.netty.buffer.ByteBuf;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

import java.security.Provider;


/**
 * Created by 79422 on 2018/5/4.
 */
public class ConsumerAgentMsgHandler extends SimpleChannelInboundHandler<DatagramPacket> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        //fix me:每次做肯定有一定的性能损耗
        ProviderAgent.setMsgReturner(msg.sender());
        ByteBuf buf = msg.content();
        buf.retain();
        long id=buf.readLong();
        ByteBuf dataBuf=buf.slice(8,buf.readableBytes());
//        System.out.println(id);
//        System.out.println(dataBuf.toString(CharsetUtil.UTF_8));
        RpcRequest request=new RpcRequest(id,dataBuf);
        ProviderChannelManager.getChannel().write(request);

        /*用于验证不经过provider性能*/
//        byte [] data=java.lang.String.valueOf(dataBuf.toString(io.netty.util.CharsetUtil.UTF_8).hashCode()).getBytes();
//        ByteBuf sendBuf=ctx.alloc().ioBuffer(8+data.length);
//        sendBuf.writeLong(id);
//        sendBuf.writeBytes(data);
//        DatagramPacket dp=new DatagramPacket(sendBuf,msg.sender());
//        ctx.writeAndFlush(dp);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ProviderChannelManager.getChannel().flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
    }
}

