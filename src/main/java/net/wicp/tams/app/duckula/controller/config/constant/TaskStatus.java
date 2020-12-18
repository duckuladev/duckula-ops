package net.wicp.tams.app.duckula.controller.config.constant;

public enum TaskStatus {
	running("运行中"),

	noExit("未布署"),
	
	exited("停止运行"),

	other("其它状态");

	public final String desc;

	private TaskStatus(String desc) {
		this.desc = desc;
	}

	public String retStatusMessage(String statusTrue) {
		return this == TaskStatus.noExit ? this.desc : String.format("%s【%s】", this.desc, statusTrue);
	}

}
