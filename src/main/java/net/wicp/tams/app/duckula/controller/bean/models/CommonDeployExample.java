package net.wicp.tams.app.duckula.controller.bean.models;

import java.util.ArrayList;
import java.util.List;

public class CommonDeployExample {
    /**
	 * This field was generated by MyBatis Generator. This field corresponds to the database table common_deploy
	 * @mbg.generated
	 */
	protected String orderByClause;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database table common_deploy
	 * @mbg.generated
	 */
	protected boolean distinct;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database table common_deploy
	 * @mbg.generated
	 */
	protected List<Criteria> oredCriteria;

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table common_deploy
	 * @mbg.generated
	 */
	public CommonDeployExample() {
		oredCriteria = new ArrayList<>();
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table common_deploy
	 * @mbg.generated
	 */
	public void setOrderByClause(String orderByClause) {
		this.orderByClause = orderByClause;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table common_deploy
	 * @mbg.generated
	 */
	public String getOrderByClause() {
		return orderByClause;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table common_deploy
	 * @mbg.generated
	 */
	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table common_deploy
	 * @mbg.generated
	 */
	public boolean isDistinct() {
		return distinct;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table common_deploy
	 * @mbg.generated
	 */
	public List<Criteria> getOredCriteria() {
		return oredCriteria;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table common_deploy
	 * @mbg.generated
	 */
	public void or(Criteria criteria) {
		oredCriteria.add(criteria);
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table common_deploy
	 * @mbg.generated
	 */
	public Criteria or() {
		Criteria criteria = createCriteriaInternal();
		oredCriteria.add(criteria);
		return criteria;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table common_deploy
	 * @mbg.generated
	 */
	public Criteria createCriteria() {
		Criteria criteria = createCriteriaInternal();
		if (oredCriteria.size() == 0) {
			oredCriteria.add(criteria);
		}
		return criteria;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table common_deploy
	 * @mbg.generated
	 */
	protected Criteria createCriteriaInternal() {
		Criteria criteria = new Criteria();
		return criteria;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table common_deploy
	 * @mbg.generated
	 */
	public void clear() {
		oredCriteria.clear();
		orderByClause = null;
		distinct = false;
	}

	/**
	 * This class was generated by MyBatis Generator. This class corresponds to the database table common_deploy
	 * @mbg.generated
	 */
	protected abstract static class GeneratedCriteria {
		protected List<Criterion> criteria;

		protected GeneratedCriteria() {
			super();
			criteria = new ArrayList<>();
		}

		public boolean isValid() {
			return criteria.size() > 0;
		}

		public List<Criterion> getAllCriteria() {
			return criteria;
		}

		public List<Criterion> getCriteria() {
			return criteria;
		}

		protected void addCriterion(String condition) {
			if (condition == null) {
				throw new RuntimeException("Value for condition cannot be null");
			}
			criteria.add(new Criterion(condition));
		}

		protected void addCriterion(String condition, Object value, String property) {
			if (value == null) {
				throw new RuntimeException("Value for " + property + " cannot be null");
			}
			criteria.add(new Criterion(condition, value));
		}

		protected void addCriterion(String condition, Object value1, Object value2, String property) {
			if (value1 == null || value2 == null) {
				throw new RuntimeException("Between values for " + property + " cannot be null");
			}
			criteria.add(new Criterion(condition, value1, value2));
		}

		public Criteria andIdIsNull() {
			addCriterion("id is null");
			return (Criteria) this;
		}

		public Criteria andIdIsNotNull() {
			addCriterion("id is not null");
			return (Criteria) this;
		}

		public Criteria andIdEqualTo(Long value) {
			addCriterion("id =", value, "id");
			return (Criteria) this;
		}

		public Criteria andIdNotEqualTo(Long value) {
			addCriterion("id <>", value, "id");
			return (Criteria) this;
		}

		public Criteria andIdGreaterThan(Long value) {
			addCriterion("id >", value, "id");
			return (Criteria) this;
		}

		public Criteria andIdGreaterThanOrEqualTo(Long value) {
			addCriterion("id >=", value, "id");
			return (Criteria) this;
		}

		public Criteria andIdLessThan(Long value) {
			addCriterion("id <", value, "id");
			return (Criteria) this;
		}

		public Criteria andIdLessThanOrEqualTo(Long value) {
			addCriterion("id <=", value, "id");
			return (Criteria) this;
		}

		public Criteria andIdIn(List<Long> values) {
			addCriterion("id in", values, "id");
			return (Criteria) this;
		}

		public Criteria andIdNotIn(List<Long> values) {
			addCriterion("id not in", values, "id");
			return (Criteria) this;
		}

		public Criteria andIdBetween(Long value1, Long value2) {
			addCriterion("id between", value1, value2, "id");
			return (Criteria) this;
		}

		public Criteria andIdNotBetween(Long value1, Long value2) {
			addCriterion("id not between", value1, value2, "id");
			return (Criteria) this;
		}

		public Criteria andNameIsNull() {
			addCriterion("name is null");
			return (Criteria) this;
		}

		public Criteria andNameIsNotNull() {
			addCriterion("name is not null");
			return (Criteria) this;
		}

		public Criteria andNameEqualTo(String value) {
			addCriterion("name =", value, "name");
			return (Criteria) this;
		}

		public Criteria andNameNotEqualTo(String value) {
			addCriterion("name <>", value, "name");
			return (Criteria) this;
		}

		public Criteria andNameGreaterThan(String value) {
			addCriterion("name >", value, "name");
			return (Criteria) this;
		}

		public Criteria andNameGreaterThanOrEqualTo(String value) {
			addCriterion("name >=", value, "name");
			return (Criteria) this;
		}

		public Criteria andNameLessThan(String value) {
			addCriterion("name <", value, "name");
			return (Criteria) this;
		}

		public Criteria andNameLessThanOrEqualTo(String value) {
			addCriterion("name <=", value, "name");
			return (Criteria) this;
		}

		public Criteria andNameLike(String value) {
			addCriterion("name like", value, "name");
			return (Criteria) this;
		}

		public Criteria andNameNotLike(String value) {
			addCriterion("name not like", value, "name");
			return (Criteria) this;
		}

		public Criteria andNameIn(List<String> values) {
			addCriterion("name in", values, "name");
			return (Criteria) this;
		}

		public Criteria andNameNotIn(List<String> values) {
			addCriterion("name not in", values, "name");
			return (Criteria) this;
		}

		public Criteria andNameBetween(String value1, String value2) {
			addCriterion("name between", value1, value2, "name");
			return (Criteria) this;
		}

		public Criteria andNameNotBetween(String value1, String value2) {
			addCriterion("name not between", value1, value2, "name");
			return (Criteria) this;
		}

		public Criteria andDeployIsNull() {
			addCriterion("deploy is null");
			return (Criteria) this;
		}

		public Criteria andDeployIsNotNull() {
			addCriterion("deploy is not null");
			return (Criteria) this;
		}

		public Criteria andDeployEqualTo(String value) {
			addCriterion("deploy =", value, "deploy");
			return (Criteria) this;
		}

		public Criteria andDeployNotEqualTo(String value) {
			addCriterion("deploy <>", value, "deploy");
			return (Criteria) this;
		}

		public Criteria andDeployGreaterThan(String value) {
			addCriterion("deploy >", value, "deploy");
			return (Criteria) this;
		}

		public Criteria andDeployGreaterThanOrEqualTo(String value) {
			addCriterion("deploy >=", value, "deploy");
			return (Criteria) this;
		}

		public Criteria andDeployLessThan(String value) {
			addCriterion("deploy <", value, "deploy");
			return (Criteria) this;
		}

		public Criteria andDeployLessThanOrEqualTo(String value) {
			addCriterion("deploy <=", value, "deploy");
			return (Criteria) this;
		}

		public Criteria andDeployLike(String value) {
			addCriterion("deploy like", value, "deploy");
			return (Criteria) this;
		}

		public Criteria andDeployNotLike(String value) {
			addCriterion("deploy not like", value, "deploy");
			return (Criteria) this;
		}

		public Criteria andDeployIn(List<String> values) {
			addCriterion("deploy in", values, "deploy");
			return (Criteria) this;
		}

		public Criteria andDeployNotIn(List<String> values) {
			addCriterion("deploy not in", values, "deploy");
			return (Criteria) this;
		}

		public Criteria andDeployBetween(String value1, String value2) {
			addCriterion("deploy between", value1, value2, "deploy");
			return (Criteria) this;
		}

		public Criteria andDeployNotBetween(String value1, String value2) {
			addCriterion("deploy not between", value1, value2, "deploy");
			return (Criteria) this;
		}

		public Criteria andEnvIsNull() {
			addCriterion("env is null");
			return (Criteria) this;
		}

		public Criteria andEnvIsNotNull() {
			addCriterion("env is not null");
			return (Criteria) this;
		}

		public Criteria andEnvEqualTo(String value) {
			addCriterion("env =", value, "env");
			return (Criteria) this;
		}

		public Criteria andEnvNotEqualTo(String value) {
			addCriterion("env <>", value, "env");
			return (Criteria) this;
		}

		public Criteria andEnvGreaterThan(String value) {
			addCriterion("env >", value, "env");
			return (Criteria) this;
		}

		public Criteria andEnvGreaterThanOrEqualTo(String value) {
			addCriterion("env >=", value, "env");
			return (Criteria) this;
		}

		public Criteria andEnvLessThan(String value) {
			addCriterion("env <", value, "env");
			return (Criteria) this;
		}

		public Criteria andEnvLessThanOrEqualTo(String value) {
			addCriterion("env <=", value, "env");
			return (Criteria) this;
		}

		public Criteria andEnvLike(String value) {
			addCriterion("env like", value, "env");
			return (Criteria) this;
		}

		public Criteria andEnvNotLike(String value) {
			addCriterion("env not like", value, "env");
			return (Criteria) this;
		}

		public Criteria andEnvIn(List<String> values) {
			addCriterion("env in", values, "env");
			return (Criteria) this;
		}

		public Criteria andEnvNotIn(List<String> values) {
			addCriterion("env not in", values, "env");
			return (Criteria) this;
		}

		public Criteria andEnvBetween(String value1, String value2) {
			addCriterion("env between", value1, value2, "env");
			return (Criteria) this;
		}

		public Criteria andEnvNotBetween(String value1, String value2) {
			addCriterion("env not between", value1, value2, "env");
			return (Criteria) this;
		}

		public Criteria andNamespaceIsNull() {
			addCriterion("namespace is null");
			return (Criteria) this;
		}

		public Criteria andNamespaceIsNotNull() {
			addCriterion("namespace is not null");
			return (Criteria) this;
		}

		public Criteria andNamespaceEqualTo(String value) {
			addCriterion("namespace =", value, "namespace");
			return (Criteria) this;
		}

		public Criteria andNamespaceNotEqualTo(String value) {
			addCriterion("namespace <>", value, "namespace");
			return (Criteria) this;
		}

		public Criteria andNamespaceGreaterThan(String value) {
			addCriterion("namespace >", value, "namespace");
			return (Criteria) this;
		}

		public Criteria andNamespaceGreaterThanOrEqualTo(String value) {
			addCriterion("namespace >=", value, "namespace");
			return (Criteria) this;
		}

		public Criteria andNamespaceLessThan(String value) {
			addCriterion("namespace <", value, "namespace");
			return (Criteria) this;
		}

		public Criteria andNamespaceLessThanOrEqualTo(String value) {
			addCriterion("namespace <=", value, "namespace");
			return (Criteria) this;
		}

		public Criteria andNamespaceLike(String value) {
			addCriterion("namespace like", value, "namespace");
			return (Criteria) this;
		}

		public Criteria andNamespaceNotLike(String value) {
			addCriterion("namespace not like", value, "namespace");
			return (Criteria) this;
		}

		public Criteria andNamespaceIn(List<String> values) {
			addCriterion("namespace in", values, "namespace");
			return (Criteria) this;
		}

		public Criteria andNamespaceNotIn(List<String> values) {
			addCriterion("namespace not in", values, "namespace");
			return (Criteria) this;
		}

		public Criteria andNamespaceBetween(String value1, String value2) {
			addCriterion("namespace between", value1, value2, "namespace");
			return (Criteria) this;
		}

		public Criteria andNamespaceNotBetween(String value1, String value2) {
			addCriterion("namespace not between", value1, value2, "namespace");
			return (Criteria) this;
		}

		public Criteria andUrlIsNull() {
			addCriterion("url is null");
			return (Criteria) this;
		}

		public Criteria andUrlIsNotNull() {
			addCriterion("url is not null");
			return (Criteria) this;
		}

		public Criteria andUrlEqualTo(String value) {
			addCriterion("url =", value, "url");
			return (Criteria) this;
		}

		public Criteria andUrlNotEqualTo(String value) {
			addCriterion("url <>", value, "url");
			return (Criteria) this;
		}

		public Criteria andUrlGreaterThan(String value) {
			addCriterion("url >", value, "url");
			return (Criteria) this;
		}

		public Criteria andUrlGreaterThanOrEqualTo(String value) {
			addCriterion("url >=", value, "url");
			return (Criteria) this;
		}

		public Criteria andUrlLessThan(String value) {
			addCriterion("url <", value, "url");
			return (Criteria) this;
		}

		public Criteria andUrlLessThanOrEqualTo(String value) {
			addCriterion("url <=", value, "url");
			return (Criteria) this;
		}

		public Criteria andUrlLike(String value) {
			addCriterion("url like", value, "url");
			return (Criteria) this;
		}

		public Criteria andUrlNotLike(String value) {
			addCriterion("url not like", value, "url");
			return (Criteria) this;
		}

		public Criteria andUrlIn(List<String> values) {
			addCriterion("url in", values, "url");
			return (Criteria) this;
		}

		public Criteria andUrlNotIn(List<String> values) {
			addCriterion("url not in", values, "url");
			return (Criteria) this;
		}

		public Criteria andUrlBetween(String value1, String value2) {
			addCriterion("url between", value1, value2, "url");
			return (Criteria) this;
		}

		public Criteria andUrlNotBetween(String value1, String value2) {
			addCriterion("url not between", value1, value2, "url");
			return (Criteria) this;
		}

		public Criteria andTokenIsNull() {
			addCriterion("token is null");
			return (Criteria) this;
		}

		public Criteria andTokenIsNotNull() {
			addCriterion("token is not null");
			return (Criteria) this;
		}

		public Criteria andTokenEqualTo(String value) {
			addCriterion("token =", value, "token");
			return (Criteria) this;
		}

		public Criteria andTokenNotEqualTo(String value) {
			addCriterion("token <>", value, "token");
			return (Criteria) this;
		}

		public Criteria andTokenGreaterThan(String value) {
			addCriterion("token >", value, "token");
			return (Criteria) this;
		}

		public Criteria andTokenGreaterThanOrEqualTo(String value) {
			addCriterion("token >=", value, "token");
			return (Criteria) this;
		}

		public Criteria andTokenLessThan(String value) {
			addCriterion("token <", value, "token");
			return (Criteria) this;
		}

		public Criteria andTokenLessThanOrEqualTo(String value) {
			addCriterion("token <=", value, "token");
			return (Criteria) this;
		}

		public Criteria andTokenLike(String value) {
			addCriterion("token like", value, "token");
			return (Criteria) this;
		}

		public Criteria andTokenNotLike(String value) {
			addCriterion("token not like", value, "token");
			return (Criteria) this;
		}

		public Criteria andTokenIn(List<String> values) {
			addCriterion("token in", values, "token");
			return (Criteria) this;
		}

		public Criteria andTokenNotIn(List<String> values) {
			addCriterion("token not in", values, "token");
			return (Criteria) this;
		}

		public Criteria andTokenBetween(String value1, String value2) {
			addCriterion("token between", value1, value2, "token");
			return (Criteria) this;
		}

		public Criteria andTokenNotBetween(String value1, String value2) {
			addCriterion("token not between", value1, value2, "token");
			return (Criteria) this;
		}

		public Criteria andUserIdIsNull() {
			addCriterion("user_id is null");
			return (Criteria) this;
		}

		public Criteria andUserIdIsNotNull() {
			addCriterion("user_id is not null");
			return (Criteria) this;
		}

		public Criteria andUserIdEqualTo(Long value) {
			addCriterion("user_id =", value, "userId");
			return (Criteria) this;
		}

		public Criteria andUserIdNotEqualTo(Long value) {
			addCriterion("user_id <>", value, "userId");
			return (Criteria) this;
		}

		public Criteria andUserIdGreaterThan(Long value) {
			addCriterion("user_id >", value, "userId");
			return (Criteria) this;
		}

		public Criteria andUserIdGreaterThanOrEqualTo(Long value) {
			addCriterion("user_id >=", value, "userId");
			return (Criteria) this;
		}

		public Criteria andUserIdLessThan(Long value) {
			addCriterion("user_id <", value, "userId");
			return (Criteria) this;
		}

		public Criteria andUserIdLessThanOrEqualTo(Long value) {
			addCriterion("user_id <=", value, "userId");
			return (Criteria) this;
		}

		public Criteria andUserIdIn(List<Long> values) {
			addCriterion("user_id in", values, "userId");
			return (Criteria) this;
		}

		public Criteria andUserIdNotIn(List<Long> values) {
			addCriterion("user_id not in", values, "userId");
			return (Criteria) this;
		}

		public Criteria andUserIdBetween(Long value1, Long value2) {
			addCriterion("user_id between", value1, value2, "userId");
			return (Criteria) this;
		}

		public Criteria andUserIdNotBetween(Long value1, Long value2) {
			addCriterion("user_id not between", value1, value2, "userId");
			return (Criteria) this;
		}

		public Criteria andHostIsNull() {
			addCriterion("host is null");
			return (Criteria) this;
		}

		public Criteria andHostIsNotNull() {
			addCriterion("host is not null");
			return (Criteria) this;
		}

		public Criteria andHostEqualTo(String value) {
			addCriterion("host =", value, "host");
			return (Criteria) this;
		}

		public Criteria andHostNotEqualTo(String value) {
			addCriterion("host <>", value, "host");
			return (Criteria) this;
		}

		public Criteria andHostGreaterThan(String value) {
			addCriterion("host >", value, "host");
			return (Criteria) this;
		}

		public Criteria andHostGreaterThanOrEqualTo(String value) {
			addCriterion("host >=", value, "host");
			return (Criteria) this;
		}

		public Criteria andHostLessThan(String value) {
			addCriterion("host <", value, "host");
			return (Criteria) this;
		}

		public Criteria andHostLessThanOrEqualTo(String value) {
			addCriterion("host <=", value, "host");
			return (Criteria) this;
		}

		public Criteria andHostLike(String value) {
			addCriterion("host like", value, "host");
			return (Criteria) this;
		}

		public Criteria andHostNotLike(String value) {
			addCriterion("host not like", value, "host");
			return (Criteria) this;
		}

		public Criteria andHostIn(List<String> values) {
			addCriterion("host in", values, "host");
			return (Criteria) this;
		}

		public Criteria andHostNotIn(List<String> values) {
			addCriterion("host not in", values, "host");
			return (Criteria) this;
		}

		public Criteria andHostBetween(String value1, String value2) {
			addCriterion("host between", value1, value2, "host");
			return (Criteria) this;
		}

		public Criteria andHostNotBetween(String value1, String value2) {
			addCriterion("host not between", value1, value2, "host");
			return (Criteria) this;
		}

		public Criteria andPortIsNull() {
			addCriterion("port is null");
			return (Criteria) this;
		}

		public Criteria andPortIsNotNull() {
			addCriterion("port is not null");
			return (Criteria) this;
		}

		public Criteria andPortEqualTo(Integer value) {
			addCriterion("port =", value, "port");
			return (Criteria) this;
		}

		public Criteria andPortNotEqualTo(Integer value) {
			addCriterion("port <>", value, "port");
			return (Criteria) this;
		}

		public Criteria andPortGreaterThan(Integer value) {
			addCriterion("port >", value, "port");
			return (Criteria) this;
		}

		public Criteria andPortGreaterThanOrEqualTo(Integer value) {
			addCriterion("port >=", value, "port");
			return (Criteria) this;
		}

		public Criteria andPortLessThan(Integer value) {
			addCriterion("port <", value, "port");
			return (Criteria) this;
		}

		public Criteria andPortLessThanOrEqualTo(Integer value) {
			addCriterion("port <=", value, "port");
			return (Criteria) this;
		}

		public Criteria andPortIn(List<Integer> values) {
			addCriterion("port in", values, "port");
			return (Criteria) this;
		}

		public Criteria andPortNotIn(List<Integer> values) {
			addCriterion("port not in", values, "port");
			return (Criteria) this;
		}

		public Criteria andPortBetween(Integer value1, Integer value2) {
			addCriterion("port between", value1, value2, "port");
			return (Criteria) this;
		}

		public Criteria andPortNotBetween(Integer value1, Integer value2) {
			addCriterion("port not between", value1, value2, "port");
			return (Criteria) this;
		}

		public Criteria andPwdDuckulaIsNull() {
			addCriterion("pwd_duckula is null");
			return (Criteria) this;
		}

		public Criteria andPwdDuckulaIsNotNull() {
			addCriterion("pwd_duckula is not null");
			return (Criteria) this;
		}

		public Criteria andPwdDuckulaEqualTo(String value) {
			addCriterion("pwd_duckula =", value, "pwdDuckula");
			return (Criteria) this;
		}

		public Criteria andPwdDuckulaNotEqualTo(String value) {
			addCriterion("pwd_duckula <>", value, "pwdDuckula");
			return (Criteria) this;
		}

		public Criteria andPwdDuckulaGreaterThan(String value) {
			addCriterion("pwd_duckula >", value, "pwdDuckula");
			return (Criteria) this;
		}

		public Criteria andPwdDuckulaGreaterThanOrEqualTo(String value) {
			addCriterion("pwd_duckula >=", value, "pwdDuckula");
			return (Criteria) this;
		}

		public Criteria andPwdDuckulaLessThan(String value) {
			addCriterion("pwd_duckula <", value, "pwdDuckula");
			return (Criteria) this;
		}

		public Criteria andPwdDuckulaLessThanOrEqualTo(String value) {
			addCriterion("pwd_duckula <=", value, "pwdDuckula");
			return (Criteria) this;
		}

		public Criteria andPwdDuckulaLike(String value) {
			addCriterion("pwd_duckula like", value, "pwdDuckula");
			return (Criteria) this;
		}

		public Criteria andPwdDuckulaNotLike(String value) {
			addCriterion("pwd_duckula not like", value, "pwdDuckula");
			return (Criteria) this;
		}

		public Criteria andPwdDuckulaIn(List<String> values) {
			addCriterion("pwd_duckula in", values, "pwdDuckula");
			return (Criteria) this;
		}

		public Criteria andPwdDuckulaNotIn(List<String> values) {
			addCriterion("pwd_duckula not in", values, "pwdDuckula");
			return (Criteria) this;
		}

		public Criteria andPwdDuckulaBetween(String value1, String value2) {
			addCriterion("pwd_duckula between", value1, value2, "pwdDuckula");
			return (Criteria) this;
		}

		public Criteria andPwdDuckulaNotBetween(String value1, String value2) {
			addCriterion("pwd_duckula not between", value1, value2, "pwdDuckula");
			return (Criteria) this;
		}

		public Criteria andHostsconfigIsNull() {
			addCriterion("hostsconfig is null");
			return (Criteria) this;
		}

		public Criteria andHostsconfigIsNotNull() {
			addCriterion("hostsconfig is not null");
			return (Criteria) this;
		}

		public Criteria andHostsconfigEqualTo(String value) {
			addCriterion("hostsconfig =", value, "hostsconfig");
			return (Criteria) this;
		}

		public Criteria andHostsconfigNotEqualTo(String value) {
			addCriterion("hostsconfig <>", value, "hostsconfig");
			return (Criteria) this;
		}

		public Criteria andHostsconfigGreaterThan(String value) {
			addCriterion("hostsconfig >", value, "hostsconfig");
			return (Criteria) this;
		}

		public Criteria andHostsconfigGreaterThanOrEqualTo(String value) {
			addCriterion("hostsconfig >=", value, "hostsconfig");
			return (Criteria) this;
		}

		public Criteria andHostsconfigLessThan(String value) {
			addCriterion("hostsconfig <", value, "hostsconfig");
			return (Criteria) this;
		}

		public Criteria andHostsconfigLessThanOrEqualTo(String value) {
			addCriterion("hostsconfig <=", value, "hostsconfig");
			return (Criteria) this;
		}

		public Criteria andHostsconfigLike(String value) {
			addCriterion("hostsconfig like", value, "hostsconfig");
			return (Criteria) this;
		}

		public Criteria andHostsconfigNotLike(String value) {
			addCriterion("hostsconfig not like", value, "hostsconfig");
			return (Criteria) this;
		}

		public Criteria andHostsconfigIn(List<String> values) {
			addCriterion("hostsconfig in", values, "hostsconfig");
			return (Criteria) this;
		}

		public Criteria andHostsconfigNotIn(List<String> values) {
			addCriterion("hostsconfig not in", values, "hostsconfig");
			return (Criteria) this;
		}

		public Criteria andHostsconfigBetween(String value1, String value2) {
			addCriterion("hostsconfig between", value1, value2, "hostsconfig");
			return (Criteria) this;
		}

		public Criteria andHostsconfigNotBetween(String value1, String value2) {
			addCriterion("hostsconfig not between", value1, value2, "hostsconfig");
			return (Criteria) this;
		}

		public Criteria andIsInitIsNull() {
			addCriterion("is_init is null");
			return (Criteria) this;
		}

		public Criteria andIsInitIsNotNull() {
			addCriterion("is_init is not null");
			return (Criteria) this;
		}

		public Criteria andIsInitEqualTo(String value) {
			addCriterion("is_init =", value, "isInit");
			return (Criteria) this;
		}

		public Criteria andIsInitNotEqualTo(String value) {
			addCriterion("is_init <>", value, "isInit");
			return (Criteria) this;
		}

		public Criteria andIsInitGreaterThan(String value) {
			addCriterion("is_init >", value, "isInit");
			return (Criteria) this;
		}

		public Criteria andIsInitGreaterThanOrEqualTo(String value) {
			addCriterion("is_init >=", value, "isInit");
			return (Criteria) this;
		}

		public Criteria andIsInitLessThan(String value) {
			addCriterion("is_init <", value, "isInit");
			return (Criteria) this;
		}

		public Criteria andIsInitLessThanOrEqualTo(String value) {
			addCriterion("is_init <=", value, "isInit");
			return (Criteria) this;
		}

		public Criteria andIsInitLike(String value) {
			addCriterion("is_init like", value, "isInit");
			return (Criteria) this;
		}

		public Criteria andIsInitNotLike(String value) {
			addCriterion("is_init not like", value, "isInit");
			return (Criteria) this;
		}

		public Criteria andIsInitIn(List<String> values) {
			addCriterion("is_init in", values, "isInit");
			return (Criteria) this;
		}

		public Criteria andIsInitNotIn(List<String> values) {
			addCriterion("is_init not in", values, "isInit");
			return (Criteria) this;
		}

		public Criteria andIsInitBetween(String value1, String value2) {
			addCriterion("is_init between", value1, value2, "isInit");
			return (Criteria) this;
		}

		public Criteria andIsInitNotBetween(String value1, String value2) {
			addCriterion("is_init not between", value1, value2, "isInit");
			return (Criteria) this;
		}

		public Criteria andDockerLoginIsNull() {
			addCriterion("docker_login is null");
			return (Criteria) this;
		}

		public Criteria andDockerLoginIsNotNull() {
			addCriterion("docker_login is not null");
			return (Criteria) this;
		}

		public Criteria andDockerLoginEqualTo(String value) {
			addCriterion("docker_login =", value, "dockerLogin");
			return (Criteria) this;
		}

		public Criteria andDockerLoginNotEqualTo(String value) {
			addCriterion("docker_login <>", value, "dockerLogin");
			return (Criteria) this;
		}

		public Criteria andDockerLoginGreaterThan(String value) {
			addCriterion("docker_login >", value, "dockerLogin");
			return (Criteria) this;
		}

		public Criteria andDockerLoginGreaterThanOrEqualTo(String value) {
			addCriterion("docker_login >=", value, "dockerLogin");
			return (Criteria) this;
		}

		public Criteria andDockerLoginLessThan(String value) {
			addCriterion("docker_login <", value, "dockerLogin");
			return (Criteria) this;
		}

		public Criteria andDockerLoginLessThanOrEqualTo(String value) {
			addCriterion("docker_login <=", value, "dockerLogin");
			return (Criteria) this;
		}

		public Criteria andDockerLoginLike(String value) {
			addCriterion("docker_login like", value, "dockerLogin");
			return (Criteria) this;
		}

		public Criteria andDockerLoginNotLike(String value) {
			addCriterion("docker_login not like", value, "dockerLogin");
			return (Criteria) this;
		}

		public Criteria andDockerLoginIn(List<String> values) {
			addCriterion("docker_login in", values, "dockerLogin");
			return (Criteria) this;
		}

		public Criteria andDockerLoginNotIn(List<String> values) {
			addCriterion("docker_login not in", values, "dockerLogin");
			return (Criteria) this;
		}

		public Criteria andDockerLoginBetween(String value1, String value2) {
			addCriterion("docker_login between", value1, value2, "dockerLogin");
			return (Criteria) this;
		}

		public Criteria andDockerLoginNotBetween(String value1, String value2) {
			addCriterion("docker_login not between", value1, value2, "dockerLogin");
			return (Criteria) this;
		}

		public Criteria andIsDefaultIsNull() {
			addCriterion("is_default is null");
			return (Criteria) this;
		}

		public Criteria andIsDefaultIsNotNull() {
			addCriterion("is_default is not null");
			return (Criteria) this;
		}

		public Criteria andIsDefaultEqualTo(String value) {
			addCriterion("is_default =", value, "isDefault");
			return (Criteria) this;
		}

		public Criteria andIsDefaultNotEqualTo(String value) {
			addCriterion("is_default <>", value, "isDefault");
			return (Criteria) this;
		}

		public Criteria andIsDefaultGreaterThan(String value) {
			addCriterion("is_default >", value, "isDefault");
			return (Criteria) this;
		}

		public Criteria andIsDefaultGreaterThanOrEqualTo(String value) {
			addCriterion("is_default >=", value, "isDefault");
			return (Criteria) this;
		}

		public Criteria andIsDefaultLessThan(String value) {
			addCriterion("is_default <", value, "isDefault");
			return (Criteria) this;
		}

		public Criteria andIsDefaultLessThanOrEqualTo(String value) {
			addCriterion("is_default <=", value, "isDefault");
			return (Criteria) this;
		}

		public Criteria andIsDefaultLike(String value) {
			addCriterion("is_default like", value, "isDefault");
			return (Criteria) this;
		}

		public Criteria andIsDefaultNotLike(String value) {
			addCriterion("is_default not like", value, "isDefault");
			return (Criteria) this;
		}

		public Criteria andIsDefaultIn(List<String> values) {
			addCriterion("is_default in", values, "isDefault");
			return (Criteria) this;
		}

		public Criteria andIsDefaultNotIn(List<String> values) {
			addCriterion("is_default not in", values, "isDefault");
			return (Criteria) this;
		}

		public Criteria andIsDefaultBetween(String value1, String value2) {
			addCriterion("is_default between", value1, value2, "isDefault");
			return (Criteria) this;
		}

		public Criteria andIsDefaultNotBetween(String value1, String value2) {
			addCriterion("is_default not between", value1, value2, "isDefault");
			return (Criteria) this;
		}

		public Criteria andVersionIdIsNull() {
			addCriterion("version_id is null");
			return (Criteria) this;
		}

		public Criteria andVersionIdIsNotNull() {
			addCriterion("version_id is not null");
			return (Criteria) this;
		}

		public Criteria andVersionIdEqualTo(Long value) {
			addCriterion("version_id =", value, "versionId");
			return (Criteria) this;
		}

		public Criteria andVersionIdNotEqualTo(Long value) {
			addCriterion("version_id <>", value, "versionId");
			return (Criteria) this;
		}

		public Criteria andVersionIdGreaterThan(Long value) {
			addCriterion("version_id >", value, "versionId");
			return (Criteria) this;
		}

		public Criteria andVersionIdGreaterThanOrEqualTo(Long value) {
			addCriterion("version_id >=", value, "versionId");
			return (Criteria) this;
		}

		public Criteria andVersionIdLessThan(Long value) {
			addCriterion("version_id <", value, "versionId");
			return (Criteria) this;
		}

		public Criteria andVersionIdLessThanOrEqualTo(Long value) {
			addCriterion("version_id <=", value, "versionId");
			return (Criteria) this;
		}

		public Criteria andVersionIdIn(List<Long> values) {
			addCriterion("version_id in", values, "versionId");
			return (Criteria) this;
		}

		public Criteria andVersionIdNotIn(List<Long> values) {
			addCriterion("version_id not in", values, "versionId");
			return (Criteria) this;
		}

		public Criteria andVersionIdBetween(Long value1, Long value2) {
			addCriterion("version_id between", value1, value2, "versionId");
			return (Criteria) this;
		}

		public Criteria andVersionIdNotBetween(Long value1, Long value2) {
			addCriterion("version_id not between", value1, value2, "versionId");
			return (Criteria) this;
		}

		public Criteria andRemarkIsNull() {
			addCriterion("remark is null");
			return (Criteria) this;
		}

		public Criteria andRemarkIsNotNull() {
			addCriterion("remark is not null");
			return (Criteria) this;
		}

		public Criteria andRemarkEqualTo(String value) {
			addCriterion("remark =", value, "remark");
			return (Criteria) this;
		}

		public Criteria andRemarkNotEqualTo(String value) {
			addCriterion("remark <>", value, "remark");
			return (Criteria) this;
		}

		public Criteria andRemarkGreaterThan(String value) {
			addCriterion("remark >", value, "remark");
			return (Criteria) this;
		}

		public Criteria andRemarkGreaterThanOrEqualTo(String value) {
			addCriterion("remark >=", value, "remark");
			return (Criteria) this;
		}

		public Criteria andRemarkLessThan(String value) {
			addCriterion("remark <", value, "remark");
			return (Criteria) this;
		}

		public Criteria andRemarkLessThanOrEqualTo(String value) {
			addCriterion("remark <=", value, "remark");
			return (Criteria) this;
		}

		public Criteria andRemarkLike(String value) {
			addCriterion("remark like", value, "remark");
			return (Criteria) this;
		}

		public Criteria andRemarkNotLike(String value) {
			addCriterion("remark not like", value, "remark");
			return (Criteria) this;
		}

		public Criteria andRemarkIn(List<String> values) {
			addCriterion("remark in", values, "remark");
			return (Criteria) this;
		}

		public Criteria andRemarkNotIn(List<String> values) {
			addCriterion("remark not in", values, "remark");
			return (Criteria) this;
		}

		public Criteria andRemarkBetween(String value1, String value2) {
			addCriterion("remark between", value1, value2, "remark");
			return (Criteria) this;
		}

		public Criteria andRemarkNotBetween(String value1, String value2) {
			addCriterion("remark not between", value1, value2, "remark");
			return (Criteria) this;
		}
	}

	/**
	 * This class was generated by MyBatis Generator. This class corresponds to the database table common_deploy
	 * @mbg.generated
	 */
	public static class Criterion {
		private String condition;
		private Object value;
		private Object secondValue;
		private boolean noValue;
		private boolean singleValue;
		private boolean betweenValue;
		private boolean listValue;
		private String typeHandler;

		public String getCondition() {
			return condition;
		}

		public Object getValue() {
			return value;
		}

		public Object getSecondValue() {
			return secondValue;
		}

		public boolean isNoValue() {
			return noValue;
		}

		public boolean isSingleValue() {
			return singleValue;
		}

		public boolean isBetweenValue() {
			return betweenValue;
		}

		public boolean isListValue() {
			return listValue;
		}

		public String getTypeHandler() {
			return typeHandler;
		}

		protected Criterion(String condition) {
			super();
			this.condition = condition;
			this.typeHandler = null;
			this.noValue = true;
		}

		protected Criterion(String condition, Object value, String typeHandler) {
			super();
			this.condition = condition;
			this.value = value;
			this.typeHandler = typeHandler;
			if (value instanceof List<?>) {
				this.listValue = true;
			} else {
				this.singleValue = true;
			}
		}

		protected Criterion(String condition, Object value) {
			this(condition, value, null);
		}

		protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
			super();
			this.condition = condition;
			this.value = value;
			this.secondValue = secondValue;
			this.typeHandler = typeHandler;
			this.betweenValue = true;
		}

		protected Criterion(String condition, Object value, Object secondValue) {
			this(condition, value, secondValue, null);
		}
	}

	/**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table common_deploy
     *
     * @mbg.generated do_not_delete_during_merge
     */
    public static class Criteria extends GeneratedCriteria {
        protected Criteria() {
            super();
        }
    }
}