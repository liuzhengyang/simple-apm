package com.github.liuzhengyang.simpleapm.agent;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author liuzhengyang
 * Created on 2019-10-28
 */
public class ChannelContextHolder {
    private static ChannelHandlerContext ctx;
    public static void setChannelContext(ChannelHandlerContext channelContext) {
        ChannelContextHolder.ctx = channelContext;
    }

    public static ChannelHandlerContext getCtx() {
        return ctx;
    }
}
