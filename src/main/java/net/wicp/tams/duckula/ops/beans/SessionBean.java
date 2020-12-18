package net.wicp.tams.duckula.ops.beans;

import lombok.Data;
import net.wicp.tams.app.duckula.controller.bean.models.SysUser;
import net.wicp.tams.common.constant.dic.YesOrNo;

@Data
public class SessionBean {
  private YesOrNo isLogin;//是否登陆
  private SysUser sysUser;
}
