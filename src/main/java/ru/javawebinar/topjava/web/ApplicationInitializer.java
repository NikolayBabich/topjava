package ru.javawebinar.topjava.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.XmlWebApplicationContext;
import ru.javawebinar.topjava.Profiles;

import javax.servlet.ServletContext;

@Configuration
public class ApplicationInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) {
        // https://www.baeldung.com/spring-web-contexts#root-servlet3-xml
        ConfigurableWebApplicationContext rootContext = new XmlWebApplicationContext();
        rootContext.getEnvironment().setActiveProfiles(Profiles.getActiveDbProfile(),
                                                       Profiles.REPOSITORY_IMPLEMENTATION);
        rootContext.setConfigLocations("classpath:spring/spring-app.xml", "classpath:spring/spring-db.xml");
        servletContext.addListener(new ContextLoaderListener(rootContext));
    }
}
