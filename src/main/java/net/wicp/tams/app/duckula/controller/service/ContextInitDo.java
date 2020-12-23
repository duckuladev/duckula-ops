package net.wicp.tams.app.duckula.controller.service;

import org.springframework.context.ApplicationContext;

import net.wicp.tams.app.duckula.controller.bean.models.SysGlobal;
import net.wicp.tams.app.duckula.controller.config.constant.ConfigGlobleName;
import net.wicp.tams.app.duckula.controller.dao.SysGlobalMapper;
import net.wicp.tams.common.spring.autoconfig.IContextInit;

public class ContextInitDo implements IContextInit {

	@Override
	public void init(ApplicationContext applicationContext) {
		/*
		 * SysGlobalMapper sysGlobalMapper = applicationContext.get
		 * .getBean(SysGlobalMapper.class); SysGlobal sysGlobal =
		 * sysGlobalMapper.selectById(1l); if(sysGlobal!=null) {
		 * ConfigGlobleName.putAwsConfig(sysGlobal); }
		 */
	}

}
