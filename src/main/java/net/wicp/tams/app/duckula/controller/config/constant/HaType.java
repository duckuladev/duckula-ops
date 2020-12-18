package net.wicp.tams.app.duckula.controller.config.constant;

import net.wicp.tams.common.constant.dic.intf.IEnumCombobox;

/**
 * Ha的类型
 * 
 * @author andy.zhou
 *
 */
public enum HaType implements IEnumCombobox {

	last("最后位点(checkpoint最后)", "表示从记录的最后位点启动,当前监听的Ha记录位点"),

	cur("最新位点(数据库最新)", "表示从当前最新位点启动，数据库的最新位点"),

	pos("指定位点(需填gtid)", "表示从设置的gtids启动，需要设置gtid值");

	private final String desc;
	private final String remark;

	public String getRemark() {
		return remark;
	}

	private HaType(String desc, String remark) {
		this.desc = desc;
		this.remark = remark;
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
