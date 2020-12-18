package net.wicp.tams.app.duckula.controller.service.deploy;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.ethz.ssh2.Session;
import lombok.extern.slf4j.Slf4j;
import net.wicp.tams.app.duckula.controller.BusiTools;
import net.wicp.tams.app.duckula.controller.bean.models.CommonConsumer;
import net.wicp.tams.app.duckula.controller.bean.models.CommonDeploy;
import net.wicp.tams.app.duckula.controller.bean.models.CommonDump;
import net.wicp.tams.app.duckula.controller.bean.models.CommonTask;
import net.wicp.tams.app.duckula.controller.config.constant.CommandType;
import net.wicp.tams.app.duckula.controller.config.constant.DeployType;
import net.wicp.tams.app.duckula.controller.dao.CommonCheckpointMapper;
import net.wicp.tams.app.duckula.controller.dao.CommonConsumerMapper;
import net.wicp.tams.app.duckula.controller.dao.CommonDeployMapper;
import net.wicp.tams.app.duckula.controller.dao.CommonDumpMapper;
import net.wicp.tams.app.duckula.controller.dao.CommonInstanceMapper;
import net.wicp.tams.app.duckula.controller.dao.CommonMiddlewareMapper;
import net.wicp.tams.app.duckula.controller.dao.CommonTaskMapper;
import net.wicp.tams.common.Result;
import net.wicp.tams.common.apiext.StringUtil;
import net.wicp.tams.common.exception.ExceptAll;
import net.wicp.tams.common.exception.ProjectException;
import net.wicp.tams.common.os.SSHAssit;
import net.wicp.tams.common.os.constant.CommandCentOs;
import net.wicp.tams.common.os.pool.SSHConnection;

@Service("host")
@Slf4j
public class DeployHost implements IDeploy {

	@Autowired
	private CommonTaskMapper commonTaskMapper;
	@Autowired
	private CommonConsumerMapper commonConsumerMapper;
	@Autowired
	private CommonDumpMapper commonDumpMapper;
	@Autowired
	private CommonMiddlewareMapper commonMiddlewareMapper;
	@Autowired
	private CommonInstanceMapper commonInstanceMapper;
	@Autowired
	private CommonCheckpointMapper commonCheckpointMapper;

