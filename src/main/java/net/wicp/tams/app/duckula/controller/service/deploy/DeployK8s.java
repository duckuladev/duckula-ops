package net.wicp.tams.app.duckula.controller.service.deploy;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1Job;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1Status;
import net.wicp.tams.app.duckula.controller.BusiTools;
import net.wicp.tams.app.duckula.controller.bean.models.CommonConsumer;
import net.wicp.tams.app.duckula.controller.bean.models.CommonDump;
import net.wicp.tams.app.duckula.controller.bean.models.CommonMiddleware;
import net.wicp.tams.app.duckula.controller.bean.models.CommonTask;
import net.wicp.tams.app.duckula.controller.bean.models.CommonVersion;
import net.wicp.tams.app.duckula.controller.config.ConfigItem;
import net.wicp.tams.app.duckula.controller.config.constant.CommandType;
import net.wicp.tams.app.duckula.controller.config.constant.DeployType;
import net.wicp.tams.app.duckula.controller.dao.CommonCheckpointMapper;
import net.wicp.tams.app.duckula.controller.dao.CommonConsumerMapper;
import net.wicp.tams.app.duckula.controller.dao.CommonDumpMapper;
import net.wicp.tams.app.duckula.controller.dao.CommonInstanceMapper;
import net.wicp.tams.app.duckula.controller.dao.CommonMiddlewareMapper;
import net.wicp.tams.app.duckula.controller.dao.CommonTaskMapper;
import net.wicp.tams.app.duckula.controller.dao.CommonVersionMapper;
import net.wicp.tams.app.duckula.controller.service.K8sService;
import net.wicp.tams.common.Result;
import net.wicp.tams.common.beans.Host;

/***
 * 服务名要与DeployType同名
 * 
 * @author Andy.zhou
 *
 */
@Service("k8s")
public class DeployK8s implements IDeploy {
	@Autowired
	private K8sService k8sService;
	@Autowired
	private CommonTaskMapper commonTaskMapper;
	@Autowired
	private CommonDumpMapper commonDumpMapper;
	@Autowired
	private CommonConsumerMapper commonConsumerMapper;
	@Autowired
	private CommonMiddlewareMapper commonMiddlewareMapper;
	@Autowired
	private CommonInstanceMapper commonInstanceMapper;
	@Autowired
	private CommonVersionMapper commonVersionMapper;
	@Autowired
	private CommonCheckpointMapper commonCheckpointMapper;

	@Override
	public Result checkExit(Long deployid, CommandType taskType, Long taskId) {
		String viewConfDeploy = viewConfDeploy(deployid, taskType, taskId);
		if (!viewConfDeploy.startsWith("error:")) {
			return Result.getSuc();
		} else {
			return Result.getError(viewConfDeploy);
		}
	}

	@Override
	public String viewConfDeploy(Long deployid, CommandType taskType, Long taskId) {
		String configName = null;
		switch (taskType) {
		case task:
			CommonTask selectTask = commonTaskMapper.selectById(taskId);
			configName = taskType.formateConfigName(selectTask.getName());
			break;
		case consumer:
			CommonConsumer commonConsumer = commonConsumerMapper.selectById(taskId);
			configName = taskType.formateConfigName(commonConsumer.getName());
			break;
		case dump:
			CommonDump commonDump = commonDumpMapper.selectById(taskId);
			configName = taskType.formateConfigName(commonDump.getName());
			break;
		default:
			break;
		}
		try {
			V1ConfigMap selectConfigMap = k8sService.selectConfigMap(deployid, configName);
			if (selectConfigMap == null) {
				return "error:没有配置";
			} else {
				String configmap = selectConfigMap.getData().get("configmap.properties");
				return configmap;
			}
		} catch (Throwable e) {
			return "error:" + e.getMessage();
		}
	}

	@Override
	public Result addConfig(Long deployid, CommandType commandType, Long taskId) {
		Map<String, Object> params = new HashMap<String, Object>();
		String configName = BusiTools.configContext(commonConsumerMapper, commonTaskMapper, commonCheckpointMapper,
				commonDumpMapper, commonMiddlewareMapper, commonInstanceMapper, commandType, taskId, params);
		k8sService.deployConfigmap(deployid, configName, params);
		return Result.getSuc();
	}

	@Override
	public String viewConf(Long deployid, CommandType commandType, Long taskId) {
		Map<String, Object> params = new HashMap<String, Object>();
		BusiTools.configContext(commonConsumerMapper, commonTaskMapper, commonCheckpointMapper, commonDumpMapper,
				commonMiddlewareMapper, commonInstanceMapper, commandType, taskId, params);
		String oriConfig = k8sService.OriConfig(deployid, params);
		return oriConfig;
	}

	@Override
	public Result deleteConfig(Long deployid, CommandType commandType, Long taskId) {
		String configName = null;
		switch (commandType) {
		case task:
			CommonTask selectTask = commonTaskMapper.selectById(taskId);
			configName = commandType.formateConfigName(selectTask.getName());
			break;
		default:
			break;
		}
		k8sService.deleteConfigmap(deployid, configName);
		return Result.getSuc();
	}

