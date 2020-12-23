package net.wicp.tams.app.duckula.controller.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.aspectj.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.wicp.tams.app.duckula.controller.BusiTools;
import net.wicp.tams.app.duckula.controller.bean.models.CommonDeploy;
import net.wicp.tams.app.duckula.controller.bean.models.CommonVersion;
import net.wicp.tams.app.duckula.controller.config.constant.CommandType;
import net.wicp.tams.app.duckula.controller.config.constant.DeployType;
import net.wicp.tams.app.duckula.controller.dao.CommonDeployMapper;
import net.wicp.tams.app.duckula.controller.dao.CommonVersionMapper;
import net.wicp.tams.app.duckula.controller.service.deploy.IDeploy;
import net.wicp.tams.common.Result;
import net.wicp.tams.common.apiext.IOUtil;
import net.wicp.tams.common.apiext.StringUtil;
import net.wicp.tams.common.beans.Host;
import net.wicp.tams.common.constant.PathType;
import net.wicp.tams.common.constant.dic.YesOrNo;
import net.wicp.tams.common.exception.ProjectException;
import net.wicp.tams.common.os.SSHAssit;
import net.wicp.tams.common.os.pool.SSHConnection;
import net.wicp.tams.common.spring.autoconfig.SpringAssit;

@Service
@Slf4j
public class DeployService {
	@Autowired
	private CommonDeployMapper commonDeployMapper;
	@Autowired
	private CommonVersionMapper commonVersionMapper;

	/***
	 * 启动监听任务
	 * 
	 * @param commonTask
	 * @param isdebug
	 * @return
	 */
	public Result startTask(CommandType commandType, Long taskId, Long deployId, boolean isdebug) {
		CommonDeploy commonDeploy = commonDeployMapper.selectById(deployId);
		if (commonDeploy == null) {
			return Result.getError("部署环境没有配置");
		}
		IDeploy deploy = (IDeploy) SpringAssit.context.getBean(commonDeploy.getDeploy());
		try {
			deploy.start(commonDeploy.getId(), commandType, taskId, isdebug);
		} catch (Throwable e) {
			log.error("host 开始任务失败", e);
			return Result.getError(e.getMessage());
		}
		Result ret = Result.getSuc("布署成功");
		return ret;
	}

	public Result addConfig(CommandType commandType, Long taskId, Long deployId) {
		CommonDeploy commonDeploy = commonDeployMapper.selectById(deployId);
		if (commonDeploy == null) {
			return Result.getError("部署环境没有配置");
		}
		IDeploy deploy = (IDeploy) SpringAssit.context.getBean(commonDeploy.getDeploy());
		try {
			Result addConfig = deploy.addConfig(commonDeploy.getId(), commandType, taskId);
			return addConfig;
		} catch (Throwable e) {
			log.error("布署配置文件失败", e);
			return Result.getError(e.getMessage());
		}
	}

	public Result stopTask(CommandType commandType, Long taskId, Long deployId) {
		CommonDeploy commonDeploy = commonDeployMapper.selectById(deployId);
		if (commonDeploy == null) {
			return Result.getError("部署环境没有配置");
		}
		IDeploy deploy = (IDeploy) SpringAssit.context.getBean(commonDeploy.getDeploy());
		try {
			deploy.stop(commonDeploy.getId(), commandType, taskId);
			return Result.getSuc("删除成功");
		} catch (Throwable e) {
			return Result.getError(e.getMessage());
		}
	}

	public BufferedReader viewLog(CommandType commandType, Long taskId, Long deployId) {
		CommonDeploy commonDeploy = commonDeployMapper.selectById(deployId);
		if (commonDeploy == null) {
			return null;
		}
		IDeploy deploy = (IDeploy) SpringAssit.context.getBean(commonDeploy.getDeploy());
		try {
			BufferedReader viewLog = deploy.viewLog(commonDeploy.getId(), commandType, taskId);
			return viewLog;
		} catch (Throwable e) {
			log.error("查看日志错误", e);
			return null;
		}
	}

	public String viewConf(CommandType commandType, Long taskId, Long deployId) {
		CommonDeploy commonDeploy = commonDeployMapper.selectById(deployId);
		if (commonDeploy == null) {
			return null;
		}
		IDeploy deploy = (IDeploy) SpringAssit.context.getBean(commonDeploy.getDeploy());
		try {
			String viewConf = deploy.viewConf(commonDeploy.getId(), commandType, taskId);
			return viewConf;
		} catch (Throwable e) {
			log.error("查看日志错误", e);
			return null;
		}
	}

