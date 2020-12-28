package net.wicp.tams.duckula.ops.pages.runing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.util.TextStreamResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;
import net.wicp.tams.app.duckula.controller.BusiTools;
import net.wicp.tams.app.duckula.controller.bean.models.CommonCheckpoint;
import net.wicp.tams.app.duckula.controller.bean.models.CommonTask;
import net.wicp.tams.app.duckula.controller.config.constant.CommandType;
import net.wicp.tams.app.duckula.controller.dao.CommonCheckpointMapper;
import net.wicp.tams.app.duckula.controller.dao.CommonDeployMapper;
import net.wicp.tams.app.duckula.controller.dao.CommonInstanceMapper;
import net.wicp.tams.app.duckula.controller.dao.CommonMiddlewareMapper;
import net.wicp.tams.app.duckula.controller.dao.CommonTaskMapper;
import net.wicp.tams.app.duckula.controller.dao.CommonVersionMapper;
import net.wicp.tams.app.duckula.controller.service.DeployService;
import net.wicp.tams.app.duckula.controller.service.PosService;
import net.wicp.tams.common.Result;
import net.wicp.tams.common.apiext.CollectionUtil;
import net.wicp.tams.common.apiext.StringUtil;
import net.wicp.tams.common.apiext.json.EasyUiAssist;
import net.wicp.tams.common.binlog.alone.ListenerConf.Position;
import net.wicp.tams.common.binlog.alone.binlog.bean.RuleManager;
import net.wicp.tams.common.callback.IConvertValue;
import net.wicp.tams.component.annotation.HtmlJs;
import net.wicp.tams.component.constant.EasyUIAdd;
import net.wicp.tams.component.services.IReq;
import net.wicp.tams.component.tools.TapestryAssist;
import net.wicp.tams.duckula.ops.WebTools;

@Slf4j
@HtmlJs(easyuiadd = { EasyUIAdd.edatagrid })
public class TaskManager {
	@Inject
	protected RequestGlobals requestGlobals;
	@Inject
	protected Request request;
	@Inject
	private IReq req;
	@Inject
	private CommonTaskMapper commonTaskMapper;
	@Inject
	private CommonVersionMapper commonVersionMapper;
	@Inject
	private CommonDeployMapper commonDeployMapper;
	@Inject
	private CommonMiddlewareMapper commonMiddlewareMapper;
	@Inject
	private CommonInstanceMapper commonInstanceMapper;
	@Inject
	private CommonCheckpointMapper commonCheckpointMapper;
	@Inject
	private DeployService deployService;
	@Inject
	private PosService posService;

	public TextStreamResponse onQuery() {
		// ajax.req(key, params);
		final CommonTask commonCheckpoint = TapestryAssist.getBeanFromPage(CommonTask.class, requestGlobals);
		QueryWrapper<CommonTask> queryWrapper = new QueryWrapper<CommonTask>();
		if (StringUtil.isNotNull(commonCheckpoint.getName())) {
			queryWrapper.likeRight("name", commonCheckpoint.getName());
		}
		Page<CommonTask> selectPage = commonTaskMapper.selectPage(WebTools.buildPage(request), queryWrapper);
		IConvertValue<String> versionConvert = new IConvertValue<String>() {
			private Map<Long, String> datamap = BusiTools.convertValues(selectPage.getRecords(), commonVersionMapper,
					"versionId", "mainVersion");

			@Override
			public String getStr(String keyObj) {
				return StringUtil.isNull(keyObj) ? "" : datamap.get(Long.parseLong(keyObj));
			}
		};

		IConvertValue<String> deployConvert = new IConvertValue<String>() {
			private Map<Long, String> datamap = BusiTools.convertValues(selectPage.getRecords(), commonDeployMapper,
					"deployId", "name", "deploy");

			@Override
			public String getStr(String keyObj) {
				return StringUtil.isNull(keyObj) ? "" : datamap.get(Long.parseLong(keyObj));
			}
		};
		IConvertValue<String> middlewareConvert = new IConvertValue<String>() {
			private Map<Long, String> datamap = BusiTools.convertValues(selectPage.getRecords(), commonMiddlewareMapper,
					"middlewareId", "name", "middlewareType");

			@Override
			public String getStr(String keyObj) {
				return StringUtil.isNull(keyObj) ? "" : datamap.get(Long.parseLong(keyObj));
			}
		};

		IConvertValue<String> instanceConvert = new IConvertValue<String>() {
			private Map<Long, String> datamap = BusiTools.convertValues(selectPage.getRecords(), commonInstanceMapper,
					"instanceId", "name", "host");

			@Override
			public String getStr(String keyObj) {
				return StringUtil.isNull(keyObj) ? "" : datamap.get(Long.parseLong(keyObj));
			}
		};

		// 查询检查点信息
		Set<String> ids = CollectionUtil.getColSetFromObj(selectPage.getRecords(), "checkpointId");
		List<CommonCheckpoint> listCheckpoint = commonCheckpointMapper.selectBatchIds(ids);
		Map<Long, CommonCheckpoint> mapCheckpoint = new HashMap<Long, CommonCheckpoint>();
		for (CommonCheckpoint checkpoint : listCheckpoint) {
			mapCheckpoint.put(checkpoint.getId(), checkpoint);
		}
		IConvertValue<String> checkpointConvert = new IConvertValue<String>() {
			@Override
			public String getStr(String keyObj) {
				if (StringUtil.isNull(keyObj)) {
					return "";
				}
				CommonCheckpoint tempobj = mapCheckpoint.get(Long.parseLong(keyObj));
				return StringUtil.isNull(keyObj) ? ""
						: String.format("%s【%s】", tempobj.getName(), tempobj.getCheckpointType());
			}
		};
		// 状态
		IConvertValue<Object> statusConvert = new IConvertValue<Object>() {
			@Override
			public String getStr(Object object) {
				CommonTask commonTask = (CommonTask) object;
				return deployService.queryStatus(CommandType.task, commonTask.getId(), commonTask.getDeployId());
			}
		};
		// 位点
		IConvertValue<Object> posConvert = new IConvertValue<Object>() {
			@Override
			public String getStr(Object object) {
				CommonTask commonTask = (CommonTask) object;
				CommonCheckpoint checkpoint = mapCheckpoint.get(commonTask.getCheckpointId());
				Position position = posService.selectPosition(checkpoint, commonTask.getName(),
						commonTask.getClientId());
				return position == null ? "" : position.getTimeStr();
			}
		};
		IConvertValue<String> configNameConvert = new IConvertValue<String>() {
			@Override
			public String getStr(String keyObj) {
				return CommandType.task.formateTaskName(keyObj);
			}
		};
		String retstr = EasyUiAssist.getJsonForGridAlias2(selectPage.getRecords(),
				new String[] { "versionId,version1", "deployId,deployId1", "middlewareId,middlewareId1",
						"instanceId,instanceId1", "checkpointId,checkpoint1", ",taskStatus", ",pos",
						"name,configName" },
				CollectionUtil.newMap("version1", versionConvert, "deployId1", deployConvert, "middlewareId1",
						middlewareConvert, "instanceId1", instanceConvert, "checkpoint1", checkpointConvert,
						"taskStatus", statusConvert, "pos", posConvert, "configName", configNameConvert),
				selectPage.getTotal());
		return TapestryAssist.getTextStreamResponse(retstr);
	}

