package net.wicp.tams.app.duckula.controller.config.k8s;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.KubeConfig;
import net.wicp.tams.common.apiext.StringUtil;
import net.wicp.tams.common.thread.threadlocal.ObjectCreator;

public class ApiClientCreator implements ObjectCreator<ApiClient> {
	private final String config;

	public ApiClientCreator(String config) {
		this.config = config;
	}

	public ApiClientCreator() {
		this.config = null;
	}

	@Override
	public ApiClient createObject() {
		ApiClient apiClient;
		try {
			// apiClient = Config.defaultClient();
			if (StringUtil.isNotNull(config)) {
				KubeConfig config = userKubeConfig(this.config);
				apiClient = Config.fromConfig(config);
			} else {
				apiClient = Config.defaultClient();// 默认的配置
			}

		} catch (IOException e) {
			throw new RuntimeException("create apiClient error", e);
		}
		return apiClient;
	}

	public KubeConfig userKubeConfig(String yamlStr) {
		Yaml yaml = new Yaml(new SafeConstructor());
		Object config = yaml.load(yamlStr);
		Map<String, Object> configMap = (Map<String, Object>) config;
		String currentContext = (String) configMap.get("current-context");
		ArrayList<Object> contexts = (ArrayList<Object>) configMap.get("contexts");
		ArrayList<Object> clusters = (ArrayList<Object>) configMap.get("clusters");
		ArrayList<Object> users = (ArrayList<Object>) configMap.get("users");
		Object preferences = configMap.get("preferences");
		KubeConfig kubeConfig = new KubeConfig(contexts, clusters, users);
		kubeConfig.setContext(currentContext);
		kubeConfig.setPreferences(preferences);
		return kubeConfig;
	}

}