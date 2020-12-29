package net.wicp.tams.duckula.ops.pages.runing;

import java.util.Map;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.util.TextStreamResponse;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import net.wicp.tams.app.duckula.controller.BusiTools;
import net.wicp.tams.app.duckula.controller.bean.models.CommonDump;
import net.wicp.tams.app.duckula.controller.config.constant.CommandType;
import net.wicp.tams.app.duckula.controller.dao.CommonCheckpointMapper;
import net.wicp.tams.app.duckula.controller.dao.CommonDeployMapper;
import net.wicp.tams.app.duckula.controller.dao.CommonDumpMapper;
import net.wicp.tams.app.duckula.controller.dao.CommonInstanceMapper;
import net.wicp.tams.app.duckula.controller.dao.CommonMiddlewareMapper;
import net.wicp.tams.app.duckula.controller.dao.CommonVersionMapper;
import net.wicp.tams.app.duckula.controller.service.DeployService;
import net.wicp.tams.app.duckula.controller.service.PosService;
import net.wicp.tams.common.Result;
import net.wicp.tams.common.apiext.CollectionUtil;
import net.wicp.tams.common.apiext.StringUtil;
import net.wicp.tams.common.apiext.json.EasyUiAssist;
import net.wicp.tams.common.callback.IConvertValue;
import net.wicp.tams.component.annotation.HtmlJs;
import net.wicp.tams.component.constant.EasyUIAdd;
import net.wicp.tams.component.services.IReq;
import net.wicp.tams.component.tools.TapestryAssist;
import net.wicp.tams.duckula.ops.WebTools;

@HtmlJs(easyuiadd = { EasyUIAdd.edatagrid })
public class DumpManager {
	@Inject
	protected RequestGlobals requestGlobals;
	@Inject
	protected Request request;
	@Inject
	private IReq req;
	@Inject
	private CommonDumpMapper commonDumpMapper;
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
		final CommonDump commonCheckpoint = TapestryAssist.getBeanFromPage(CommonDump.class, requestGlobals);
		QueryWrapper<CommonDump> queryWrapper = new QueryWrapper<CommonDump>();
		if (StringUtil.isNotNull(commonCheckpoint.getName())) {
			queryWrapper.likeRight("name", commonCheckpoint.getName());
		}
		Page<CommonDump> selectPage = commonDumpMapper.selectPage(WebTools.buildPage(request), queryWrapper);
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
		// 状态
		IConvertValue<Object> statusConvert = new IConvertValue<Object>() {
			@Override
			public String getStr(Object object) {
				CommonDump commonDump = (CommonDump) object;
				return deployService.queryStatus(CommandType.dump, commonDump.getId(), commonDump.getDeployId());
			}
		};
		IConvertValue<String> configNameConvert = new IConvertValue<String>() {
			@Override
			public String getStr(String keyObj) {
				return  CommandType.dump.formateTaskName(keyObj);
			}
		};
		String retstr = EasyUiAssist.getJsonForGridAlias2(selectPage.getRecords(),
				new String[] { "versionId,version1", "deployId,deployId1", "middlewareId,middlewareId1",
						"instanceId,instanceId1", ",taskStatus","name,configName"},
				CollectionUtil.newMap("version1", versionConvert, "deployId1", deployConvert, "middlewareId1",
						middlewareConvert, "instanceId1", instanceConvert, "taskStatus", statusConvert,"configName",configNameConvert),
				selectPage.getTotal());
		return TapestryAssist.getTextStreamResponse(retstr);
	}

	public TextStreamResponse onSave() {
		final CommonDump commonCheckpoint = TapestryAssist.getBeanFromPage(CommonDump.class, requestGlobals);
		if (commonCheckpoint.getId() == null) {
			commonDumpMapper.insert(commonCheckpoint);
		} else {
			commonDumpMapper.updateByPrimaryKeySelective(commonCheckpoint);
		}
		return TapestryAssist.getTextStreamResponse(Result.getSuc());
	}

	public TextStreamResponse onDel() {
		String id = request.getParameter("id");
		commonDumpMapper.deleteById(id);
		return TapestryAssist.getTextStreamResponse(Result.getSuc());
	}


	public TextStreamResponse onViewlog() {
		final CommonDump commonDump = TapestryAssist.getBeanFromPage(CommonDump.class, requestGlobals);
		deployService.viewLog(CommandType.dump, commonDump.getId(), commonDump.getDeployId());
		return TapestryAssist.getTextStreamResponse(Result.getSuc());
	}

	/**
	 * 开启任务
	 * 
	 * @return
	 */
	public TextStreamResponse onStartDump() {
		final CommonDump commonDump = TapestryAssist.getBeanFromPage(CommonDump.class, requestGlobals);
		Result startDump = deployService.startTask(CommandType.dump, commonDump.getId(), commonDump.getDeployId(),
				false);
		return TapestryAssist.getTextStreamResponse(startDump);
	}
	
	// 布署配置文件
	public TextStreamResponse onAddConfig() {
		final CommonDump commonDump = TapestryAssist.getBeanFromPage(CommonDump.class, requestGlobals);
		Result startDump = deployService.addConfig(CommandType.dump, commonDump.getId(), commonDump.getDeployId());
		return TapestryAssist.getTextStreamResponse(startDump);
	}

	/// 停止任务,会等3分钟。
	public TextStreamResponse onStopDump() {
		final CommonDump commonDump = TapestryAssist.getBeanFromPage(CommonDump.class, requestGlobals);
		Result stopDump = deployService.stopTask(CommandType.dump, commonDump.getId(), commonDump.getDeployId());
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
			String queryStatus = deployService.queryStatus(CommandType.dump, commonDump.getId(),
					commonDump.getDeployId());
			System.out.println("=========stopdump============" + queryStatus);
			if (queryStatus.contains("未布署")) {
				break;
			} else {
				continue;
			}
		}
		return TapestryAssist.getTextStreamResponse(stopDump);
	}
	

	

}