	public String viewConfDeploy(CommandType commandType, Long taskId, Long deployId) {
		CommonDeploy commonDeploy = commonDeployMapper.selectById(deployId);
		if (commonDeploy == null) {
			return null;
		}
		IDeploy deploy = (IDeploy) SpringAssit.context.getBean(commonDeploy.getDeploy());
		try {
			String viewConf = deploy.viewConfDeploy(commonDeploy.getId(), commandType, taskId);
			return viewConf;
		} catch (Throwable e) {
			log.error("查看日志错误", e);
			return null;
		}
	}

	////// TODO 查询状态
	public String queryStatus(CommandType commandType, Long taskId, Long deployId) {
		CommonDeploy commonDeploy = commonDeployMapper.selectById(deployId);
		IDeploy deploy = (IDeploy) SpringAssit.context.getBean(commonDeploy.getDeploy());
		try {
			String queryStatus = deploy.queryStatus(commonDeploy.getId(), commandType, taskId);
			return queryStatus;
		} catch (Exception e) {
			return null;
		}
	}

	public Result initHost(CommonDeploy commonDeploy, String pwd) {
		DeployType deployType = DeployType.valueOf(commonDeploy.getDeploy());
		if (deployType == DeployType.k8s) {
			return Result.getError("k8s类型不需要初始化");
		}
		// 1、登陆
		SSHConnection conn = null;
		try {
			conn = SSHAssit.getConn(commonDeploy.getHost(), commonDeploy.getPort(), "root", pwd, 0);
		} catch (ProjectException e) {
			log.error("连接服务器失败", e);
			return Result.getError("连接服务器失败：" + e.getMessage());
		}
		// 3、检查是否支持docker
		YesOrNo checkDocker = conn.checkDocker();
		if (checkDocker != null && checkDocker == YesOrNo.yes && deployType == DeployType.host) {
			commonDeploy.setDeploy(DeployType.docker.name());// 强制设置为docker
			commonDeployMapper.updateByPrimaryKey(commonDeploy);
		}
		deployType = DeployType.valueOf(commonDeploy.getDeploy());
		// 4、复制配置信息等 (由运行程序来完成)
		// 5、初始化logs目录（TODO）
		// 6、复制程序(TODO)
		InputStream fileToInputStream = IOUtil.fileToInputStream("/deploy/bin/duckula-init.sh", DeployService.class);
		byte[] slurpbyte = null;
		try {
			slurpbyte = IOUtil.slurpbyte(fileToInputStream);
		} catch (IOException e) {
			log.error("获取duckula-init.sh错误", e);
			return Result.getError("获取duckula-init.sh错误");
		}
		conn.scp(slurpbyte, "duckula-init.sh", "~", "0744");
		// 7、执行shell脚本
		Result executeCommand = null;
		String hosts = "";// 主机，如果是host，没有权限配置/etc/hosts，
		if (deployType == DeployType.host && StringUtil.isNotNull(commonDeploy.getHostsconfig())) {
			hosts = Host.getHostStr(Host.jsonToHosts(commonDeploy.getHostsconfig()));
		}
		if (StringUtil.isNotNull(hosts)) {
			executeCommand = conn.executeCommand(
					String.format("sh ~/duckula-init.sh %s %s \"%s\"", "duckula", commonDeploy.getPwdDuckula(), hosts));
		} else {
			// 如果是docker默认6秒太少了，12秒
			executeCommand = conn.executeCommand(
					String.format("sh ~/duckula-init.sh %s %s", "duckula", commonDeploy.getPwdDuckula()), 12000);
		}
		// 8、如果是docker且需要执行一个login
		if (executeCommand.isSuc() && deployType == DeployType.docker
				&& StringUtil.isNotNull(commonDeploy.getDockerLogin())) {
			executeCommand = conn.executeCommand(commonDeploy.getDockerLogin());
		}
		conn.close();
		if (executeCommand.isSuc()&&commonDeploy.getVersionId()!=null) {
			// 同时升级版本
			//CommonVersion commonVersion = commonVersionMapper.selectByMaxKey();
			CommonVersion setversion = commonVersionMapper.selectById(commonDeploy.getVersionId());
			commonDeploy.setVersionId(null);// 这是初始化升级，旧版本要设置为null才能升级，否则会被挡住
			Result upgradeVersion = upgradeVersion(commonDeploy, setversion);
			if(upgradeVersion.isSuc()) {
				commonDeploy.setVersionId(setversion.getId());	
			}
			return upgradeVersion;
		}
		return executeCommand;
	}

