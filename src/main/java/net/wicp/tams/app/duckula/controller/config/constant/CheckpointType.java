package net.wicp.tams.app.duckula.controller.config.constant;

import net.wicp.tams.common.constant.dic.intf.IEnumCombobox;

/**
 * checkpoint的实现方式
 * 
 * @author andy.zhou
 *
 */
public enum CheckpointType implements IEnumCombobox {

	H2db("h2db数据库", "net.wicp.tams.common.binlog.alone.checkpoint.CheckPointH2db"),

	Memory("纯内存模式", "net.wicp.tams.common.binlog.alone.checkpoint.CheckPointMemory"),

	Mysql("mysql数据库", "net.wicp.tams.common.binlog.alone.checkpoint.CheckPointMysql"),
//暂没测试
	// Zookeeper("Zookeeper中间件",
	// "net.wicp.tams.common.binlog.alone.checkpoint.CheckPointZookeeper")
	;

	private final String desc;
	private final String SaveCheckPointClassName;

	public String getSaveCheckPointClassName() {
		return SaveCheckPointClassName;
	}

	private CheckpointType(String desc, String SaveCheckPointClassName) {
		this.desc = desc;
		this.SaveCheckPointClassName = SaveCheckPointClassName;
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
