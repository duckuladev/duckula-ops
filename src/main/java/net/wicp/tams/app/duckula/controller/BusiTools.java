package net.wicp.tams.app.duckula.controller;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.mvel2.templates.TemplateRuntime;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import lombok.extern.slf4j.Slf4j;
import net.wicp.tams.app.duckula.controller.bean.models.CommonCheckpoint;
import net.wicp.tams.app.duckula.controller.bean.models.CommonConsumer;
import net.wicp.tams.app.duckula.controller.bean.models.CommonDump;
import net.wicp.tams.app.duckula.controller.bean.models.CommonInstance;
import net.wicp.tams.app.duckula.controller.bean.models.CommonMiddleware;
import net.wicp.tams.app.duckula.controller.bean.models.CommonTask;
import net.wicp.tams.app.duckula.controller.bean.models.CommonVersion;
import net.wicp.tams.app.duckula.controller.config.constant.CommandType;
import net.wicp.tams.app.duckula.controller.config.constant.MiddlewareType;
import net.wicp.tams.app.duckula.controller.dao.CommonCheckpointMapper;
import net.wicp.tams.app.duckula.controller.dao.CommonConsumerMapper;
import net.wicp.tams.app.duckula.controller.dao.CommonDumpMapper;
import net.wicp.tams.app.duckula.controller.dao.CommonInstanceMapper;
import net.wicp.tams.app.duckula.controller.dao.CommonMiddlewareMapper;
import net.wicp.tams.app.duckula.controller.dao.CommonTaskMapper;
import net.wicp.tams.common.apiext.CollectionUtil;
import net.wicp.tams.common.apiext.IOUtil;
import net.wicp.tams.common.binlog.alone.binlog.bean.Rule;
import net.wicp.tams.common.binlog.alone.binlog.bean.RuleItem;
import net.wicp.tams.common.binlog.alone.binlog.bean.RuleManager;

@Slf4j
public abstract class BusiTools {
	public static String getVersion(String dirPath, String relaPath) {
		try {
			String context = FileUtils.readFileToString(new File(IOUtil.mergeFolderAndFilePath(dirPath, relaPath)));
			int indexOf = context.indexOf("\r\n");
			String version = context.substring(8, indexOf);
			return version;
		} catch (Exception e) {
			log.error("解析tar文件失败", e);
			return "last";
		}
	}

	public static String packVersionUrl(CommonVersion version, boolean isMain) {
		String returl = "";
		if (isMain) {
			returl = IOUtil.mergeFolderAndFilePath(version.getMainPath(),
					"duckula-" + version.getMainVersion() + ".tar");
		} else {
			returl = IOUtil.mergeFolderAndFilePath(version.getDataPath(),
					"duckula-" + (version.getDataVersion().startsWith("data-") ? "" : "data-")
							+ version.getDataVersion() + ".tar");
		}
		return returl;
	}

	public static <T1, T2> Map<Long, String> convertValues(List<T1> oriList, BaseMapper<T2> maper, String oriColName,
			String... colNames) {
		Map<Long, String> retmap = new HashMap<Long, String>();
		if (CollectionUtils.isEmpty(oriList)) {
			return retmap;
		}
		Set<String> ids = CollectionUtil.getColSetFromObj(oriList, oriColName);
		List<T2> retListObj = maper.selectBatchIds(ids);
		String tempstr = "@{id}~~~";

		for (int i = 0; i < colNames.length; i++) {
			if (i == 0) {
				tempstr += "@{" + colNames[i] + "}";
			} else {
				tempstr += "【@{" + colNames[i] + "}】";
			}
		}

		for (T2 t2 : retListObj) {

			// @{'{"name":"'+name+'","id":"'+id+'",
			// String jsonTempStr = "@['{label:\"'+" + nameFiled + "+'\",value:\"'+"
			// + codeFiled + "+'\",id:\"'+" + idName + "+'\"},']";

			String value = String.valueOf(TemplateRuntime.eval(tempstr, t2));
			String[] tempAry = value.split("~~~");
			retmap.put(Long.parseLong(tempAry[0]), tempAry[1]);
		}
		return retmap;
	}

