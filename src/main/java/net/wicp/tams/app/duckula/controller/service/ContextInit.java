package net.wicp.tams.app.duckula.controller.service;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import net.wicp.tams.app.duckula.controller.bean.models.SysGlobal;
import net.wicp.tams.app.duckula.controller.config.constant.ConfigGlobleName;
import net.wicp.tams.app.duckula.controller.dao.SysGlobalMapper;

@Service
public class ContextInit implements ApplicationContextAware {

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SysGlobalMapper sysGlobalMapper = applicationContext.getBean(SysGlobalMapper.class);
		SysGlobal sysGlobal = sysGlobalMapper.selectById(1l);
		if (sysGlobal != null) {
			ConfigGlobleName.putAwsConfig(sysGlobal);
		}

	}
}