	public TextStreamResponse onSave() {
		final CommonTask commonCheckpoint = TapestryAssist.getBeanFromPage(CommonTask.class, requestGlobals);
		if (commonCheckpoint.getId() == null) {
			commonTaskMapper.insert(commonCheckpoint);
		} else {
			commonTaskMapper.updateByPrimaryKeySelective(commonCheckpoint);
		}
		return TapestryAssist.getTextStreamResponse(Result.getSuc());
	}

	public TextStreamResponse onDel() {
		String id = request.getParameter("id");
		commonTaskMapper.deleteById(id);
		return TapestryAssist.getTextStreamResponse(Result.getSuc());
	}

	public TextStreamResponse onDataConvert() {
		String saveDataStr = request.getParameter("saveData");
		JSONObject dgAll = JSONObject.parseObject(saveDataStr);
		com.alibaba.fastjson.JSONArray rows = dgAll.getJSONArray("rows");
		RuleManager ruleManager = new RuleManager(rows);
		return TapestryAssist.getTextStreamResponse(Result.getSuc(ruleManager.toString()));
	}

	public TextStreamResponse onRuleData() {
		String commandtypeStr = request.getParameter("ruleData");
		try {
			RuleManager ruleManager = new RuleManager(commandtypeStr);
			JSONArray retAry = ruleManager.toJsonAry();
			return TapestryAssist.getTextStreamResponse(retAry.toJSONString());
		} catch (Exception e) {// 异常需要清楚grid
			log.error("rule转换出错",e);
			return TapestryAssist.getTextStreamResponse(new JSONArray().toJSONString());
		}
	}

	public TextStreamResponse onViewlog() {
		final CommonTask commonTask = TapestryAssist.getBeanFromPage(CommonTask.class, requestGlobals);
		deployService.viewLog(CommandType.task, commonTask.getId(), commonTask.getDeployId());
		return TapestryAssist.getTextStreamResponse(Result.getSuc());
	}

	/**
	 * 开启任务
	 * 
	 * @return
	 */
	public TextStreamResponse onStartTask() {
		final CommonTask commonTask = TapestryAssist.getBeanFromPage(CommonTask.class, requestGlobals);
		Result startTask = deployService.startTask(CommandType.task, commonTask.getId(), commonTask.getDeployId(),
				false);
		return TapestryAssist.getTextStreamResponse(startTask);
	}

	// 布署配置文件
	public TextStreamResponse onAddConfig() {
		final CommonTask commonTask = TapestryAssist.getBeanFromPage(CommonTask.class, requestGlobals);
		Result startTask = deployService.addConfig(CommandType.task, commonTask.getId(), commonTask.getDeployId());
		return TapestryAssist.getTextStreamResponse(startTask);
	}

	/// 停止任务,会等3分钟。
	public TextStreamResponse onStopTask() {
		final CommonTask commonTask = TapestryAssist.getBeanFromPage(CommonTask.class, requestGlobals);
		Result stopTask = deployService.stopTask(CommandType.task, commonTask.getId(), commonTask.getDeployId());
		long maxWaitTime = 180000;// 最长等10S
		long curTime = System.currentTimeMillis();
		while (true) {
			if (System.currentTimeMillis() - curTime > maxWaitTime) {
				break;
			}
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			}
			String queryStatus = deployService.queryStatus(CommandType.task, commonTask.getId(),
					commonTask.getDeployId());
			System.out.println("=========stoptasking============" + queryStatus);
			if (queryStatus.contains("未布署")) {
				break;
			} else {
				continue;
			}
		}
		return TapestryAssist.getTextStreamResponse(stopTask);
	}

}