	@Override
	public Result start(Long deployid, CommandType taskType, Long taskId, boolean isDebug) {
		if (!checkExit(deployid, taskType, taskId).isSuc()) {
			addConfig(deployid, taskType, taskId);
		}
		String configName = null;
		Map<String, Object> params = new HashMap<String, Object>();
		switch (taskType) {
		case task:
			CommonTask selectTask = commonTaskMapper.selectById(taskId);
			configName = taskType.formateTaskName(selectTask.getName());
			params.put(ConfigItem.task_name, configName);
			CommonVersion commonVersion = commonVersionMapper.selectById(selectTask.getVersionId());
			params.put(ConfigItem.task_version, BusiTools.packVersionImage(commonVersion, true));
			params.put(ConfigItem.task_data_version, BusiTools.packVersionImage(commonVersion, false));
			params.put(ConfigItem.task_image, BusiTools.getImageGroup(commonVersion.getImageGroup()));
			params.put(ConfigItem.task_debug, isDebug);
			params.put(ConfigItem.configmap_name, taskType.formateConfigName(selectTask.getName()));
			// 处理中间件的hosts
			CommonMiddleware middleware = commonMiddlewareMapper.selectById(selectTask.getMiddlewareId());
			List<Host> jsonToHosts = Host.jsonToHosts(middleware.getHostsconfig());
			params.put(ConfigItem.task_hosts, jsonToHosts);
			V1Deployment deployTask = k8sService.deployTask(deployid, params);
			if (deployTask != null) {
				return Result.getSuc();
			} else {
				return Result.getError("布署失败");
			}
		case dump:
			CommonDump commonDump = commonDumpMapper.selectById(taskId);
			configName = taskType.formateTaskName(commonDump.getName());
			params.put(ConfigItem.task_name, configName);
			CommonVersion commonVersion2 = commonVersionMapper.selectById(commonDump.getVersionId());
			params.put(ConfigItem.task_version, BusiTools.packVersionImage(commonVersion2, true));
			params.put(ConfigItem.task_data_version, BusiTools.packVersionImage(commonVersion2,false));
			params.put(ConfigItem.task_image, BusiTools.getImageGroup(commonVersion2.getImageGroup()));
			params.put(ConfigItem.configmap_name, taskType.formateConfigName(commonDump.getName()));
			// 处理中间件的hosts
			CommonMiddleware middleware2 = commonMiddlewareMapper.selectById(commonDump.getMiddlewareId());
			List<Host> jsonToHosts2 = Host.jsonToHosts(middleware2.getHostsconfig());
			params.put(ConfigItem.task_hosts, jsonToHosts2);
			V1Job v1Job = k8sService.deployDump(deployid, params);
			if (v1Job != null) {
				return Result.getSuc();
			} else {
				return Result.getError("布署失败");
			}
		default:
			break;
		}
		return Result.getError("还未实现的布署类型");
	}

	@Override
	public Result stop(Long deployid, CommandType taskType, Long taskId) {
		String configName = null;
		V1Status stopTask = null;
		switch (taskType) {
		case task:
			CommonTask selectTask = commonTaskMapper.selectById(taskId);
			configName = taskType.formateTaskName(selectTask.getName());
			stopTask = k8sService.stopTask(deployid, configName);
			break;
		case consumer:
			CommonConsumer commonConsumer = commonConsumerMapper.selectById(taskId);
			configName = taskType.formateTaskName(commonConsumer.getName());
			stopTask = k8sService.stopTask(deployid, configName);
			break;
		case dump:
			CommonDump commonDump = commonDumpMapper.selectById(taskId);
			configName = taskType.formateTaskName(commonDump.getName());
			stopTask = k8sService.stopDump(deployid, configName);
			return Result.getSuc();// 特殊处理
		default:
			break;
		}
		if ("Success".equals(stopTask.getStatus())) {
			if (checkExit(deployid, taskType, taskId).isSuc()) {// 删除配置信息
				deleteConfig(deployid, taskType, taskId);
			}
			return Result.getSuc();
		} else {
			return Result.getError(stopTask.getReason());
		}
	}

	@Override
	public String queryStatus(Long deployid, CommandType taskType, Long taskId) {
		String configName = null;
		switch (taskType) {
		case task:
			CommonTask selectTask = commonTaskMapper.selectById(taskId);
			configName = taskType.formateTaskName(selectTask.getName());
			break;
		case consumer:
			CommonConsumer commonConsumer = commonConsumerMapper.selectById(taskId);
			configName = taskType.formateTaskName(commonConsumer.getName());
			break;
		case dump:
			CommonDump commonDump = commonDumpMapper.selectById(taskId);
			configName = taskType.formateTaskName(commonDump.getName());
			break;
		default:
			break;
		}
		try {
			V1Pod queryPod = k8sService.selectPod(deployid, configName);
			return DeployType.k8s.getStatus(queryPod.getStatus().getPhase());
			// 结果不太准确
			// V1Deployment selectDeployment = k8sService.selectDeployment(deployid,
			// configName);
			// Integer availableReplicas =
			// selectDeployment.getStatus().getAvailableReplicas();
			// String statusstr=availableReplicas==1?"Running":"Stoping";
			// return DeployType.k8s.getStatus(statusstr);
		} catch (Exception e) {
			return DeployType.k8s.getStatus(null);
		}
	}

	@Override
	public BufferedReader viewLog(Long deployid, CommandType taskType, Long taskId) {
		String configName = null;
		switch (taskType) {
		case task:
			CommonTask selectTask = commonTaskMapper.selectById(taskId);
			configName = taskType.formateTaskName(selectTask.getName());
			break;
		case consumer:
			CommonConsumer commonConsumer = commonConsumerMapper.selectById(taskId);
			configName = taskType.formateTaskName(commonConsumer.getName());
			break;
		case dump:
			CommonDump commonDump = commonDumpMapper.selectById(taskId);
			configName = taskType.formateTaskName(commonDump.getName());
			break;
		default:
			break;
		}
		BufferedReader viewLog = k8sService.viewLog(deployid, configName);
		return viewLog;
	}

}
