package com.socket;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.alibaba.fastjson.JSONObject;

@ServerEndpoint("/websocket")
public class WebSocketServer {
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	private static final Set<Session> session = new HashSet<Session>();
	
	@OnOpen
	public void open(Session session) {
		// 添加初始化操作
		System.out.println(session + "建立连接");
		WebSocketServer.session.add(session);
	}
	
	@OnMessage 
	public void onmessage(Session session,String message){
		// 把客户端的消息解析为JSON对象
		JSONObject jsonObject = JSONObject.parseObject(message);
		// 在消息中添加发送日期
		jsonObject.put("date", DATE_FORMAT.format(new Date()));
		// 把消息发送给所有连接的会话
		for (Session openSession : WebSocketServer.session) {
			// 添加本条消息是否为当前会话本身发的标志
			jsonObject.put("isSelf", openSession.equals(session));
			// 发送JSON格式的消息
			openSession.getAsyncRemote().sendText(jsonObject.toString());
		}
	}
	
	@OnClose
	public void close(Session session,CloseReason reason) {
		// 添加关闭会话时的操作
		System.out.println(session + "关闭连接," + reason);
		WebSocketServer.session.remove(session);
	}

	@OnError
	public void error(Session session,Throwable t) {
		// 添加处理错误的操作
		System.out.println(session + "连接异常," + t);
	}

}
