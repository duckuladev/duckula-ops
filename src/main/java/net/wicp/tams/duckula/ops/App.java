package net.wicp.tams.duckula.ops;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import net.wicp.tams.app.duckula.controller.BusiTools;
import net.wicp.tams.common.Conf;
import net.wicp.tams.common.spring.autoconfig.annotation.EnableTams;

@SpringBootApplication
@ComponentScan(basePackages = { "net.wicp.tams.app.duckula.controller", "net.wicp.tams.duckula.ops" })
@ImportResource("classpath:beanRefContext.xml")
@MapperScan("net.wicp.tams.app.duckula.controller.dao")
@EnableTams
public class App extends SpringBootServletInitializer {
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(AppConfiguration.class);
	}

	public static void main(String[] args) throws Exception {
		// 在k8s里使用此目录，所以duckula支持2种配置，在bootstrap.yml文件里配置的，和下面这种方式设置的文件都支持。且下面这个配置文件优先级更高
		Conf.overProp("common.spring.autoconfig.property.path", "abs:/data/duckula-data/conf/duckula-ops.properties");
		Conf.overProp("common.spring.autoconfig.contextInit.duckulaops",
				"net.wicp.tams.app.duckula.controller.service.ContextInitDo");
		SpringApplication application = new SpringApplication(App.class);
		application.setApplicationContextClass(AnnotationConfigWebApplicationContext.class);
		BusiTools.printAscill();
		SpringApplication.run(App.class, args);
	}
}
