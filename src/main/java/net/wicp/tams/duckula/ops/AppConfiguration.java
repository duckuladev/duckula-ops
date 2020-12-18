package net.wicp.tams.duckula.ops;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.SessionTrackingMode;

import org.apache.tapestry5.spring.TapestrySpringFilter;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
@ComponentScan({ "net.wicp.tams.duckula.ops" })
public class AppConfiguration
{

    @Bean
    public ServletContextInitializer initializer()
    {
        return new ServletContextInitializer()
        {
            @Override
            public void onStartup(ServletContext servletContext) throws ServletException {
                servletContext.setInitParameter("tapestry.app-package", "net.wicp.tams.duckula.ops");
                servletContext.setInitParameter("tapestry.development-modules", "net.wicp.tams.duckula.ops.services.DevelopmentModule");
                servletContext.setInitParameter("tapestry.qa-modules", "com.foo.services.QaModule");
                servletContext.setInitParameter("tapestry.use-external-spring-context", "true");
               // servletContext.addFilter("app", TapestryFilter.class).addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR), false, "/*");
                servletContext.addFilter("app", TapestrySpringFilter.class).addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR), false, "/*");
                servletContext.setSessionTrackingModes(EnumSet.of(SessionTrackingMode.COOKIE));
            }
        };
    }

    @Bean
    public ConfigurableServletWebServerFactory webServerFactory()
    {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/error404"));
        return factory;
    }
}
