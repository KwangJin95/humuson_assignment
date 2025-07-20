package com.humuson.assignment.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.HashMap;
import java.util.Map;

@Configuration
@PropertySource("classpath:external-system-info.properties")
@ConfigurationProperties(prefix = "external")
@Getter
@Setter
public class ExternalSystemProperties {
    
    // 외부 시스템 설정값 Map
    private Map<String, SystemDetail> systems = new HashMap<>();

    private SystemDetail defaultConfig = new SystemDetail();

    @Getter
    @Setter
    public static class SystemDetail {

        // 외부 시스템 기본 엔드포인트
        private String baseUrl;

        // 주문 요청 엔드포인트 경로
        private String fetchPath;

        // 주문 반환 엔드포인트 경로
        private String sendPath;

        // 전체 API 엔드포인트 반환
        public String getFetchUrl() {
            return baseUrl + fetchPath;
        }
        public String getSendUrl() {
            return baseUrl + sendPath;
        }
    }

}