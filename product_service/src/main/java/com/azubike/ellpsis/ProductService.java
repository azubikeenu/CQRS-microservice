package com.azubike.ellpsis;

import com.azubike.ellpsis.config.AxonXstreamConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableEurekaClient
@Import({AxonXstreamConfig.class})
public class ProductService {
    public static void main(String[] args) {
        SpringApplication.run(ProductService.class, args);
    }


}
