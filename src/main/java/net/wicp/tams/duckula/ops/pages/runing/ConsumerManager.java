package net.wicp.tams.duckula.ops.pages.runing;

import java.util.Map;

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
import net.wicp.tams.app.duckula.controller.bean.models.CommonConsumer;
import net.wicp.tams.app.duckula.controller.bean.models.CommonTask;
import net.wicp.tams.app.duckula.controller.config.constant.CommandType;
import net.wicp.tams.app.duckula.controller.dao.CommonCheckpointMapper;
import net.wicp.tams.app.duckula.controller.dao.CommonConsumerMapper;
import net.wicp.tams.app.duckula.controller.dao.CommonDeployMapper;
import net.wicp.tams.app.duckula.controller.dao.CommonInstanceMapper;
import net.wicp.tams.app.duckula.controller.dao.CommonMiddlewareMapper;
import net.wicp.tams.app.duckula.controller.dao.CommonVersionMapper;
import net.wicp.tams.app.duckula.controller.service.DeployService;
import net.wicp.tams.app.duckula.controller.service.PosService;
import net.wicp.tams.common.Result;
import net.wicp.tams.common.apiext.CollectionUtil;
import net.wicp.tams.common.apiext.StringUtil;
import net.wicp.tams.common.apiext.json.EasyUiAssist;
import net.wicp.tams.common.binlog.alone.binlog.bean.RuleManager;
import net.wicp.tams.common.callback.IConvertValue;
import net.wicp.tams.component.annotation.HtmlJs;
import net.wicp.tams.component.constant.EasyUIAdd;
import net.wicp.tams.component.services.IReq;
import net.wicp.tams.component.tools.TapestryAssist;
import net.wicp.tams.duckula.ops.WebTools;
@Slf4j
@HtmlJs(easyuiadd = { EasyUIAdd.edatagrid })
public class ConsumerManager {
	@Inject
	protected RequestGlobals requestGlobals;
	@Inject
	protected Request request;
	@Inject
	private IReq req;
	@Inject
	private CommonConsumerMapper commonConsumerMapper;
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
		final CommonConsumer commonCheckpoint = TapestryAssist.getBeanFromPage(CommonConsumer.class, requestGlobals);
		QueryWrapper<CommonConsumer> queryWrapper = new QueryWrapper<CommonConsumer>();
		if (StringUtil.isNotNull(commonCheckpoint.getName())) {
			queryWrapper.likeRight("name", commonCheckpoint.getName());
		}
		Page<CommonConsumer> selectPage = commonConsumerMapper.selectPage(WebTools.buildPage(request), queryWrapper);
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

		// 状态
		IConvertValue<Object> statusConvert = new IConvertValue<Object>() {
			@Override
			public String getStr(Object object) {
				CommonConsumer commonConsumer = (CommonConsumer) object;
				return deployService.queryStatus(CommandType.consumer, commonConsumer.getId(),
						commonConsumer.getDeployId());
			}
		};

		IConvertValue<String> configNameConvert = new IConvertValue<String>() {
			@Override
			public String getStr(String keyObj) {
				return CommandType.consumer.formateTaskName(keyObj);
			}
		};
		String retstr = EasyUiAssist.getJsonForGridAlias2(selectPage.getRecords(),
				new String[] { "versionId,version1", "deployId,deployId1", "middlewareId,middlewareId1", ",taskStatus",
						"name,configName" },
				CollectionUtil.newMap("version1", versionConvert, "deployId1", deployConvert, "middlewareId1",
						middlewareConvert, "taskStatus", statusConvert, "configName", configNameConvert),
				selectPage.getTotal());
		return TapestryAssist.getTextStreamResponse(retstr);
	}

	public TextStreamResponse onSave() {
		final CommonConsumer commonCheckpoint = TapestryAssist.getBeanFromPage(CommonConsumer.class, requestGlobals);
		if (commonCheckpoint.getId() == null) {
			commonConsumerMapper.insert(commonCheckpoint);
		} else {
			commonConsumerMapper.updateByPrimaryKeySelective(commonCheckpoint);
		}
		return TapestryAssist.getTextStreamResponse(Result.getSuc());
	}

	public TextStreamResponse onDel() {
		String id = request.getParameter("id");
		commonConsumerMapper.deleteById(id);
		return TapestryAssist.getTextStreamResponse(Result.getSuc());
	}





	public TextStreamResponse onViewlog() {
		final CommonConsumer commonTask = TapestryAssist.getBeanFromPage(CommonConsumer.class, requestGlobals);
		deployService.viewLog(CommandType.consumer, commonTask.getId(), commonTask.getDeployId());
		return TapestryAssist.getTextStreamResponse(Result.getSuc());
	}

	/**
	 * 开启任务
	 * 
	 * @return
	 */
	public TextStreamResponse onStartTask() {
		final CommonConsumer commonTask = TapestryAssist.getBeanFromPage(CommonConsumer.class, requestGlobals);
		Result startTask = deployService.startTask(CommandType.consumer, commonTask.getId(), commonTask.getDeployId(),
				false);
		return TapestryAssist.getTextStreamResponse(startTask);
	}
	
	// 布署配置文件
		public TextStreamResponse onAddConfig() {
			final CommonConsumer commonConsumer = TapestryAssist.getBeanFromPage(CommonConsumer.class, requestGlobals);
			Result startDump = deployService.addConfig(CommandType.consumer, commonConsumer.getId(), commonConsumer.getDeployId());
			return TapestryAssist.getTextStreamResponse(startDump);
		}

	/// 停止任务,会等3分钟。
	public TextStreamResponse onStopTask() {
		final CommonConsumer commonTask = TapestryAssist.getBeanFromPage(CommonConsumer.class, requestGlobals);
		Result stopTask = deployService.stopTask(CommandType.consumer, commonTask.getId(), commonTask.getDeployId());
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
			String queryStatus = deployService.queryStatus(CommandType.consumer, commonTask.getId(),
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