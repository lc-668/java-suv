package com.txkj.tool;

import com.txkj.tool.service.ResetService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {FreeMarkerAutoConfiguration.class,})
@EnableScheduling
/*@ComponentScan*/
public class SuvApp {

    public static void main(String[] args) {
        SpringApplication.run(SuvApp.class, args);
        System.out.println("http://localhost:9006/");
    }

}
