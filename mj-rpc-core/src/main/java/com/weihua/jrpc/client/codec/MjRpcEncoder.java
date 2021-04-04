package com.weihua.jrpc.client.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * encoder
 */
public class MjRpcEncoder extends MessageToByteEncoder<Object> {
	
	private Class<?> genericClass;

	public MjRpcEncoder(Class<?> genericClass) {
		this.genericClass = genericClass;
	}	
	

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
		if(genericClass.isInstance(msg)) {
			byte[] data = Serialization.serialize(msg);
			out.writeInt(data.length);
			out.writeBytes(data);
		}
	}

}
