package net.wicp.tams.duckula.ops.pages.setting;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.util.TextStreamResponse;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import net.wicp.tams.app.duckula.controller.BusiTools;
import net.wicp.tams.app.duckula.controller.bean.models.CommonDeploy;
import net.wicp.tams.app.duckula.controller.bean.models.CommonVersion;
import net.wicp.tams.app.duckula.controller.config.k8s.ApiClientManager;
import net.wicp.tams.app.duckula.controller.dao.CommonDeployMapper;
import net.wicp.tams.app.duckula.controller.dao.CommonVersionMapper;
import net.wicp.tams.app.duckula.controller.service.DeployService;
import net.wicp.tams.common.Result;
import net.wicp.tams.common.apiext.CollectionUtil;
import net.wicp.tams.common.apiext.StringUtil;
import net.wicp.tams.common.apiext.json.EasyUiAssist;
import net.wicp.tams.common.callback.IConvertValue;
import net.wicp.tams.common.callback.impl.convertvalue.ConvertValueEnum;
import net.wicp.tams.common.constant.dic.YesOrNo;
import net.wicp.tams.component.services.IReq;
import net.wicp.tams.component.tools.TapestryAssist;
import net.wicp.tams.duckula.ops.WebTools;
import net.wicp.tams.duckula.ops.ajax.IAjax;

public class DeployManager {
	@Inject
	protected RequestGlobals requestGlobals;

	@Inject
	protected Request request;

	@Inject
	private IReq req;

	@Inject
	private IAjax ajax;
	@Inject
	private CommonDeployMapper commonDeployMapper;
	@Inject
	private CommonVersionMapper commonVersionMapper;
	@Inject
	private DeployService deployService;

	@Property
	@Inject
	@Symbol(SymbolConstants.CONTEXT_PATH)
	private String contextPath;

	public TextStreamResponse onQuery() {
		// ajax.req(key, params);
		final CommonDeploy commonDeploy = TapestryAssist.getBeanFromPage(CommonDeploy.class, requestGlobals);
		QueryWrapper<CommonDeploy> queryWrapper = new QueryWrapper<CommonDeploy>();
		if (StringUtil.isNotNull(commonDeploy.getName())) {
			queryWrapper.likeRight("name", commonDeploy.getName());
		}

		String needpage = request.getParameter("needpage");
		boolean isPage = StringUtil.isNotNull(needpage) && !Boolean.parseBoolean(needpage) ? false : true;

		List<CommonDeploy> selectList = null;
		long size = 0;
		if (isPage) {// 需要分页，默认
			Page<CommonDeploy> selectPage = commonDeployMapper.selectPage(WebTools.buildPage(request), queryWrapper);
			selectList = selectPage.getRecords();
			size = selectPage.getTotal();
		} else {
			selectList = commonDeployMapper.selectList(queryWrapper);
			size = selectList.size();
		}
		final Map<Long, String> datamap = BusiTools.convertValues(selectList, commonVersionMapper, "versionId",
				"mainVersion");
		IConvertValue<String> versionConvert = new IConvertValue<String>() {
			@Override
			public String getStr(String keyObj) {
				return StringUtil.isNull(keyObj) ? "" : datamap.get(Long.parseLong(keyObj));
			}
		};
		String retstr = EasyUiAssist.getJsonForGrid(selectList,
				new String[] { "id", "name", "deploy", "env", "namespace", "host", "port", "pwdDuckula", "isInit",
						"dockerLogin", "isDefault", "imagegroup", "versionId", "remark", "isInit,isInit2",
						"versionId,version1" },
				new IConvertValue[] { null, null, null, null, null, null, null, null, null, null, null, null, null,
						null, new ConvertValueEnum(YesOrNo.class), versionConvert },
				size);
		return TapestryAssist.getTextStreamResponse(retstr);
	}

	public TextStreamResponse onSave() {
		final CommonDeploy commonDeploy = TapestryAssist.getBeanFromPage(CommonDeploy.class, requestGlobals);

		if (commonDeploy.getId() == null) {
			commonDeployMapper.insert(commonDeploy);
		} else {
			commonDeployMapper.updateByPrimaryKeySelective(commonDeploy);
		}
		return TapestryAssist.getTextStreamResponse(Result.getSuc());
	}

	public TextStreamResponse onDel() {
		String id = request.getParameter("id");
		commonDeployMapper.deleteById(id);
		return TapestryAssist.getTextStreamResponse(Result.getSuc());
	}

	public TextStreamResponse onInitServer() {
		final CommonDeploy commonDeploy = TapestryAssist.getBeanFromPage(CommonDeploy.class, requestGlobals);
		String pwd = request.getParameter("pwd");
		Result result = deployService.initHost(commonDeploy, pwd);
		if (result.isSuc()) {
			commonDeploy.setIsInit(YesOrNo.yes.name());
			commonDeployMapper.updateByPrimaryKeySelective(commonDeploy);
		}
		return TapestryAssist.getTextStreamResponse(result);
	}

	@SuppressWarnings("unchecked")
	public TextStreamResponse onUpServer() {
		final CommonDeploy commonDeploy = TapestryAssist.getBeanFromPage(CommonDeploy.class, requestGlobals);
		CommonVersion commonVersionNew = null;
		try {
			String versionNew = request.getParameter("versionNew");
			versionNew = versionNew.startsWith("task.") ? versionNew : "task." + versionNew;
			List<CommonVersion> selectByMap = commonVersionMapper
					.selectByMap(CollectionUtil.newMap("main_version", versionNew));
			if (CollectionUtils.isEmpty(selectByMap) || selectByMap.size() > 1) {
				return TapestryAssist.getTextStreamResponse(Result.getError("没有这个版本或存在2个相同的版本"));
			}
			commonVersionNew = selectByMap.get(0);
		} catch (Exception e) {
			return TapestryAssist.getTextStreamResponse(Result.getError(e.getMessage()));
		}
		Result result = deployService.upgradeVersion(commonDeploy, commonVersionNew);
		if (result.isSuc()) {
			commonDeploy.setVersionId(commonVersionNew.getId().longValue());
			commonDeployMapper.updateByPrimaryKeySelective(commonDeploy);
		}
		return TapestryAssist.getTextStreamResponse(result);
	}

	public void onSaveFile() {
		List<String> uploadFiles = req.uploadFile();
		if (CollectionUtils.isEmpty(uploadFiles)) {
			return;
		}
		String id = request.getParameter("id");
		CommonDeploy commonDeploy = new CommonDeploy();
		commonDeploy.setId(Long.parseLong(id));
		commonDeploy.setConfig(uploadFiles.get(0));
		commonDeploy.setIsInit(YesOrNo.yes.name());
		commonDeployMapper.updateByPrimaryKeySelective(commonDeploy);
		// 要清理相关的线程连接
		ApiClientManager.cleanThread(id);
	}

}