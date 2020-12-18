package net.wicp.tams.app.duckula.controller.bean.models;

import java.util.Date;

/**
 *
 * This class was generated by MyBatis Generator.
 * This class corresponds to the database table sys_global
 */
public class SysGlobal {

	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column sys_global.id
	 * @mbg.generated
	 */
	private Long id;
	/**
	 * Database Column Remarks: 分组 This field was generated by MyBatis Generator. This field corresponds to the database column sys_global.config_globle
	 * @mbg.generated
	 */
	private String configGloble;
	/**
	 * Database Column Remarks: 验证类型 This field was generated by MyBatis Generator. This field corresponds to the database column sys_global.last_username
	 * @mbg.generated
	 */
	private String lastUsername;
	/**
	 * Database Column Remarks: 排序 This field was generated by MyBatis Generator. This field corresponds to the database column sys_global.last_updatetime
	 * @mbg.generated
	 */
	private Date lastUpdatetime;

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table sys_global
	 * @mbg.generated
	 */
	public SysGlobal(Long id, String configGloble, String lastUsername, Date lastUpdatetime) {
		this.id = id;
		this.configGloble = configGloble;
		this.lastUsername = lastUsername;
		this.lastUpdatetime = lastUpdatetime;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table sys_global
	 * @mbg.generated
	 */
	public SysGlobal() {
		super();
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column sys_global.id
	 * @return  the value of sys_global.id
	 * @mbg.generated
	 */
	public Long getId() {
		return id;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column sys_global.id
	 * @param id  the value for sys_global.id
	 * @mbg.generated
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column sys_global.config_globle
	 * @return  the value of sys_global.config_globle
	 * @mbg.generated
	 */
	public String getConfigGloble() {
		return configGloble;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column sys_global.config_globle
	 * @param configGloble  the value for sys_global.config_globle
	 * @mbg.generated
	 */
	public void setConfigGloble(String configGloble) {
		this.configGloble = configGloble;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column sys_global.last_username
	 * @return  the value of sys_global.last_username
	 * @mbg.generated
	 */
	public String getLastUsername() {
		return lastUsername;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column sys_global.last_username
	 * @param lastUsername  the value for sys_global.last_username
	 * @mbg.generated
	 */
	public void setLastUsername(String lastUsername) {
		this.lastUsername = lastUsername;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column sys_global.last_updatetime
	 * @return  the value of sys_global.last_updatetime
	 * @mbg.generated
	 */
	public Date getLastUpdatetime() {
		return lastUpdatetime;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column sys_global.last_updatetime
	 * @param lastUpdatetime  the value for sys_global.last_updatetime
	 * @mbg.generated
	 */
	public void setLastUpdatetime(Date lastUpdatetime) {
		this.lastUpdatetime = lastUpdatetime;
	}
}