	public static String configContext(CommonConsumerMapper commonConsumerMapper, CommonTaskMapper commonTaskMapper,
			CommonCheckpointMapper commonCheckpointMapper, CommonDumpMapper commonDumpMapper,
			CommonMiddlewareMapper commonMiddlewareMapper, CommonInstanceMapper commonInstanceMapper,
			CommandType commandType, Long taskId, Map<String, Object> params) {
		Long middlewareId = null;
		String configName = null;
		Long instanceId = null;
		switch (commandType) {
		case task:
			CommonTask selectTask = commonTaskMapper.selectById(taskId);
			CommonCheckpoint commonCheckpoint = commonCheckpointMapper.selectById(selectTask.getCheckpointId());
			params.putAll(CommandType.proTaskConfig(selectTask, commonCheckpoint));// 默认配置
			configName = commandType.formateConfigName(selectTask.getName());
			middlewareId = selectTask.getMiddlewareId();
			instanceId = selectTask.getInstanceId();
			break;
		case consumer:
			CommonConsumer commonConsumer = commonConsumerMapper.selectById(taskId);
			params.putAll(CommandType.proConsumerConfig(commonConsumer));// 默认配置
			configName = commandType.formateConfigName(commonConsumer.getName());
			middlewareId = commonConsumer.getMiddlewareId();
			instanceId = commonConsumer.getInstanceId();
			// 特殊配置,用于幂等反查用
			RuleManager ruleManager = new RuleManager(commonConsumer.getRule());
			Set<String> instanceNames = new HashSet<String>();
			for (Rule rule : ruleManager.getRules()) {
				if (rule.getItems().containsKey(RuleItem.addProp)) {
					instanceNames.add(rule.getItems().get(RuleItem.addProp));
				}
			}
			if (CollectionUtils.isNotEmpty(instanceNames)) {
				QueryWrapper<CommonInstance> queryWrapper = new QueryWrapper<CommonInstance>();
				queryWrapper.in("name", instanceNames.toArray(new String[instanceNames.size()]));
				List<CommonInstance> selectList = commonInstanceMapper.selectList(queryWrapper);
				for (CommonInstance commonInstance : selectList) {
					String prestr = "common.binlog.alone.plugin.jdbc." + commonInstance.getId() + ".";
					params.put(prestr + "host", commonInstance.getHost());
					params.put(prestr + "port", commonInstance.getPort());
					params.put(prestr + "username", commonInstance.getUsername());
					params.put(prestr + "password", commonInstance.getPassword());
					params.put(prestr + "rds", "false");// 写死为false,不理rds
				}
			}
			// end 特殊配置,用于幂等反查用
			// kafka配置
			Long middlewareKafkaId = commonConsumer.getMiddlewareKafkaId();
			CommonMiddleware commonMiddleware = commonMiddlewareMapper.selectById(middlewareKafkaId);
			Map<String, Object> proConfig = MiddlewareType.kafka.proConfig(commonMiddleware);
			params.putAll(proConfig);
			// end kafka配置
			break;
		case dump:
			CommonDump commonDump = commonDumpMapper.selectById(taskId);
			params.putAll(CommandType.proDumpConfig(commonDump));// 默认配置
			configName = commandType.formateConfigName(commonDump.getName());
			middlewareId = commonDump.getMiddlewareId();
			instanceId = commonDump.getInstanceId();
			break;
		default:
			break;
		}
		CommonMiddleware middleware = commonMiddlewareMapper.selectById(middlewareId);
		MiddlewareType middlewareType = MiddlewareType.valueOf(middleware.getMiddlewareType());
		// 配置插件
		Map<String, Object> pluginConfig = middlewareType.proPluginConfig(commandType, middleware.getVersion());
		params.putAll(pluginConfig);
		// 配置目标中间件
		Map<String, Object> proConfig = middlewareType.proConfig(middleware);
		params.putAll(proConfig);
		// 配置监听实例,如consumer可能就没有这个实例
		if (instanceId != null) {
			CommonInstance commonInstance = commonInstanceMapper.selectById(instanceId);
			params.putAll(configInstall(commandType, commonInstance));
		}
		return configName;
	}

