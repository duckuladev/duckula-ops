package net.wicp.tams.app.duckula.controller.config.constant;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import net.wicp.tams.app.duckula.controller.bean.models.CommonMiddleware;
import net.wicp.tams.common.apiext.CollectionUtil;
import net.wicp.tams.common.apiext.StringUtil;
import net.wicp.tams.common.apiext.json.JSONUtil;
import net.wicp.tams.common.binlog.alone.binlog.bean.RuleItem;
import net.wicp.tams.common.constant.dic.intf.IEnumCombobox;

public enum MiddlewareType implements IEnumCombobox {
	es("es搜索", "common.es.", new String[][] {
			{ "5.X", "net.wicp.tams.common.es.plugin.ListenerEs5", "net.wicp.tams.common.es.plugin.ListenerEs5",
					"net.wicp.tams.common.es.plugin.DumperEs5", "env:DUCKULA3_DATA:/plugins/duckula-plugin-es5" },
			{ "6.X", "net.wicp.tams.common.es.plugin.ListenerEs6", "net.wicp.tams.common.es.plugin.ListenerEs6",
					"net.wicp.tams.common.es.plugin.DumperEs6", "env:DUCKULA3_DATA:/plugins/duckula-plugin-es6" },
			{ "7.X", "net.wicp.tams.common.es.plugin.ListenerEs7", "net.wicp.tams.common.es.plugin.ListenerEs7",
					"net.wicp.tams.common.es.plugin.DumperEs7", "env:DUCKULA3_DATA:/plugins/duckula-plugin-es7" } },
			new RuleItem[] { RuleItem.index, RuleItem.type, RuleItem.relakey, RuleItem.copynum, RuleItem.partitions }),

	//manticore("manticore搜索", "", new String[][] { { "3.5" } }, new RuleItem[] {}),

	//cassandra("cassandra数据库", "", new String[][] { { "3" } }, new RuleItem[] { RuleItem.ks, RuleItem.table }),

	mysql("mysql数据库", "common.binlog.alone.plugin.jdbc.",
			new String[][] { { "*", "net.wicp.tams.common.binlog.plugin.jdbc.ListenerJdbc",
					"net.wicp.tams.common.binlog.plugin.jdbc.ListenerJdbc",
					"net.wicp.tams.common.binlog.plugin.jdbc.DumperJdbc", null } }, // null不需要插件
			new RuleItem[] { RuleItem.dbinstanceid, RuleItem.dbtb }),

	kafka("kafka消息", "common.kafka.",
			new String[][] {
					{ "1.X", "net.wicp.tams.common.kafka.plugin.ListenerKafka",
							"net.wicp.tams.common.kafka.plugin.ListenerKafka", null,
							"env:DUCKULA3_DATA:/plugins/duckula-plugin-kafka" },
					{ "2.X", "net.wicp.tams.common.kafka.plugin.ListenerKafka", null,
							"env:DUCKULA3_DATA:/plugins/duckula-plugin-kafka" } },
			new RuleItem[] { RuleItem.topic }),

	http("http服务器", "common.http.",
			new String[][] { { "1.1", "net.wicp.tams.duckula.plugin.http.ListenerHttp",
					"net.wicp.tams.duckula.plugin.http.ListenerHttp", "net.wicp.tams.duckula.plugin.http.DumperHttp",
					"env:DUCKULA3_DATA:/plugins/duckula-plugin-http" } },
			new RuleItem[] { RuleItem.httpRela }),
	// 只打logger，测试用服务器
	logger("logger日志", "",
			new String[][] { { "*", "net.wicp.tams.common.binlog.plugin.logger.ListenerLogger",
					"net.wicp.tams.common.binlog.plugin.logger.ListenerLogger",
					"net.wicp.tams.common.binlog.plugin.logger.DumperLogger", null } },
			new RuleItem[] {}),

	;

	private final String[][] verPlugins;// 支持的版本,元素：0:版本 1：监听 2：全量 3:插件目录

	private final RuleItem[] ruleItems;// 可以配置的item

	private final String pre;// 配置项目的前缀，

	private final RuleItem[] commonItems = new RuleItem[] { RuleItem.colName, RuleItem.addProp, RuleItem.splitkey };// 公共配置

