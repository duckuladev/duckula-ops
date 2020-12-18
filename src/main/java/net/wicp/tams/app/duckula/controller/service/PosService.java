package net.wicp.tams.app.duckula.controller.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.wicp.tams.app.duckula.controller.bean.models.CommonCheckpoint;
import net.wicp.tams.app.duckula.controller.config.constant.CheckpointType;
import net.wicp.tams.common.apiext.jdbc.JdbcAssit;
import net.wicp.tams.common.binlog.alone.ListenerConf.Position;
import net.wicp.tams.common.jdbc.DruidAssit;

/**
 * 位点服务
 * 
 * @author Andy.zhou
 *
 */
@Service
@Slf4j
public class PosService {
	/**
	 * 查询位点
	 * 
	 * @param checkpointType
	 * @return
	 */
	public Position selectPosition(CommonCheckpoint checkpoint, String taskName, Integer clientId) {
		if (checkpoint == null || clientId == null) {
			return null;
		}
		CheckpointType checkpointType = CheckpointType.valueOf(checkpoint.getCheckpointType());
		Position retpos = null;
		switch (checkpointType) {
		case Memory:
		case H2db:
			break;
		case Mysql:
			retpos = selectMaxPositionByMysql(checkpoint, clientId);
			break;
		//case Zookeeper:
			// String taskNameTrue = CommandType.task.formateTaskName(taskName);
			// TODO
			//break;
		default:
			break;
		}
		return retpos;
	}

	/***
	 * mysql类型的
	 * 
	 * @param checkpoint
	 * @return
	 * @throws SQLException
	 */
	public List<Position> selectPositionByMysql(CommonCheckpoint checkpoint, Integer clientId, Long lastTime,
			int maxSize) {
		Properties props = new Properties();
		props.put("host", checkpoint.getHost());
		props.put("port", checkpoint.getPort());
		props.put("defaultdb", checkpoint.getDefaultDb());
		props.put("username", checkpoint.getUsername());
		props.put("password", checkpoint.getPassword());
		DataSource dataSourceNoConf = DruidAssit.getDataSourceNoConf(checkpoint.getName(), props);
		List<Position> retlist = new ArrayList<Position>();
		try {
			PreparedStatement preStatement = dataSourceNoConf.getConnection().prepareStatement(
					"select * from position  where serverIp=? and clintId=? and time<=?   order by time desc limit 0,"
							+ (maxSize <= 0 ? 1 : maxSize));
			JdbcAssit.setPreParam(preStatement, checkpoint.getHost(), clientId, lastTime);
			ResultSet executeQuery = preStatement.executeQuery();
			if (executeQuery.next()) {
				Position.Builder builder = Position.newBuilder();
				builder.setFileName(executeQuery.getString("fileName"));
				builder.setGtids(executeQuery.getString("gtids"));
				builder.setPos(executeQuery.getLong("pos"));
				builder.setMasterServerId(executeQuery.getLong("masterServerId"));
				builder.setTime(executeQuery.getLong("time"));
				builder.setTimeStr(executeQuery.getString("timeStr"));
				builder.setServerIp(executeQuery.getString("serverIp"));
				builder.setClintId(executeQuery.getInt("clintId"));
				retlist.add(builder.build());
			}
		} catch (SQLException e) {
			log.error("查询位点失败", e);
		}
		return retlist;
	}

	public Position selectMaxPositionByMysql(CommonCheckpoint checkpoint, Integer clientId) {
		List<Position> selectList = selectPositionByMysql(checkpoint, clientId, Long.MAX_VALUE, 1);
		return CollectionUtils.isEmpty(selectList) ? null : selectList.get(0);
	}

}