	// 配置监听实例
	public static Map<String, Object> configInstall(CommandType taskType, CommonInstance commonInstance) {
		Map<String, Object> tempmap = new HashMap<String, Object>();
		if (commonInstance == null) {
			return tempmap;
		}
		switch (taskType) {
		case task:
			tempmap.put("common.binlog.alone.binlog.global.conf.host", commonInstance.getHost());
			tempmap.put("common.binlog.alone.binlog.global.conf.port", commonInstance.getPort());
			tempmap.put("common.binlog.alone.binlog.global.conf.username", commonInstance.getUsername());
			tempmap.put("common.binlog.alone.binlog.global.conf.password", commonInstance.getPassword());
			tempmap.put("common.binlog.alone.binlog.global.conf.rds", "false");// 写死为false,不理rds
			break;
		case consumer:// 做为返查实例，不是必须
			tempmap.put("common.binlog.alone.plugin.jdbc._global.host", commonInstance.getHost());
			tempmap.put("common.binlog.alone.plugin.jdbc._global.port", commonInstance.getPort());
			tempmap.put("common.binlog.alone.plugin.jdbc._global.username", commonInstance.getUsername());
			tempmap.put("common.binlog.alone.plugin.jdbc._global.password", commonInstance.getPassword());
			tempmap.put("common.binlog.alone.plugin.jdbc._global.rds", "false");// 写死为false,不理rds
			break;
		case dump:
			tempmap.put("common.binlog.alone.dump.global.pool.host", commonInstance.getHost());
			tempmap.put("common.binlog.alone.dump.global.pool.port", commonInstance.getPort());
			tempmap.put("common.binlog.alone.dump.global.pool.username", commonInstance.getUsername());
			tempmap.put("common.binlog.alone.dump.global.pool.password", commonInstance.getPassword());
			// tempmap.put("common.binlog.alone.binlog.global.conf.rds", "false");//
			// 写死为false,不理rds dump不认
			break;
		default:
			break;
		}

		return tempmap;
	}

	public static void printAscill() {
		System.out.println(
				"                                                                                                                                            \r\n"
						+ "            dddddddd                                                                                                                        \r\n"
						+ "            d::::::d                                      kkkkkkkk                             lllllll                    333333333333333   \r\n"
						+ "            d::::::d                                      k::::::k                             l:::::l                   3:::::::::::::::33 \r\n"
						+ "            d::::::d                                      k::::::k                             l:::::l                   3::::::33333::::::3\r\n"
						+ "            d:::::d                                       k::::::k                             l:::::l                   3333333     3:::::3\r\n"
						+ "    ddddddddd:::::d uuuuuu    uuuuuu      cccccccccccccccc k:::::k    kkkkkkkuuuuuu    uuuuuu   l::::l   aaaaaaaaaaaaa               3:::::3\r\n"
						+ "  dd::::::::::::::d u::::u    u::::u    cc:::::::::::::::c k:::::k   k:::::k u::::u    u::::u   l::::l   a::::::::::::a              3:::::3\r\n"
						+ " d::::::::::::::::d u::::u    u::::u   c:::::::::::::::::c k:::::k  k:::::k  u::::u    u::::u   l::::l   aaaaaaaaa:::::a     33333333:::::3 \r\n"
						+ "d:::::::ddddd:::::d u::::u    u::::u  c:::::::cccccc:::::c k:::::k k:::::k   u::::u    u::::u   l::::l            a::::a     3:::::::::::3  \r\n"
						+ "d::::::d    d:::::d u::::u    u::::u  c::::::c     ccccccc k::::::k:::::k    u::::u    u::::u   l::::l     aaaaaaa:::::a     33333333:::::3 \r\n"
						+ "d:::::d     d:::::d u::::u    u::::u  c:::::c              k:::::::::::k     u::::u    u::::u   l::::l   aa::::::::::::a             3:::::3\r\n"
						+ "d:::::d     d:::::d u::::u    u::::u  c:::::c              k:::::::::::k     u::::u    u::::u   l::::l  a::::aaaa::::::a             3:::::3\r\n"
						+ "d:::::d     d:::::d u:::::uuuu:::::u  c::::::c     ccccccc k::::::k:::::k    u:::::uuuu:::::u   l::::l a::::a    a:::::a             3:::::3\r\n"
						+ "d::::::ddddd::::::ddu:::::::::::::::uuc:::::::cccccc:::::ck::::::k k:::::k   u:::::::::::::::uul::::::la::::a    a:::::a 3333333     3:::::3\r\n"
						+ " d:::::::::::::::::d u:::::::::::::::u c:::::::::::::::::ck::::::k  k:::::k   u:::::::::::::::ul::::::la:::::aaaa::::::a 3::::::33333::::::3\r\n"
						+ "  d:::::::::ddd::::d  uu::::::::uu:::u  cc:::::::::::::::ck::::::k   k:::::k   uu::::::::uu:::ul::::::l a::::::::::aa:::a3:::::::::::::::33 \r\n"
						+ "   ddddddddd   ddddd    uuuuuuuu  uuuu    cccccccccccccccckkkkkkkk    kkkkkkk    uuuuuuuu  uuuullllllll  aaaaaaaaaa  aaaa 333333333333333   \r\n"
						+ "                                                                                                                                            \r\n"
						+ "                                                                                                                                            \r\n"
						+ "                                                                                                                                            \r\n"
						+ "                                                                                                                                            \r\n"
						+ "                                                                                                                                            ");
	}
}
