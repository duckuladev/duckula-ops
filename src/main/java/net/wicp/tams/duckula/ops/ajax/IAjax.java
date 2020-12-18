package net.wicp.tams.duckula.ops.ajax;

import com.alibaba.fastjson.JSONObject;

import net.wicp.tams.common.apiext.json.JSONUtil;
import net.wicp.tams.common.apiext.json.ResponseBean;
import net.wicp.tams.common.exception.ProjectException;
import net.wicp.tams.common.http.HttpClient;

public interface IAjax {
	public default ResponseBean req(String key, JSONObject params) throws ProjectException {
		JSONObject doPostRela = HttpClient.doPostRela("/connector", params);
		ResponseBean responseBean = JSONUtil.convertResponBean(doPostRela);
		return responseBean;
	}
}
