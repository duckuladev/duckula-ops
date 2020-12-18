package net.wicp.tams.app.duckula.controller.service.deploy;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

import net.wicp.tams.app.duckula.controller.bean.models.CommonCheckpoint;
import net.wicp.tams.app.duckula.controller.bean.models.CommonDump;
import net.wicp.tams.app.duckula.controller.bean.models.CommonInstance;
import net.wicp.tams.app.duckula.controller.bean.models.CommonMiddleware;
import net.wicp.tams.app.duckula.controller.bean.models.CommonTask;
import net.wicp.tams.app.duckula.controller.config.constant.CommandType;
import net.wicp.tams.app.duckula.controller.config.constant.MiddlewareType;
import net.wicp.tams.common.Result;

public interface IDeploy {
	/***
	 * 相关配置是否存在
	 * 
	 * @param deployid
	 * @param taskType
	 * @param taskId
	 * @return
	 */
	public Result checkExit(Long deployid, CommandType taskType, Long taskId);

	/***
	 * 添加配置文件
	 * 
	 * @param deployid
	 * @param taskType
	 * @param taskId
	 * @return
	 */
	public Result addConfig(Long deployid, CommandType taskType, Long taskId);

	public Result deleteConfig(Long deployid, CommandType commandType, Long taskId);

	public Result start(Long deployid, CommandType taskType, Long taskId, boolean isDebug);

	public Result stop(Long deployid, CommandType taskType, Long taskId);

	public BufferedReader viewLog(Long deployid, CommandType taskType, Long taskId);

	//查看现在的配置
	public String viewConf(Long deployid, CommandType commandType, Long taskId);
	//查看已布署的配置
	public String viewConfDeploy(Long deployid, CommandType commandType, Long taskId);

	// 查询任务的相关状态
	public String queryStatus(Long deployid, CommandType taskType, Long taskId);

}
