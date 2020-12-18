package net.wicp.tams.duckula.ops.pages;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.internal.util.CollectionFactory;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import net.wicp.tams.app.duckula.controller.bean.models.SysGlobal;
import net.wicp.tams.app.duckula.controller.config.constant.ConfigGlobleName;
import net.wicp.tams.app.duckula.controller.dao.SysGlobalMapper;
import net.wicp.tams.common.Conf;
import net.wicp.tams.common.Result;
import net.wicp.tams.common.apiext.StringUtil;
import net.wicp.tams.common.constant.dic.YesOrNo;
import net.wicp.tams.component.assistbean.Menu;
import net.wicp.tams.component.constant.ResType;
import net.wicp.tams.component.tools.TapestryAssist;
import net.wicp.tams.duckula.ops.beans.SessionBean;

/**
 * Start page of application duckula-ops.
 */
public class Index {

	@Inject
	@Path("menu.properties")
	private Asset asset;

	@SessionState
	private SessionBean sessionBean;

	private boolean sessionBeanExists;

	@InjectPage
	private Login login;

	@Inject
	protected Request request;

	@Inject
	private SysGlobalMapper sysGlobalMapper;

	@OnEvent(value = "switchMenu")
	public List<Menu> switchMenu(String moudleId) throws IOException {
		Properties prop = new Properties();
		prop.load(asset.getResource().openStream());
		String[] menus = prop.getProperty("menu.all").split(",");
		List<Menu> retlist = CollectionFactory.newList();
		for (int i = 0; i < menus.length; i++) {
			if (!menus[i].startsWith(moudleId)) {
				continue;
			}
			String id = prop.getProperty(String.format("%s.id", menus[i]));

			String resName = prop.getProperty(String.format("%s.resName", menus[i]));
			String resType = prop.getProperty(String.format("%s.resType", menus[i]));
			String resValue = prop.getProperty(String.format("%s.resValue", menus[i]));
			if (StringUtil.isNotNull(id) && StringUtil.isNotNull(resName) && StringUtil.isNotNull(resType)
					&& StringUtil.isNotNull(resValue)) {
				Menu menu = Menu.builder().id(id).resName(resName).resType(ResType.get(resType)).resValue(resValue)
						.build();
				retlist.add(menu);
			}
		}
		return retlist;
	}

	public TextStreamResponse onDataSave() {
		String saveDataStr = request.getParameter("saveData");
		JSONObject dgAll = JSONObject.parseObject(saveDataStr);
		JSONArray rows = dgAll.getJSONArray("rows");
		System.out.println("rows=" + rows.size());
		SysGlobal save = new SysGlobal();
		save.setId(1l);
		save.setConfigGloble(dgAll.toJSONString());
		save.setLastUpdatetime(new Date());
		save.setLastUsername(sessionBean.getSysUser().getUsername());
		putConfig(save);
		SysGlobal selectById = sysGlobalMapper.selectById(1L);
		if (selectById == null) {
			sysGlobalMapper.insert(save);
		} else {
			sysGlobalMapper.updateByPrimaryKeySelective(save);
		}
		return TapestryAssist.getTextStreamResponse(Result.getSuc());
	}

	private void putConfig(SysGlobal save) {
		// aws配置
		Conf.overProp("common.aws.region", ConfigGlobleName.region.getValue(save));
		Conf.overProp("common.aws.profile.accessKey", ConfigGlobleName.accessKey.getValue(save));
		Conf.overProp("common.aws.profile.secretKey", ConfigGlobleName.secretKey.getValue(save));
		Conf.overProp("common.aws.sqs.s3.bucketName", ConfigGlobleName.bucketName.getValue(save));
	}

	public TextStreamResponse onDataInit() {
		JSONObject retjson = null;
		SysGlobal sysGlobal = sysGlobalMapper.selectById(1L);
		if (sysGlobal != null) {
			retjson = JSON.parseObject(sysGlobal.getConfigGloble());
		} else {
			putConfig(sysGlobal);
			retjson = ConfigGlobleName.retInitJsonObject();
		}
		return TapestryAssist.getTextStreamResponse(retjson.toJSONString());
	}

	public TextStreamResponse onLogout() {
		sessionBean = null;
		return TapestryAssist.getTextStreamResponse(Result.getSuc());
	}

	public Object onActivate() {
		if (!sessionBeanExists || sessionBean == null || sessionBean.getIsLogin() == YesOrNo.no) {
			return login;
		} else {
			return null;
		}
	}
}
