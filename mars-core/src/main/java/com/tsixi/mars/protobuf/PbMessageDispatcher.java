/**
 * Copyright Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 * <p>
 * 2017年2月8日
 */
package com.tsixi.mars.protobuf;

import com.tsixi.mars.net.Connect;
import com.tsixi.mars.net.MarsSession;
import com.tsixi.mars.net.NetAddress;
import com.tsixi.mars.netty.ConnectImpl;
import com.tsixi.mars.protobuf.PbMessage.TXMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.lang.annotation.Annotation;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * pb协议消息分发器
 *
 * @author Alan
 * @scene 1.0
 */
@Sharable
public class PbMessageDispatcher extends
        SimpleChannelInboundHandler<PbMessage.TXMessage> implements
        ApplicationListener<ContextRefreshedEvent>, PbMessageHandler {

    Logger log = LoggerFactory.getLogger(getClass());

    private Map<Integer, PbMessageHandler> messageHandlers = new HashMap<>();

    Class<? extends Annotation> aclazz;

    AttributeKey<MarsSession> attributeKey;


    /**
     * 消息分发起构造器
     */
    public PbMessageDispatcher(Class<? extends Annotation> aclazz) {
        this.aclazz = aclazz;
        attributeKey = AttributeKey.valueOf("session");
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.debug("开始初始化 {} 消息分发器", aclazz);
        ApplicationContext context = event.getApplicationContext();
        Map<String, Object> beans = context.getBeansWithAnnotation(aclazz);
        List<PbMessageHandler> handlers = PbMessageHelper.findHandler(beans.values(), PbMessageHandler.class);
        handlers.forEach(handler -> {
            int messageType = handler.getMessageType();
            messageHandlers.put(messageType, handler);
            log.info("注册 {} 消息处理 {} -> {} ",
                    aclazz.getSimpleName(), messageType,
                    handler);
        });
    }

    public void addMessageHandler(PbMessageHandler handler) {
        log.info("注册 {} 消息处理 {} -> {} ",
                aclazz.getSimpleName(), handler.getMessageType(),
                handler);
        this.messageHandlers.put(handler.getMessageType(), handler);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TXMessage msg)
            throws Exception {
        MarsSession session = getMarsSession(ctx.channel());
        handle(session, msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("channel active,ctx= {}", ctx);
        }
        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        NetAddress netAddress = new NetAddress(address.getAddress().getHostAddress(), address.getPort());
        Connect connect = new ConnectImpl(ctx.channel());
        MarsSession session = new MarsSession(ctx.channel().id().asShortText(), netAddress, connect);
        AttributeKey<MarsSession> sessionKey = AttributeKey.valueOf("session");
        ctx.channel().attr(sessionKey).set(session);
    }

    /**
     * 连接通道注册成功时被调用
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        if (log.isDebugEnabled()) {
            log.debug("channel registered,ctx= {}", ctx);
        }
    }

    /**
     * 连接断开时被调用
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        if (log.isDebugEnabled()) {
            log.debug("channel unregistered,ctx= {}", ctx);
        }
        MarsSession session = getMarsSession(ctx.channel());
        ctx.channel().attr(attributeKey).set(null);
        if (session != null) {
            session.onClose();
        }
    }

    /**
     * 获取绑定在通道上下文中的自定义session
     *
     * @param channel
     * @return
     */
    public MarsSession getMarsSession(Channel channel) {
        MarsSession session = channel.attr(attributeKey).get();
        return session;
    }

    @Override
    public int getMessageType() {
        return 0;
    }

    @Override
    public void handle(MarsSession connect, TXMessage msg) {
        int messageType = msg.getMessageType();
        if (log.isDebugEnabled()) {
            log.debug("message received...,messageType={},address={} ", messageType, connect.address());
        }
        PbMessageHandler pbMessageHandler = messageHandlers.get(messageType);
        if (pbMessageHandler != null) {
            try {
                pbMessageHandler.handle(connect, msg);
            } catch (Exception e) {
                log.warn("message handle exceptions,messageType={},address={}", messageType, connect.address(), e);
            }
        } else {
            log.info("message not have handler,messageType={},address={}", messageType, connect.address());
        }
    }
}
