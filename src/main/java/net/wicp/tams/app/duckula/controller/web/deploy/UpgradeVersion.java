package net.wicp.tams.app.duckula.controller.web.deploy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.wicp.tams.app.duckula.controller.bean.models.CommonDeploy;
import net.wicp.tams.app.duckula.controller.bean.models.CommonVersion;
import net.wicp.tams.app.duckula.controller.dao.CommonDeployMapper;
import net.wicp.tams.app.duckula.controller.dao.CommonVersionMapper;
import net.wicp.tams.app.duckula.controller.service.DeployService;
import net.wicp.tams.common.Result;
import net.wicp.tams.common.connector.beans.CusDynaBean;
import net.wicp.tams.common.connector.executor.IBusiApp;
import net.wicp.tams.common.exception.ExceptAll;
import net.wicp.tams.common.exception.ProjectException;

/**
 * 升级版本
 * 
 * @author Andy
 *
 */
@Service(value = "deploy.UpgradeVersion")
public class UpgradeVersion implements IBusiApp {

	@Autowired
	private CommonDeployMapper commonDeployMapper;

	@Autowired
	private DeployService deployService;

	@Autowired
	private CommonVersionMapper commonVersionMapper;

	@Override
	public CusDynaBean exe(CusDynaBean inputBean, CusDynaBean outBeanOri) throws ProjectException {
		long deployId = Long.parseLong(inputBean.getStrValueByName("deployId"));
		long versionId = Long.parseLong(inputBean.getStrValueByName("versionId"));
		CommonDeploy commonDeploy = commonDeployMapper.selectById(deployId);
		if (commonDeploy == null) {
			throw new ProjectException(ExceptAll.param_error, "没有部署环境");
		}
		CommonVersion commonVersionNew = commonVersionMapper.selectById(versionId);
		if (commonVersionNew == null) {
			throw new ProjectException(ExceptAll.param_error, "没有指定的版本");
		}
		Result result = deployService.upgradeVersion(commonDeploy, commonVersionNew);
		outBeanOri.setResult(result);
		return outBeanOri;
	}
}
