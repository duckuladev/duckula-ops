package net.wicp.tams.app.duckula.controller.service.websocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;
import net.wicp.tams.app.duckula.controller.config.constant.CommandType;
import net.wicp.tams.app.duckula.controller.config.constant.DeployType;
import net.wicp.tams.app.duckula.controller.service.DeployService;
import net.wicp.tams.app.duckula.controller.service.K8sService;
import net.wicp.tams.app.duckula.controller.service.deploy.IDeploy;
import net.wicp.tams.common.spring.autoconfig.SpringAssit;

@ServerEndpoint(value = "/websocket")
@Service
@Slf4j
public class PodLogSocket {

	// concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
	private static CopyOnWriteArraySet<PodLogSocket> webSocketSet = new CopyOnWriteArraySet<PodLogSocket>();

	// 与某个客户端的连接会话，需要通过它来给客户端发送数据
	private Session session;


	private K8sService k8sService;

	/**
	 * 连接建立成功调用的方法
	 */
	@OnOpen
	public void onOpen(Session session) {
		this.session = session;
		webSocketSet.add(this); // 加入set中
		try {
			sendMessage("begin show pod logs============================================================");
		} catch (Exception e) {
			log.error("error",e);
		}

	}

	/**
	 * 连接关闭调用的方法
	 */
	@OnClose
	public void onClose() {
		webSocketSet.remove(this); // 从set中删除
		log.info("close websocket");
	}

	/***
	 * 收到客户端消息后调用的方法
	 * 
	 * @param message  需要传deployId和configName
	 * @param session
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		// 什么也不做
		k8sService= (K8sService) SpringAssit.context.getBean(K8sService.class);		
		JSONObject jsonObj = JSONObject.parseObject(message);
		DeployService deployService=(DeployService) SpringAssit.context.getBean(DeployService.class);		
		String commandTypeStr = jsonObj.getString("commandType");
		long taskId = jsonObj.getLongValue("taskId");
		BufferedReader viewLogbuff = deployService.viewLog(CommandType.valueOf(commandTypeStr), taskId, jsonObj.getLong("deployId"));
		//BufferedReader viewLogbuff = k8sService.viewLog(jsonObj.getLong("deployId"),jsonObj.getString("configName"));// "t-rjzjh-jdbc"
		try {
			if(viewLogbuff==null) {
				sendMessage("查看日志错误，请联系相关人员");
				return;
			}
			String line;
			while ((line = viewLogbuff.readLine()) != null) {
				// System.out.println("=====" + line);
				sendMessage(line);
			}
		} catch (IOException e) {
			log.error("open websocket error", e);
		}
	}

	/***
	 * 发生错误时调用
	 * 
	 * @param session
	 * @param error
	 */
	@OnError
	public void onError(Session session, Throwable error) {
		log.error("connect websocket error", error);
	}

	public void sendMessage(String message) throws IOException {
		this.session.getBasicRemote().sendText(message);
	}

	// 群发消息
	public static void sendInfo(String message) throws IOException {
		for (PodLogSocket item : webSocketSet) {
			try {
				item.sendMessage(message);
			} catch (IOException e) {
				continue;
			}
		}
	}

}