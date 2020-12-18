package net.wicp.tams.app.duckula.controller.config;

import net.wicp.tams.common.Conf;
import net.wicp.tams.common.apiext.StringUtil;
import net.wicp.tams.common.callback.impl.convertvalue.obj.ConvertValueExcept;
import net.wicp.tams.common.exception.IDynaMsg;
import net.wicp.tams.common.exception.IExcept;

public enum ExceptDuckula implements IExcept {
	duckula_deploy_excetion("任务部署错误", 10000, 403);

	private String desc;
	private int value;
	private int httpCode;
	private int httpCodeSub;

	private IDynaMsg dynaMsg = null;

	private ExceptDuckula(String desc, int value, int httpCode) {
		this.desc = desc;
		this.value = value;
		this.httpCode = httpCode;
	}

	private ExceptDuckula(String desc, int value, int httpCode, int httpCodeSub) {
		this.desc = desc;
		this.value = value;
		this.httpCode = httpCode;
		this.httpCodeSub = httpCodeSub;
	}

	private ExceptDuckula(String desc, int value, IDynaMsg dynaMsg) {
		this.desc = desc;
		this.value = value;
		this.dynaMsg = dynaMsg;
	}

	@Override
	public String getDesc() {
		return this.desc;
	}

	@Override
	public int getErrorValue() {
		return this.value;
	}

	@Override
	public String getErrorCode() {
		return this.name();
	}

	@Override
	public String getErrMsg(Object errBean) {
		ConvertValueExcept cv = new ConvertValueExcept(Conf.getCurLocale());
		return packmsg(cv.getStr(this), errBean);
	}

	@Override
	public String getErrMsg() {
		return getErrMsg(null);
	}

	public String getHttp() {
		if (httpCode == 0 && httpCodeSub == 0) {
			return "200";// 未定义http编码
		}
		if (httpCodeSub == 0) {
			return Integer.toString(httpCode);
		}
		return String.format("%s.%s", httpCode, httpCodeSub);
	}

	private String packmsg(String msg, Object ctx) {
		if (dynaMsg != null && ctx != null) {
			msg = dynaMsg.packMsg(msg, ctx);
		}
		return msg;
	}

	public int getHttpCode() {
		return httpCode;
	}

	public void setHttpCode(int httpCode) {
		this.httpCode = httpCode;
	}

	public int getHttpCodeSub() {
		return httpCodeSub;
	}

	public void setHttpCodeSub(int httpCodeSub) {
		this.httpCodeSub = httpCodeSub;
	}

	/**
	 * 通过http得到httpstatus
	 * 
	 * @param http http编码
	 * @return http状态
	 */
	public static int getHttp(String http) {
		if (StringUtil.isNull(http)) {
			return 200;
		} else {
			return Integer.parseInt(http.split("\\.")[0]);
		}
	}

	public static int getHttp(IExcept error) {
		return getHttp(error.getHttp());
	}

}
