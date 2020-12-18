package net.wicp.tams.app.duckula.controller.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.io.ByteStreams;

import io.kubernetes.client.PodLogs;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1Job;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1Status;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import net.wicp.tams.app.duckula.controller.bean.models.CommonDeploy;
import net.wicp.tams.app.duckula.controller.config.ConfigItem;
import net.wicp.tams.app.duckula.controller.config.ExceptDuckula;
import net.wicp.tams.app.duckula.controller.config.constant.DeployType;
import net.wicp.tams.app.duckula.controller.config.k8s.ApiClientManager;
import net.wicp.tams.app.duckula.controller.dao.CommonDeployMapper;
import net.wicp.tams.common.apiext.FreemarkUtil;
import net.wicp.tams.common.apiext.IOUtil;
import net.wicp.tams.common.apiext.StringUtil;
import net.wicp.tams.common.exception.ExceptAll;
import net.wicp.tams.common.exception.ProjectExceptionRuntime;

/***
 * k8s接口全部在此定义
 * 
 * @author Andy.zhou
 *
 */
@Service
@Slf4j
public class K8sService {

	@Autowired
	private CommonDeployMapper commonDeployMapper;

	public V1Job deployDump(Long deployid, Map<String, Object> params) {
		CommonDeploy commonDeploy = commonDeployMapper.selectById(deployid);
		ApiClient apiClient = getApiClient(commonDeploy);
		try {
			String context = IOUtil.slurp(IOUtil.fileToInputStream("/deploy/k8s/job.yaml", K8sService.class));
			String result = FreemarkUtil.getInst().doProcessByTemp(context, params);
			V1Job yamlSvc = (V1Job) Yaml.load(result);
			BatchV1Api batchV1Api = new BatchV1Api(apiClient);
			V1Job v1Job = batchV1Api.createNamespacedJob(commonDeploy.getNamespace(), yamlSvc, "true", null, null);
			return v1Job;
		} catch (ApiException e) {
			if ("Conflict".equals(e.getMessage())) {
				throw new ProjectExceptionRuntime(ExceptAll.k8s_deploy_conflict);
			} else {
				throw new ProjectExceptionRuntime(ExceptAll.k8s_api_other, e.getMessage());
			}
		} catch (Exception e) {
			log.error("部署dump失败", e);
			throw new ProjectExceptionRuntime(ExceptDuckula.duckula_deploy_excetion, e.getMessage());
		}
	}

	public V1Deployment deployTask(Long deployid, Map<String, Object> params) {
		CommonDeploy commonDeploy = commonDeployMapper.selectById(deployid);
		ApiClient apiClient = getApiClient(commonDeploy);
		try {
			String context = IOUtil.slurp(IOUtil.fileToInputStream("/deploy/k8s/deployment.yaml", K8sService.class));
			String result = FreemarkUtil.getInst().doProcessByTemp(context, params);
			V1Deployment yamlSvc = (V1Deployment) Yaml.load(result);
			AppsV1Api appsV1Api = new AppsV1Api(apiClient);
			V1Deployment v1Deployment = appsV1Api.createNamespacedDeployment(commonDeploy.getNamespace(), yamlSvc,
					"true", null, null);
			return v1Deployment;
		} catch (ApiException e) {
			if ("Conflict".equals(e.getMessage())) {
				throw new ProjectExceptionRuntime(ExceptAll.k8s_deploy_conflict);
			} else {
				throw new ProjectExceptionRuntime(ExceptAll.k8s_api_other, e.getMessage());
			}
		} catch (Exception e) {
			log.error("部署task失败", e);
			throw new ProjectExceptionRuntime(ExceptDuckula.duckula_deploy_excetion, e.getMessage());
		}
	}
	
	public V1Status stopTask(Long deployid, String name) {
		CommonDeploy commonDeploy = commonDeployMapper.selectById(deployid);
		ApiClient apiClient = getApiClient(commonDeploy);
		try {
			AppsV1Api appsV1Api = new AppsV1Api(apiClient);
			V1Status delStatus = appsV1Api.deleteNamespacedDeployment(name, commonDeploy.getNamespace(), null, null,
					null, null, null, null);
			return delStatus;
		} catch (ApiException e) {
			if (e.getCode() == 404) {
				throw new ProjectExceptionRuntime(ExceptAll.k8s_api_notfind, "要停止的资源没找到");
			} else {
				throw new ProjectExceptionRuntime(ExceptAll.k8s_api_other, e.getMessage());
			}
		} catch (Exception e) {
			log.error("停止dump失败", e);
			throw new ProjectExceptionRuntime(ExceptDuckula.duckula_deploy_excetion, e.getMessage());
		}
	}
	
