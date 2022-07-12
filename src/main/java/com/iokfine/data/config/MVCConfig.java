package com.iokfine.data.config;

import com.iokfine.data.utils.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@Slf4j
public class MVCConfig implements WebMvcConfigurer {
    @Value("${front-location:}")
    private String frontLocation;

    /**
     * 配置静态资源
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String frontRoot;
        if (StringUtils.hasText(frontLocation)) {
            frontRoot = StringUtils.trimTrailingCharacter(frontLocation, '/');
        } else {
            String homeDir = SystemUtil.getBinPath();
            if ("/".equals(homeDir)) {
                homeDir = "";
            }
            frontRoot = homeDir + "/view";
        }
        log.info("Front Resource Directory：{}", frontRoot);
        String frontLocation = "file:" + frontRoot;
        registry.addResourceHandler("/index.html").addResourceLocations(frontLocation + "/index.html");
        registry.addResourceHandler("/favicon.ico").addResourceLocations(frontLocation + "/favicon.ico");
        registry.addResourceHandler("/static/**").addResourceLocations(frontLocation + "/static/");
        registry.addResourceHandler("/aty/**").addResourceLocations(frontLocation + "/aty/");
    }

}
