package net.wicp.tams.app.duckula.controller.config.constant;

import java.util.Map;

import net.wicp.tams.common.apiext.CollectionUtil;
import net.wicp.tams.common.apiext.StringUtil;
import net.wicp.tams.common.callback.IConvertValue;
import net.wicp.tams.common.constant.dic.intf.IEnumCombobox;

/***
 * 3种运行模式
 * 
 * @author Andy.zhou
 *
 */
public enum DeployType implements IEnumCombobox {
	k8s("k8s环境", new IConvertValue<String>() {
		@Override
		public String getStr(String keyObj) {
			if (StringUtil.isNull(keyObj)) {
				return TaskStatus.noExit.retStatusMessage(null);
			} else if ("Running".equals(keyObj)) {
				return TaskStatus.running.retStatusMessage(keyObj);
			} else {
				return TaskStatus.other.retStatusMessage(keyObj);
			}
		}
	}),

	docker("docker环境", new IConvertValue<String>() {

		@Override
		public String getStr(String keyObj) {
			if ("running".equals(keyObj)) {
				return TaskStatus.running.retStatusMessage(keyObj);
			} else if ("exited".equals(keyObj)) {
				return TaskStatus.exited.retStatusMessage("Exited");
			} else if ("timeout".equals(keyObj)) {
				return TaskStatus.other.retStatusMessage("Timeout");
			} else {
				return TaskStatus.noExit.retStatusMessage(null);
			}
		}
	}),

	host("centos7主机", new IConvertValue<String>() {

		@Override
		public String getStr(String keyObj) {
			if ("running".equals(keyObj)) {
				return TaskStatus.running.retStatusMessage(keyObj);
			} else if ("timeout".equals(keyObj)) {
				return TaskStatus.other.retStatusMessage("Timeout");
			} else {
				return TaskStatus.noExit.retStatusMessage(null);
			}
		}
	});

	/****
	 * 必须要英文
	 * 
	 * @param commandType
	 * @param taskName
	 * @return
	 */
	public static String getConfig(DeployType deployType, CommandType commandType, String taskName) {
		String returnConfiInfo = commandType.formateTaskName(taskName);
		switch (deployType) {
		case docker:
		case host:
			returnConfiInfo = String.format("/duckula/conf/%s", returnConfiInfo);
			break;
		// k8s没有路径，它通过configmap完成参数传递
		case k8s:
		default:
			break;
		}
		return returnConfiInfo;
	}

	/***
	 * 返回配置文件的形态，
	 * 
	 * @param commandType
	 * @param datamap
	 * @return
	 */
	public static String formateConfig(DeployType deployType, Map<String, Object> datamap) {
		String propString = "";
		switch (deployType) {
		case k8s:
			propString = CollectionUtil.toPropString(datamap, 4);
			break;
		case docker:
		case host:
			propString = CollectionUtil.toPropString(datamap, 0);
		default:
			break;
		}
		return propString;
	}

	private final String desc;

	private final IConvertValue<String> statusConvert;

	private DeployType(String desc, IConvertValue<String> statusConvert) {
		this.desc = desc;
		this.statusConvert = statusConvert;
	}

	public String getStatus(String statusTrue) {
		return statusConvert.getStr(statusTrue);
	}

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
