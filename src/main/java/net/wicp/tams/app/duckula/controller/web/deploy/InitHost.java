package net.wicp.tams.app.duckula.controller.web.deploy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.wicp.tams.app.duckula.controller.bean.models.CommonDeploy;
import net.wicp.tams.app.duckula.controller.dao.CommonDeployMapper;
import net.wicp.tams.app.duckula.controller.service.DeployService;
import net.wicp.tams.common.Result;
import net.wicp.tams.common.connector.beans.CusDynaBean;
import net.wicp.tams.common.connector.executor.IBusiApp;
import net.wicp.tams.common.exception.ExceptAll;
import net.wicp.tams.common.exception.ProjectException;

/**
 * 初始化主机对于host和docker模式有用,centos7
 * 
 * @author Andy
 *
 */
@Service(value = "deploy.InitHost")
public class InitHost implements IBusiApp {

	@Autowired
	private CommonDeployMapper commonDeployMapper;
	@Autowired
	private DeployService deployService;

	@Override
	public CusDynaBean exe(CusDynaBean inputBean, CusDynaBean outBeanOri) throws ProjectException {
		long deployId = Long.parseLong(inputBean.getStrValueByName("deployId"));
		String pwd = inputBean.getStrValueByName("pwd");
		CommonDeploy commonDeploy = commonDeployMapper.selectById(deployId);
		if (commonDeploy == null) {
			throw new ProjectException(ExceptAll.param_error, "没有部署环境");
		}
		Result result = deployService.initHost(commonDeploy, pwd);
		outBeanOri.setResult(result);
		return outBeanOri;
	}
}
