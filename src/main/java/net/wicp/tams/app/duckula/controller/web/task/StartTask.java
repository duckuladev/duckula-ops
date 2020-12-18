package net.wicp.tams.app.duckula.controller.web.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.wicp.tams.app.duckula.controller.bean.models.CommonTask;
import net.wicp.tams.app.duckula.controller.config.constant.CommandType;
import net.wicp.tams.app.duckula.controller.dao.CommonTaskMapper;
import net.wicp.tams.app.duckula.controller.service.DeployService;
import net.wicp.tams.common.Result;
import net.wicp.tams.common.connector.beans.CusDynaBean;
import net.wicp.tams.common.connector.executor.IBusiApp;
import net.wicp.tams.common.exception.ProjectException;

@Service(value = "task.StartTask")
public class StartTask implements IBusiApp {

	@Autowired
	private CommonTaskMapper commonTaskMapper;
	@Autowired
	private DeployService deployService;

	@Override
	public CusDynaBean exe(CusDynaBean inputBean, CusDynaBean outBeanOri) throws ProjectException {
		long taskId = Long.parseLong(inputBean.getStrValueByName("taskId"));
		CommonTask commonTask = commonTaskMapper.selectById(taskId);
		Boolean isdebug = Boolean.parseBoolean(inputBean.getStrValueByName("isDebug"));// inputBean.getByType(Boolean.class,
																						// "isdebug");
		Result startTask = deployService.startTask(CommandType.task, commonTask.getId(), commonTask.getDeployId(),
				isdebug);
		outBeanOri.setResult(startTask);
		return outBeanOri;
	}
}