	@Autowired
	private CommonDeployMapper commonDeployMapper;

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
			CommonDeploy commonDeploy = commonDeployMapper.selectById(deployid);
			SSHConnection conn = null;
			try {
				conn = SSHAssit.getConn(commonDeploy.getHost(), commonDeploy.getPort(), "duckula",
						commonDeploy.getPwdDuckula(), 0);
				Result result = SSHAssit.callCommand(conn, CommandCentOs.ls, configName + ".properties",
						"/data/duckula-data/conf");
				// message示例: -rwxr--r--. 1 duckula duckula 1595 2020-11-15 22:14:25
				// ct-rjzjh-jdbc.properties
				// 考虑中英文的问题
				if ("没有异常".equals(result.getMessage()) || "No Exception".equals(result.getMessage())
						|| StringUtil.isNull(result.getMessage())) {
					return "error:没有配置信息";
				} else {
					return result.getMessage();
				}
			} catch (ProjectException e) {
				log.error("连接服务器失败", e);
				return "error:连接服务器失败：" + e.getMessage();
			} finally {
				if (conn != null) {
					conn.close();
				}
			}
		} catch (Throwable e) {
			return "error:查找异常:" + e.getMessage();
		}
	}
	

	@Override
	public Result addConfig(Long deployid, CommandType commandType, Long taskId) {
		Map<String, Object> params = new HashMap<String, Object>();
		String configName = BusiTools.configContext(commonConsumerMapper,commonTaskMapper, commonCheckpointMapper, commonDumpMapper,
				commonMiddlewareMapper, commonInstanceMapper, commandType, taskId, params);

		// 产生相关文件
		CommonDeploy commonDeploy = commonDeployMapper.selectById(deployid);
		String configmapStr = DeployType.formateConfig(DeployType.valueOf(commonDeploy.getDeploy()), params);
		SSHConnection conn = null;
		try {
			conn = SSHAssit.getConn(commonDeploy.getHost(), commonDeploy.getPort(), "duckula",
					commonDeploy.getPwdDuckula(), 0);
			Result scp = conn.scp(configmapStr.getBytes(), configName + ".properties", "/data/duckula-data/conf",
					"0744");
			return scp;
		} catch (ProjectException e) {
			log.error("连接服务器失败", e);
			return Result.getError("连接服务器失败：" + e.getMessage());
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	@Override
	public String viewConf(Long deployid, CommandType commandType, Long taskId) {
		Map<String, Object> params = new HashMap<String, Object>();
		String configName = BusiTools.configContext(commonConsumerMapper,commonTaskMapper, commonCheckpointMapper, commonDumpMapper,
				commonMiddlewareMapper, commonInstanceMapper, commandType, taskId, params);

		// 产生相关文件
		CommonDeploy commonDeploy = commonDeployMapper.selectById(deployid);
		String configmapStr = DeployType.formateConfig(DeployType.valueOf(commonDeploy.getDeploy()), params);
		return configmapStr;
	}

	@Override
	public Result deleteConfig(Long deployid, CommandType commandType, Long taskId) {
		String configName = null;
		switch (commandType) {
		case task:
			CommonTask selectTask = commonTaskMapper.selectById(taskId);
			configName = commandType.formateConfigName(selectTask.getName());
			break;
		case consumer:
			CommonConsumer commonConsumer = commonConsumerMapper.selectById(taskId);
			configName = commandType.formateConfigName(commonConsumer.getName());
			break;
		default:
			break;
		}
		CommonDeploy commonDeploy = commonDeployMapper.selectById(deployid);
		SSHConnection conn = null;
		try {
			conn = SSHAssit.getConn(commonDeploy.getHost(), commonDeploy.getPort(), "duckula",
					commonDeploy.getPwdDuckula(), 0);
			Result executeCommand = conn.executeCommand("rm -rf " + configName + ".properties");
			return executeCommand;
		} catch (ProjectException e) {
			log.error("连接服务器失败", e);
			return Result.getError("连接服务器失败：" + e.getMessage());
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public Result start(Long deployid, CommandType taskType, Long taskId, boolean isDebug) {
		if (!checkExit(deployid, taskType, taskId).isSuc()) {
			addConfig(deployid, taskType, taskId);
		}
		String configName = null;

		CommonDeploy commonDeploy = commonDeployMapper.selectById(deployid);
		SSHConnection conn = null;
		try {
			conn = SSHAssit.getConn(commonDeploy.getHost(), commonDeploy.getPort(), "duckula",
					commonDeploy.getPwdDuckula(), 0);
			switch (taskType) {
			case task:
				CommonTask selectTask = commonTaskMapper.selectById(taskId);
				configName = taskType.formateConfigName(selectTask.getName());// 使用的配置
				break;
			case consumer:
				CommonConsumer commonConsumer = commonConsumerMapper.selectById(taskId);
				configName = taskType.formateConfigName(commonConsumer.getName());// 使用的配置
				break;
			case dump:
				CommonDump commonDump = commonDumpMapper.selectById(taskId);
				configName = taskType.formateConfigName(commonDump.getName());// 使用的配置
				break;
			default:
				return Result.getError("还未实现的布署类型");
			}
			int jmxPort = StringUtil.buildPort(configName);
			Result startTask = conn.executeCommand(
					String.format("sh $DUCKULA3_HOME/bin/%s %s %s", taskType.getHostShellFile(), configName, jmxPort));
			if (startTask != null) {
				return Result.getSuc();
			} else {
				return Result.getError("布署失败");
			}
		} catch (ProjectException e) {
			log.error("连接服务器失败", e);
			return Result.getError("连接服务器失败：" + e.getMessage());
		} finally {
			if (conn != null) {
				conn.close();
			}
		}

	}

	@Override
	public Result stop(Long deployid, CommandType taskType, Long taskId) {
		String configName = null;
		String containsStr=null;
		switch (taskType) {
		case task:
			CommonTask selectTask = commonTaskMapper.selectById(taskId);
			configName = taskType.formateConfigName(selectTask.getName());
			containsStr="/opt/duckula/duckula-task.jar";
			break;
		case consumer:
			CommonConsumer commonConsumer = commonConsumerMapper.selectById(taskId);
			configName = taskType.formateConfigName(commonConsumer.getName());
			containsStr="/opt/duckula/duckula-consumer.jar";
			break;
		case dump:
		default:
			break;
		}
		CommonDeploy commonDeploy = commonDeployMapper.selectById(deployid);
		SSHConnection conn = null;
		try {
			conn = SSHAssit.getConn(commonDeploy.getHost(), commonDeploy.getPort(), "duckula",
					commonDeploy.getPwdDuckula(), 0);
			Result executeCommand = conn.executeCommand("ps -ax|grep " + configName + "/gc.log");// 加/gc.log为了区分一个id是另一个id的前缀
			if (executeCommand.isSuc()) {
				if (executeCommand.getMessage().contains(containsStr)) {// 还在运行
					String[] ress = executeCommand.getMessage().split(" ");
					Result result = conn.executeCommand("kill -15 " + ress[1]);
					return result;
				}
			} else {
			}
		} catch (ProjectException e) {
			log.error("执行失败", e);
			return Result.getError("执行失败：" + e.getMessage());
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
		return Result.getError("不需要停止");
	}

	@Override
	public BufferedReader viewLog(Long deployid, CommandType taskType, Long taskId) {
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
		default:
			break;
		}
		CommonDeploy commonDeploy = commonDeployMapper.selectById(deployid);
		SSHConnection conn = null;
		Session session = null;
		try {
			conn = SSHAssit.getConn(commonDeploy.getHost(), commonDeploy.getPort(), "duckula",
					commonDeploy.getPwdDuckula(), 300000);// 0表示不超时 ，使用5分钟，怕链接泄露
			session = conn.getConn().openSession();
			String cmdStr = String.format("tail -100f $DUCKULA3_DATA/logs/%s/%s/other.log",taskType.name(),configName);
			session.execCommand(cmdStr);
			InputStream stdout = session.getStdout();
			BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
			return reader;
		} catch (ProjectException e) {
			log.error("连接服务器失败", e);
		} catch (Exception e) {
			log.error("执行tail命令失败", e);
		}
		if (session != null) {
			session.close();
		}
		if (conn != null) {
			conn.close();
		}
		return null;
	}

	@Override
	public String queryStatus(Long deployid, CommandType taskType, Long taskId) {
		String configName = null;
		String containsStr=null;
		switch (taskType) {
		case task:
			CommonTask selectTask = commonTaskMapper.selectById(taskId);
			configName = taskType.formateConfigName(selectTask.getName());
			containsStr="/opt/duckula/duckula-task.jar";
			break;
		case consumer:
			CommonConsumer commonConsumer = commonConsumerMapper.selectById(taskId);
			configName = taskType.formateConfigName(commonConsumer.getName());
			containsStr="/opt/duckula/duckula-consumer.jar";
			break;
		case dump:
		default:
			break;
		}

		CommonDeploy commonDeploy = commonDeployMapper.selectById(deployid);
		SSHConnection conn = null;
		String status = null;
		try {
			conn = SSHAssit.getConn(commonDeploy.getHost(), commonDeploy.getPort(), "duckula",
					commonDeploy.getPwdDuckula(), 0);
			Result executeCommand = conn.executeCommand("ps -ax|grep " + configName + "/gc.log");// 加/gc.log为了区分一个id是另一个id的前缀
			if (executeCommand.isSuc()) {
				if (executeCommand.getMessage().contains(containsStr)) {// 还在运行
					status = DeployType.host.getStatus("running");
				} else {
					status = DeployType.host.getStatus(null);
				}
			} else {
				status = DeployType.host.getStatus(null);
			}
		} catch (ProjectException e) {
			log.error("连接服务器失败", e);
			if(e.getExcept()==ExceptAll.project_timeout) {
				status = DeployType.host.getStatus("timeout");
			}else {
				status = DeployType.host.getStatus(e.getExcept().getDesc());
			}
			
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
		return status;
	}



	

}
