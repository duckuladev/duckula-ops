package net.wicp.tams.duckula.ops.pages.setting;

import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.util.TextStreamResponse;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import net.wicp.tams.app.duckula.controller.bean.models.CommonInstance;
import net.wicp.tams.app.duckula.controller.dao.CommonInstanceMapper;
import net.wicp.tams.common.Result;
import net.wicp.tams.common.apiext.StringUtil;
import net.wicp.tams.common.apiext.json.EasyUiAssist;
import net.wicp.tams.component.services.IReq;
import net.wicp.tams.component.tools.TapestryAssist;
import net.wicp.tams.duckula.ops.WebTools;

public class InstanceManager {
	@Inject
	protected RequestGlobals requestGlobals;

	@Inject
	protected Request request;

	@Inject
	private IReq req;

	@Inject
	private CommonInstanceMapper commonInstanceMapper;

	public TextStreamResponse onQuery() {
		// ajax.req(key, params);
		final CommonInstance commonInstance = TapestryAssist.getBeanFromPage(CommonInstance.class, requestGlobals);
		QueryWrapper<CommonInstance> queryWrapper = new QueryWrapper<CommonInstance>();
		if (StringUtil.isNotNull(commonInstance.getName())) {
			queryWrapper.likeRight("name", commonInstance.getName());
		}
		
		
		String needpage = request.getParameter("needpage");
		boolean isPage = StringUtil.isNotNull(needpage) && !Boolean.parseBoolean(needpage) ? false : true;
		
		List<CommonInstance> selectList = null;
		long size = 0;
		if (isPage) {// 需要分页，默认
			Page<CommonInstance> selectPage = commonInstanceMapper.selectPage(WebTools.buildPage(request), queryWrapper);
			selectList = selectPage.getRecords();
			size = selectPage.getTotal();
		} else {
			selectList = commonInstanceMapper.selectList(queryWrapper);
			size = selectList.size();
		}
		
		String retstr = EasyUiAssist.getJsonForGridAlias(selectList, size);
		return TapestryAssist.getTextStreamResponse(retstr);
	}

	public TextStreamResponse onSave() {
		final CommonInstance commonInstance = TapestryAssist.getBeanFromPage(CommonInstance.class, requestGlobals);
		if (commonInstance.getId() == null) {
			commonInstanceMapper.insert(commonInstance);
		} else {
			commonInstanceMapper.updateByPrimaryKeySelective(commonInstance);
		}
		return TapestryAssist.getTextStreamResponse(Result.getSuc());
	}

	public TextStreamResponse onDel() {
		String id = request.getParameter("id");
		commonInstanceMapper.deleteById(id);
		return TapestryAssist.getTextStreamResponse(Result.getSuc());
	}
}