	//删除job不会删除pod
	public V1Status stopDump(Long deployid, String name) {
		CommonDeploy commonDeploy = commonDeployMapper.selectById(deployid);
		ApiClient apiClient = getApiClient(commonDeploy);
		V1Status delStatus=null;
		try {
			BatchV1Api batchV1Api = new BatchV1Api(apiClient);
			delStatus = batchV1Api.deleteNamespacedJob(name, commonDeploy.getNamespace(), null, null,
					null, null, null, null);
			return delStatus;
		} catch (ApiException e) {
			//if (e.getCode() == 404) {
			//	throw new ProjectExceptionRuntime(ExceptAll.k8s_api_notfind, "要停止的资源没找到");
			//} else {
			//	throw new ProjectExceptionRuntime(ExceptAll.k8s_api_other, e.getMessage());
			//}
		} catch (Exception e) {
			//com.google.gson.JsonSyntaxException是正确的
			//log.error("停止dump失败", e);
			//throw new ProjectExceptionRuntime(ExceptDuckula.duckula_deploy_excetion, e.getMessage());
		}
		//删除相关的pod
		CoreV1Api coreV1Api = new CoreV1Api(apiClient);
		V1Pod selectPod = selectPod(deployid,name);
		String name2 = selectPod.getMetadata().getName();
		try {
			coreV1Api.deleteNamespacedPod(name2, commonDeploy.getNamespace(), null, null, null, null, null, null);
		} catch (Exception e) {
			//log.error("删除job相关的pod失败",e);
			//throw new ProjectExceptionRuntime(ExceptDuckula.duckula_deploy_excetion, e.getMessage());
		}
		return delStatus;		
	}
	

	public V1Pod selectPod(Long deployid, String configName) {
		CommonDeploy commonDeploy = commonDeployMapper.selectById(deployid);
		ApiClient apiClient = getApiClient(commonDeploy);
		try {
			CoreV1Api coreApi = new CoreV1Api(apiClient);// appname=
			// V1Pod v1Pod = coreApi.readNamespacedPod(configName,
			// commonDeploy.getNamespace(), null, null, null);
			List<V1Pod> items = coreApi.listNamespacedPod(commonDeploy.getNamespace(), null, false, null, null,
					String.format("appname=%s", configName), null, null, null, null).getItems();
			if (CollectionUtils.isEmpty(items)) {
				return null;
			}
			return items.get(0);
		} catch (ApiException e) {
			throw new ProjectExceptionRuntime(ExceptAll.k8s_api_other, e.getMessage());
		} catch (Exception e) {
			log.error("部署task失败", e);
			throw new ProjectExceptionRuntime(ExceptDuckula.duckula_deploy_excetion, e.getMessage());
		}
	}
	
	
	public BufferedReader viewLog(Long deployid, String configName) {
		CommonDeploy commonDeploy = commonDeployMapper.selectById(deployid);
		ApiClient apiClient = getApiClient(commonDeploy);
		V1Pod v1Pod = selectPod(deployid,configName);
		 PodLogs logs = new PodLogs(apiClient);
		 apiClient.setReadTimeout(300000);//5分钟
		 try {			
			InputStream is = logs.streamNamespacedPodLog(v1Pod);
			BufferedReader reader= new BufferedReader(new InputStreamReader(is));
			return reader;
			// ByteStreams.copy(is, System.out);
			// String line;
			 //while((line = reader.readLine()) != null) {
	          //      System.out.println("====="+line);
	        // }
			 //System.out.println(2222);
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return null;
	}
	
	
	
	public V1Deployment selectDeployment(Long deployid, String configName) {
		CommonDeploy commonDeploy = commonDeployMapper.selectById(deployid);
		ApiClient apiClient = getApiClient(commonDeploy);
		try {
			AppsV1Api appsV1Api = new AppsV1Api(apiClient);
			V1Deployment deployment = appsV1Api.readNamespacedDeployment(configName, commonDeploy.getNamespace(), null, null, null) ;
			return deployment;
		} catch (ApiException e) {
			throw new ProjectExceptionRuntime(ExceptAll.k8s_api_other, e.getMessage());
		} catch (Exception e) {
			log.error("部署task失败", e);
			throw new ProjectExceptionRuntime(ExceptDuckula.duckula_deploy_excetion, e.getMessage());
		}
	}
	

	/**
	 * 部署configmap
	 * 
	 * @param deployid      部署id
	 * @param configmapName configmap名称
	 * @param params        key：文件名 value：文件内容，不需要考虑空格,暂时只有一个
	 * @return
	 */
	public V1ConfigMap deployConfigmap(Long deployid, String configMapName, Map<String, Object> contextParams) {
		CommonDeploy commonDeploy = commonDeployMapper.selectById(deployid);
		String configmapStr = DeployType.formateConfig(DeployType.valueOf(commonDeploy.getDeploy()), contextParams);		
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConfigItem.configmap_name, configMapName);
		// 保证第一行为非空字符，为了避免yaml语法检查失败
		if (!configmapStr.startsWith("    ")) {
			configmapStr = configmapStr.replace("\n", "\n    ");// 加4个空格用于完成map格式
		} else {
			configmapStr = configmapStr.substring(4);
		}
		params.put(ConfigItem.configmap_filecontext, configmapStr);
		
		ApiClient apiClient = getApiClient(commonDeploy);
		try {
			String context = IOUtil.slurp(IOUtil.fileToInputStream("/deploy/k8s/configMap.yaml", K8sService.class));
			String result = FreemarkUtil.getInst().doProcessByTemp(context, params);
			V1ConfigMap yamlSvc = (V1ConfigMap) Yaml.load(result);
			CoreV1Api coreV1Api = new CoreV1Api(apiClient);
			V1ConfigMap v1ConfigMap = coreV1Api.createNamespacedConfigMap(commonDeploy.getNamespace(), yamlSvc, "true",
					null, null);
			return v1ConfigMap;
		} catch (Exception e) {
			log.error("创建V1ConfigMap失败", e);
			throw new RuntimeException(e);
		}
	}
	
