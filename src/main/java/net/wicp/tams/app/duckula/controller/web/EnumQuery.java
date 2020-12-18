package net.wicp.tams.app.duckula.controller.web;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;

import net.wicp.tams.common.apiext.json.JSONUtil;
import net.wicp.tams.common.connector.beans.CusDynaBean;
import net.wicp.tams.common.connector.executor.IBusiApp;
import net.wicp.tams.common.exception.ProjectException;

/***
 * 枚举类型
 * 
 * @author Andy
 *
 */

@Service(value = "EnumQuery")
public class EnumQuery implements IBusiApp {

	@Override
	public CusDynaBean exe(CusDynaBean inputBean, CusDynaBean outBeanOri) throws ProjectException {
		String enumClassStr = inputBean.getStrValueByName("enumClass");
		JSONArray jsonArrayFromEnum = JSONUtil.getJsonArrayFromEnum(enumClassStr);
		outBeanOri.set("listjson", jsonArrayFromEnum.toJSONString());
		return outBeanOri;
	}

}
