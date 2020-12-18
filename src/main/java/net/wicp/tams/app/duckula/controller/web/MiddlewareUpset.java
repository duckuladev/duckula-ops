package net.wicp.tams.app.duckula.controller.web;

import org.springframework.stereotype.Service;

import net.wicp.tams.common.connector.beans.CusDynaBean;
import net.wicp.tams.common.connector.executor.IBusiApp;
import net.wicp.tams.common.exception.ProjectException;

/***
 * 中间件配置
 * @author Andy.zhou
 *
 */
@Service("MiddlewareUpset")
public class MiddlewareUpset implements IBusiApp {

	@Override
	public CusDynaBean exe(CusDynaBean inputBean, CusDynaBean outBeanOri) throws ProjectException {
		// TODO Auto-generated method stub
		return null;
	}

}