	public String OriConfig(Long deployid, Map<String, Object> contextParams) {
		CommonDeploy commonDeploy = commonDeployMapper.selectById(deployid);
		String configmapStr = DeployType.formateConfig(DeployType.valueOf(commonDeploy.getDeploy()), contextParams);
		return  configmapStr;
	}
	
	public V1Status deleteConfigmap(Long deployid, String configMapName) {
		CommonDeploy commonDeploy = commonDeployMapper.selectById(deployid);
		ApiClient apiClient = getApiClient(commonDeploy);
		try {
			CoreV1Api coreV1Api = new CoreV1Api(apiClient);
			V1Status deleteNamespacedConfigMap = coreV1Api.deleteNamespacedConfigMap(configMapName, commonDeploy.getNamespace(), null, null, null, null, null, null);
			return deleteNamespacedConfigMap;
		} catch (ApiException e) {
			if (e.getCode() == 404) {
				throw new ProjectExceptionRuntime(ExceptAll.k8s_api_notfind, "要停止的资源没找到");
			} else {
				throw new ProjectExceptionRuntime(ExceptAll.k8s_api_other, e.getMessage());
			}
		} catch (Exception e) {
			log.error("删除configmap失败", e);
			throw new ProjectExceptionRuntime(ExceptDuckula.duckula_deploy_excetion, e.getMessage());
		}
	}

	/***
	 * 查询ConfigMap
	 * 
	 * @param deployid
	 * @return
	 */
	public V1ConfigMap selectConfigMap(Long deployid, String configName) {
		CommonDeploy commonDeploy = commonDeployMapper.selectById(deployid);
		ApiClient apiClient = getApiClient(commonDeploy);
		CoreV1Api coreV1Api = new CoreV1Api(apiClient);
		String pretty = null;
		Boolean exact = null;
		Boolean export = null;
		try {
			V1ConfigMap v1ConfigMap = coreV1Api.readNamespacedConfigMap(configName, commonDeploy.getNamespace(), pretty,
					exact, export);
			return v1ConfigMap;
		} catch (Exception e) {
			log.error("查询V1ConfigMap失败", e);
			throw new RuntimeException(e);
		}
	}

	public ApiClient getApiClient(Long deployid) {
		CommonDeploy commonDeploy = commonDeployMapper.selectById(deployid);
		return getApiClient(commonDeploy);
	}

	public ApiClient getApiClient(CommonDeploy commonDeploy) {
		if (commonDeploy == null || StringUtil.isNull(commonDeploy.getConfig())) {
			throw new RuntimeException("没有得到k8s相关配置");
		}
		ApiClient apiClient = ApiClientManager.getApiClient(String.valueOf(commonDeploy.getId()),
				commonDeploy.getConfig());
		return apiClient;
	}
}
