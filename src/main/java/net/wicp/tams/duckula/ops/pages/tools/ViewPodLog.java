package net.wicp.tams.duckula.ops.pages.tools;

import org.apache.tapestry5.annotations.BeforeRenderTemplate;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.util.TextStreamResponse;

import com.alibaba.fastjson.JSONObject;

import net.wicp.tams.app.duckula.controller.config.constant.CommandType;
import net.wicp.tams.app.duckula.controller.service.DeployService;
import net.wicp.tams.common.Conf;
import net.wicp.tams.common.apiext.IOUtil;
import net.wicp.tams.component.services.IReq;
import net.wicp.tams.component.tools.TapestryAssist;

public class ViewPodLog {

	@Inject
	protected RequestGlobals requestGlobals;
	@Inject
	protected Request request;
	@Inject
	private IReq req;

	@Property
	private String wsUri;
	@Property
	private String params;

	@Inject
	private DeployService deployService;

	@BeforeRenderTemplate
	public void init() {
		// configName deployid
		String context = Conf.get("common.http.url.context");
		this.wsUri = context.replace(requestGlobals.getHTTPServletRequest().getScheme(), "ws");
		this.wsUri = IOUtil.mergeFolderAndFilePath(this.wsUri, "/websocket");
		String params = request.getParameter("params");
		String[] paramsAry = params.split(":");
		JSONObject json = new JSONObject();
		// commandType taskId deployId
		json.put("commandType", paramsAry[0]);
		json.put("taskId", paramsAry[1]);
		json.put("deployId", paramsAry[2]);
		this.params = json.toJSONString();
	}

	public TextStreamResponse onOldConfig(String type, Long id, Long deployId) {
		String viewConf = deployService.viewConfDeploy(CommandType.valueOf(type), id, deployId);
		//viewConf = viewConf.replaceAll("\\r\\n", "<br/><br/>");
		viewConf = viewConf.replaceAll("\\n", "<br/>");
		return TapestryAssist.getTextStreamResponse(viewConf);
	}

	public TextStreamResponse onNewConfig(String type, Long id, Long deployId) {
		String viewConf = deployService.viewConf(CommandType.valueOf(type), id, deployId);
		viewConf = viewConf.replaceAll("\\r\\n", "<br/>");
		// String retstr = "abc<br/> efc<br/>aaa";
		return TapestryAssist.getTextStreamResponse(viewConf);
	}

}