	public Map<String, Object> proConfig(CommonMiddleware commonMiddleware) {
		Map<String, Object> retmap = new HashMap<String, Object>();
		JSONObject opt = JSON.parseObject(commonMiddleware.getOpt());
		switch (this) {
		case es:
			retmap.put("common.es.host.name", commonMiddleware.getHost());
			retmap.put("common.es.host.port.rest", String.valueOf(commonMiddleware.getPort()));
			retmap.put("common.es.host.port.transport", String.valueOf(commonMiddleware.getPort2()));
			if (StringUtil.isNotNull(commonMiddleware.getUsername())) {
				retmap.put("common.es.cluster.userName", String.valueOf(commonMiddleware.getUsername()));
				retmap.put("common.es.cluster.password", String.valueOf(commonMiddleware.getPassword()));
			}
			break;
		case mysql:
			retmap.put(String.format("%s%s.host", this.pre, commonMiddleware.getId()), commonMiddleware.getHost());
			retmap.put(String.format("%s%s.port", this.pre, commonMiddleware.getId()),
					String.valueOf(commonMiddleware.getPort()));
			retmap.put(String.format("%s%s.username", this.pre, commonMiddleware.getId()),
					String.valueOf(commonMiddleware.getUsername()));
			retmap.put(String.format("%s%s.password", this.pre, commonMiddleware.getId()),
					String.valueOf(commonMiddleware.getPassword()));
			break;
		case kafka:
			String[] hosts = commonMiddleware.getHost().split(",");
			StringBuffer buff = new StringBuffer();
			for (int i = 0; i < hosts.length; i++) {
				buff.append(String.format("%s:%s,", hosts[i], commonMiddleware.getPort()));
			}
			retmap.put(String.format("%scommon.bootstrap.servers", this.pre), buff.substring(0, buff.length() - 1));
			break;
		case http:
			retmap.put(String.format("%s.host", this.pre), commonMiddleware.getHost());
			retmap.put(String.format("%s.port", this.pre), String.valueOf(commonMiddleware.getPort()));
			break;
		default:
			break;
		}
		Map<String, Object> map = JSONUtil.jsonToMap(opt, this.pre);
		retmap.putAll(map);
		return retmap;
	}

	public Map<String, Object> proPluginConfig(CommandType commandType, String version) {
		Map<String, Object> retmap = new HashMap<String, Object>();
		String[] verPluginByVersion = getVerPluginByVersion(version);
		switch (commandType) {
		case task:
			retmap.put("common.binlog.alone.binlog.global.conf.listener", verPluginByVersion[1]);// 监听器
			if (verPluginByVersion[4] != null) {
				retmap.put("common.binlog.alone.binlog.global.busiPluginDir", verPluginByVersion[4]);
			}
			break;
		case consumer:
			retmap.put("common.binlog.alone.consumer.global.busiSender", verPluginByVersion[2]);// 监听器
			if (verPluginByVersion[4] != null) {
				retmap.put("common.binlog.alone.consumer.global.busiPluginDir", verPluginByVersion[4]);
			}
			break;
		case dump:
			retmap.put("common.binlog.alone.dump.global.ori.busiSender", verPluginByVersion[3]);// 监听器
			if (verPluginByVersion[4] != null) {
				retmap.put("common.binlog.alone.dump.global.busiPluginDir", verPluginByVersion[4]);
			}
			break;
		default:
			break;
		}
		return retmap;
	}

	public RuleItem[] getRuleItems() {
		return ruleItems;
	}

	public String[][] getVerPlugins() {
		return verPlugins;
	}

	public String getPre() {
		return pre;
	}

	public String[] getVerPluginByVersion(String version) {
		for (String[] verPlugin : verPlugins) {
			if (version.equalsIgnoreCase(verPlugin[0])) {
				return verPlugin;
			}
		}
		return null;
	}

	/**
	 * 得到插件的所有版本
	 * 
	 * @return
	 */
	public String[] getPluginVers() {
		String[] retAry = new String[verPlugins.length];
		for (int i = 0; i < retAry.length; i++) {
			retAry[i] = verPlugins[i][0];
		}
		return retAry;
	}

	private MiddlewareType(String desc, String pre, String[][] verPlugins, RuleItem[] ruleItems) {
		this.desc = desc;
		this.pre = pre;
		this.verPlugins = verPlugins;
		this.ruleItems = CollectionUtil.arrayMerge(RuleItem[].class, ruleItems, commonItems);
	}

	private final String desc;

	public String getDesc() {
		return desc;
	}

	public String getName() {
		return this.name();
	}

	@Override
	public String getDesc_zh() {
		return this.desc;
	}

	@Override
	public String getDesc_en() {
		return this.name();
	}
}
