package net.wicp.tams.duckula.ops;

import org.apache.tapestry5.services.Request;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;


public abstract class WebTools {
	public static <T> Page<T> buildPage(Request request) {
		int page = Integer.parseInt(request.getParameter("page"));
		int rows = Integer.parseInt(request.getParameter("rows"));
		return new Page<T>(page, rows);
	}
}