	public Result upgradeVersion(CommonDeploy commonDeploy, CommonVersion commonVersionNew) {
		DeployType deployType = DeployType.valueOf(commonDeploy.getDeploy());
		if (deployType == DeployType.k8s) {
			return Result.getError("k8s类型不需要升级版本");
		}
		CommonVersion commonVersionOld = commonVersionMapper.selectById(commonDeploy.getVersionId());
		if (commonVersionOld != null) {
			String[] newVersion = commonVersionNew.getMainVersion().split("\\.");
			String[] oldVersion = commonVersionOld.getMainVersion().split("\\.");
			if (Integer.parseInt(oldVersion[0]) > Integer.parseInt(newVersion[0])
					|| Integer.parseInt(oldVersion[1]) > Integer.parseInt(newVersion[1])
					|| Integer.parseInt(oldVersion[2]) >= Integer.parseInt(newVersion[2])) {
				return Result.getError("新版本小于当前版本，无需更新");
			}
		}
		String mainPath = null, dataPath = null;
		try {
			switch (deployType) {
			case host:
				// 更新main
				mainPath = PathType.getPath(BusiTools.packVersionUrl(commonVersionNew, true), true);
				//home:是返回整个tar文件  而s3://返回加压后的目录地址。
				mainPath=mainPath.endsWith(".tar")?mainPath:mainPath+".tar";
			case docker:
				// 更新data
				dataPath = PathType.getPath(BusiTools.packVersionUrl(commonVersionNew, false), true);
				//home:是返回整个tar文件  而s3://返回加压后的目录地址。
				dataPath=dataPath.endsWith(".tar")?dataPath:dataPath+".tar";
				break;
			default:
				break;
			}
		} catch (Throwable e) {
			return Result.getError("读取执行器文件错误：" + e.getMessage()); 
		}
		
		// 1、使用duckula登陆
		SSHConnection conn = null;
		try {
			conn = SSHAssit.getConn(commonDeploy.getHost(), commonDeploy.getPort(), "duckula",
					commonDeploy.getPwdDuckula(), 0);
		} catch (ProjectException e) {
			log.error("连接服务器失败", e);
			return Result.getError("连接服务器失败：" + e.getMessage());
		}
		// 2.复制文件
		if (StringUtil.isNotNull(mainPath)) {
			String fileName = mainPath.substring(mainPath.lastIndexOf("/") + 1);
			File file = new File(mainPath);
			if (!file.exists()) {
				return Result.getError("不存在主程序文件：" + mainPath);
			}
			conn.scp(mainPath, fileName, "~", "0744");
			// 3.转移历史
			// String version =
			// BusiTools.getVersion(dataPath,"/duckula-data/plugins/readme.text");
			if (commonVersionOld != null && StringUtil.isNotNull(commonVersionOld.getMainVersion())) {// mv 方式没有权限
				conn.executeCommand(String.format(
						"mkdir /opt/duckula-history/%s;cp -R /opt/duckula/  /opt/duckula-history/%s/; rm -rf /opt/duckula/*",
						commonVersionOld.getMainVersion(), commonVersionOld.getMainVersion()));
			}
			// 4.解压
			conn.tarX("~/" + fileName, "/opt");
		}
		if (StringUtil.isNotNull(dataPath)) {
			String fileName = dataPath.substring(dataPath.lastIndexOf("/") + 1);
			File file = new File(dataPath);
			if (!file.exists()) {
				return Result.getError("不存在数据文件：" + dataPath);
			}
			conn.scp(dataPath, fileName, "~", "0744");
			// 3.转移历史
			// String version =
			// BusiTools.getVersion(dataPath,"/duckula-data/plugins/readme.text");
			if (commonVersionOld != null && StringUtil.isNotNull(commonVersionOld.getDataVersion())) {
				conn.executeCommand("mv /data/duckula-data/plugins/  /data/duckula-data/history/"
						+ commonVersionOld.getDataVersion());
			}
			// 4.解压
			conn.tarX("~/" + fileName, "/data");
		}
		conn.close();
		commonDeploy.setVersionId(commonVersionNew.getId().longValue());
		commonDeployMapper.updateById(commonDeploy);
		return Result.getSuc();
	}

}
