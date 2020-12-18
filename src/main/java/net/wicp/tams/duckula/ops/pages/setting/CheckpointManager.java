package net.wicp.tams.duckula.ops.pages.setting;

import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.util.TextStreamResponse;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import net.wicp.tams.app.duckula.controller.bean.models.CommonCheckpoint;
import net.wicp.tams.app.duckula.controller.dao.CommonCheckpointMapper;
import net.wicp.tams.common.Result;
import net.wicp.tams.common.apiext.StringUtil;
import net.wicp.tams.common.apiext.json.EasyUiAssist;
import net.wicp.tams.component.services.IReq;
import net.wicp.tams.component.tools.TapestryAssist;
import net.wicp.tams.duckula.ops.WebTools;

public class CheckpointManager {
	@Inject
	protected RequestGlobals requestGlobals;

	@Inject
	protected Request request;

	@Inject
	private IReq req;

	@Inject
	private CommonCheckpointMapper commonCheckpointMapper;

	public TextStreamResponse onQuery() {
		// ajax.req(key, params);
		final CommonCheckpoint commonCheckpoint = TapestryAssist.getBeanFromPage(CommonCheckpoint.class, requestGlobals);
		QueryWrapper<CommonCheckpoint> queryWrapper = new QueryWrapper<CommonCheckpoint>();
		if (StringUtil.isNotNull(commonCheckpoint.getName())) {
			queryWrapper.likeRight("name", commonCheckpoint.getName());
		}
		String needpage = request.getParameter("needpage");
		boolean isPage = StringUtil.isNotNull(needpage) && !Boolean.parseBoolean(needpage) ? false : true;
		List<CommonCheckpoint> selectList = null;
		long size = 0;
		if (isPage) {// 需要分页，默认
			Page<CommonCheckpoint> selectPage = commonCheckpointMapper.selectPage(WebTools.buildPage(request), queryWrapper);
			selectList = selectPage.getRecords();
			size = selectPage.getTotal();
		} else {
			selectList = commonCheckpointMapper.selectList(queryWrapper);
			size = selectList.size();
		}		
		String retstr = EasyUiAssist.getJsonForGridAlias(selectList, size);
		return TapestryAssist.getTextStreamResponse(retstr);
	}

	public TextStreamResponse onSave() {
		final CommonCheckpoint commonCheckpoint = TapestryAssist.getBeanFromPage(CommonCheckpoint.class, requestGlobals);
		if (commonCheckpoint.getId() == null) {
			commonCheckpointMapper.insert(commonCheckpoint);
		} else {
			commonCheckpointMapper.updateByPrimaryKeySelective(commonCheckpoint);
		}
		return TapestryAssist.getTextStreamResponse(Result.getSuc());
	}

	public TextStreamResponse onDel() {
		String id = request.getParameter("id");
		commonCheckpointMapper.deleteById(id);
		return TapestryAssist.getTextStreamResponse(Result.getSuc());
	}
}