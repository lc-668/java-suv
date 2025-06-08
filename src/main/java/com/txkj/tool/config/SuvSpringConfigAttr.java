package com.txkj.tool.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "suv.config")
@Data
public class SuvSpringConfigAttr {

    private List<String> path;
}