package com.nhom10.doanmonhoc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class ResourceConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String path = "file:///" + System.getProperty("user.dir") + "/src/main/resources/images/";
        registry.addResourceHandler("/contents/images/**")
                .addResourceLocations(path);
    }
}