package net.wicp.tams.duckula.ops.pages.setting;

import java.util.List;
import java.util.Map;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.util.TextStreamResponse;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import net.wicp.tams.app.duckula.controller.BusiTools;
import net.wicp.tams.app.duckula.controller.bean.models.CommonVersion;
import net.wicp.tams.app.duckula.controller.dao.CommonVersionMapper;
import net.wicp.tams.common.Result;
import net.wicp.tams.common.apiext.CollectionUtil;
import net.wicp.tams.common.apiext.StringUtil;
import net.wicp.tams.common.apiext.json.EasyUiAssist;
import net.wicp.tams.common.callback.IConvertValue;
import net.wicp.tams.common.callback.impl.convertvalue.ConvertValueDate;
import net.wicp.tams.common.constant.DateFormatCase;
import net.wicp.tams.component.services.IReq;
import net.wicp.tams.component.tools.TapestryAssist;
import net.wicp.tams.duckula.ops.WebTools;

public class VersionManager {
	@Inject
	protected RequestGlobals requestGlobals;

	@Inject
	protected Request request;

	@Inject
	private IReq req;

	@Inject
	private CommonVersionMapper commonVersionMapper;

	public TextStreamResponse onQuery() {
		// ajax.req(key, params);
		final CommonVersion commonVersion = TapestryAssist.getBeanFromPage(CommonVersion.class, requestGlobals);
		QueryWrapper<CommonVersion> queryWrapper = new QueryWrapper<CommonVersion>();
		if (StringUtil.isNotNull(commonVersion.getMainVersion())) {
			queryWrapper.likeRight("mainVersion", commonVersion.getMainVersion());
		}
		IConvertValue<String> datamap = new ConvertValueDate(DateFormatCase.YYYY_MM_DD_hhmmss);
		String needpage = request.getParameter("needpage");
		boolean isPage = StringUtil.isNotNull(needpage) && !Boolean.parseBoolean(needpage) ? false : true;
		List<CommonVersion> selectList = null;
		long size = 0;
		if (isPage) {// 需要分页，默认
			Page<CommonVersion> selectPage = commonVersionMapper.selectPage(WebTools.buildPage(request), queryWrapper);
			selectList = selectPage.getRecords();
			size = selectPage.getTotal();
		} else {
			selectList = commonVersionMapper.selectList(queryWrapper);
			size = selectList.size();
		}
		
		
		String retstr = EasyUiAssist.getJsonForGridAlias2(selectList, new String[] { "updateTime,updateTimeStr" },
				CollectionUtil.newMap("updateTimeStr", datamap), size);
		return TapestryAssist.getTextStreamResponse(retstr);
	}

	public TextStreamResponse onSave() {
		final CommonVersion commonVersion = TapestryAssist.getBeanFromPage(CommonVersion.class, requestGlobals);
		if (commonVersion.getId() == null) {
			commonVersionMapper.insert(commonVersion);
		} else {
			commonVersionMapper.updateByPrimaryKeySelective(commonVersion);
		}
		return TapestryAssist.getTextStreamResponse(Result.getSuc());
	}

	public TextStreamResponse onDel() {
		String id = request.getParameter("id");
		commonVersionMapper.deleteById(id);
		return TapestryAssist.getTextStreamResponse(Result.getSuc());
	}
}