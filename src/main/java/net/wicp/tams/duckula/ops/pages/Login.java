package net.wicp.tams.duckula.ops.pages;

import java.util.List;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.slf4j.Logger;

import lombok.extern.slf4j.Slf4j;
import net.wicp.tams.app.duckula.controller.bean.models.SysUser;
import net.wicp.tams.app.duckula.controller.dao.SysUserMapper;
import net.wicp.tams.common.apiext.CollectionUtil;
import net.wicp.tams.common.apiext.StringUtil;
import net.wicp.tams.common.constant.dic.YesOrNo;
import net.wicp.tams.duckula.ops.beans.SessionBean;

@Import(stack = "easyuistack")
@Slf4j
public class Login {
	@Inject
	private Logger logger;

	@Inject
	protected Request request;

	@SessionState
	private SessionBean sessionBean;

	@Inject
	private SysUserMapper sysUserMapper;

	@SuppressWarnings("unchecked")
	Object onSuccess() {
		String userName = request.getParameter("userName");
		String pwd = request.getParameter("pwd");
		if (StringUtil.isNull(userName) || StringUtil.isNull(pwd)) {
			return Login.class;
		}
		List<SysUser> selUsers = sysUserMapper.selectByMap(CollectionUtil.newMap("username", userName));
		if (selUsers.size() != 1) {
			log.error("用户不惟一,数量：{}", selUsers.size());
			return Login.class;
		}
		SysUser sysUser = selUsers.get(0);
		if(!sysUser.getPassword().equals(pwd)) {
			log.error("用户{}密码有误", userName);
			return Login.class;
		}
		sessionBean = new SessionBean();
		sessionBean.setIsLogin(YesOrNo.yes);
		sessionBean.setSysUser(selUsers.get(0));
		return Index.class;
	}

}
