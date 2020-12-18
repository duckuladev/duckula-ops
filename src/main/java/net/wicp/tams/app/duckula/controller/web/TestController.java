/*
 * Copyright 2015 Zhongan.com All right reserved. This software is the
 * confidential and proprietary information of Zhongan.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Zhongan.com.
 */
package net.wicp.tams.app.duckula.controller.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/***
 * 测试用的Controller
 * 
 * @author zhoujunhui
 *
 */
@Controller
@RestController
@Configuration
public class TestController {

	private Logger logger = LoggerFactory.getLogger(TestController.class);

	/***
	 * 测试http插件用的服务
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping("exinfo")
	String getInfo(@RequestBody String body) {
		logger.info("Start Get Info" + body);
		return "bbb";
	}

	@RequestMapping("exconfig")
	String getConfig() {
		logger.info("Start Get Config");
		return "aaa";
	}

}