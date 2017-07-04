/**
 * Copyright Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 *
 * 2017年2月8日 	
 */
package com.tsixi.mars.protobuf;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.tsixi.mars.net.MarsSession;
import com.tsixi.mars.protobuf.PbMessage.ReqPingMessage;
import com.tsixi.mars.protobuf.PbMessage.ResPingMessage;
import com.tsixi.mars.protobuf.PbMessage.TXMessage;

/**
 *
 * 
 * @scene 1.0
 * 
 * @author Alan
 *
 */
@Component
@ServerHandler
public class PingMessageHandler implements PbMessageHandler {

	Logger log = Logger.getLogger(getClass());

	@Override
	public void handle(MarsSession session, TXMessage msg) {
		if (log.isDebugEnabled()) {
			log.debug("ping message received.");
		}
		ByteString bs = msg.getDataMessage();
//		System.out.println(bs.size());
		try {
			ReqPingMessage pm = ReqPingMessage.parseFrom(bs);
			int mt = pm.getMessageType();
			long time = pm.getTime();
			long currentTime = System.currentTimeMillis();
			int ping = (int) (currentTime - time);
			log.debug("ping message parse ok, time=" + time + ",ping=" + ping
					+ ",messageType=" + mt);
			ResPingMessage rpm = ResPingMessage.newBuilder().setPing(ping)
					.build();
			session.send(rpm);
		} catch (InvalidProtocolBufferException e) {
			log.warn("ping message parse warn.", e);
		}
	}

	@Override
	public int getMessageType() {
		return ReqPingMessage.getDefaultInstance().getMessageType();
	}

}
