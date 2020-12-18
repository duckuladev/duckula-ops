package net.wicp.tams.app.duckula.controller.config.constant;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import net.wicp.tams.app.duckula.controller.bean.models.SysGlobal;
import net.wicp.tams.common.apiext.StringUtil;
import net.wicp.tams.common.constant.dic.intf.IEnumCombobox;

/**
 * 需要定义的全局配置项
 * 
 * @author andy.zhou
 *
 */
public enum ConfigGlobleName implements IEnumCombobox {

	email("邮箱", "管理员", "validatebox", "email", null),

	region("区域(region)", "AWS(亚马逊)", "text", null, "cn-northwest-1"),

	accessKey("accessKey", "AWS(亚马逊)", "text", null, null),

	secretKey("secretKey", "AWS(亚马逊)", "text", null, null),

	bucketName("bucketName", "AWS(亚马逊)", "text", null, null);

	private final String desc;
	private final String group;

	private final String type;
	private final String validType;
	private final String defaultValue;

	private ConfigGlobleName(String desc, String group, String type, String validType, String defaultValue) {
		this.desc = desc;
		this.group = group;
		this.type = type;
		this.validType = validType;
		this.defaultValue = defaultValue;
	}

	public static JSONObject retInitJsonObject() {
		ConfigGlobleName[] configs = ConfigGlobleName.values();
		JSONArray rows = new JSONArray();
		for (ConfigGlobleName configGlobleName : configs) {
			JSONObject parseObject = new JSONObject();
			parseObject.put("name", configGlobleName.getDesc());
			parseObject.put("value", configGlobleName.getDefaultValue());
			parseObject.put("group", configGlobleName.getGroup());
			if (StringUtil.isNotNull(configGlobleName.getValidType())) {
				parseObject.put("editor",
						JSON.parseObject(String.format("{\"type\":\"%s\",\"options\":{\"validType\":\"%s\"}}",
								configGlobleName.getType(), configGlobleName.getValidType())));
			} else {
				parseObject.put("editor", configGlobleName.getType());
			}
			rows.add(parseObject);
		}
		JSONObject retjson = new JSONObject();
		retjson.put("rows", rows);
		retjson.put("total", retjson.size());
		return retjson;
	}

	// 得到配置文件的值
	public String getValue(SysGlobal sysGlobal) {
		if (sysGlobal == null) {
			return "";
		}
		JSONObject object = JSON.parseObject(sysGlobal.getConfigGloble());
		JSONArray jsonArray = object.getJSONArray("rows");
		for (int i = 0; i < jsonArray.size(); i++) {
			String name = jsonArray.getJSONObject(i).getString("name");
			if (this.getDesc().equals(name)) {
				return jsonArray.getJSONObject(i).getString("value");
			}
		}
		return "";
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

	public String getGroup() {
		return group;
	}

	public String getType() {
		return type;
	}

	public String getValidType() {
		return validType;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

}
