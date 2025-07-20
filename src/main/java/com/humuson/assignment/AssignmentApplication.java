package com.humuson.assignment;

import com.humuson.assignment.config.ExternalSystemProperties;
import com.humuson.assignment.domain.order.external.service.ExternalOrderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(ExternalSystemProperties.class)
@Log4j2
public class AssignmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssignmentApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(ExternalOrderService externalOrderService) {
        return args -> {
            try {
                // 주문 요청 ( 정상 : 3개 / 유효성 오류 케이스 : 1개)
                externalOrderService.fetchAndSaveOrders("systemLocal");

                // 주문 반환
                externalOrderService.sendOrdersToExternal("systemLocal");
            } catch (Exception e) {
                log.error("[외부 시스템 주문 연동 중 오류 발생] - error : {}", e.getMessage());
            }

            try {
                // 주문 요청 (네트워크 오류)
                externalOrderService.fetchAndSaveOrders("systemA");
            } catch (Exception e) {
                log.error("[systemA 연동 실패] - {}", e.getMessage());
            }
            
            try {
                // 주문 요청 (존재하지 않는 시스템명)
                externalOrderService.fetchAndSaveOrders("invalidSystem");
            } catch (Exception e) {
                log.error("[미존재 연동 실패] - {}", e.getMessage());
            }

        };
    }
}
