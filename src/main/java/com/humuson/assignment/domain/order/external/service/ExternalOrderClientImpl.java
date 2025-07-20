package com.humuson.assignment.domain.order.external.service;

import com.humuson.assignment.domain.order.external.exception.OrderSyncException;
import com.humuson.assignment.domain.order.dto.OrderDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

// 외부 시스템 연결 및 통신
@Service
@Log4j2
public class ExternalOrderClientImpl implements ExternalOrderClient {

    private final WebClient webClient;

    public ExternalOrderClientImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    // 외부 시스템 주문 요청 GET
    @Override
    public List<OrderDTO> fetchOrders(String url) {

        try {
            List<OrderDTO> orders = webClient.get()
                    .uri(url)
                    .exchangeToFlux(response -> {
                        // 상태 코드
                        HttpStatusCode statusCode = response.statusCode();

                        // 성공
                        if (statusCode.is2xxSuccessful()) {
                            return response.bodyToFlux(OrderDTO.class);

                        // 실패
                        } else {
                            return response.bodyToMono(String.class)
                                    .defaultIfEmpty("")
                                    .flatMapMany(body ->
                                            Flux.error(new OrderSyncException("[연동][주문 요청 실패] - code : " + statusCode.value() +
                                                                              " / body : " + body)));
                        }
                    })
                    .collectList()
                    .block();

            log.info("[연동][주문 요청 성공] - count : {} / url : {}", orders != null ? orders.size() : 0,
                                                                            url);
            return orders;

        } catch (WebClientException e) {
            log.error("[연동][주문 요청 실패] - url : {} / error : {}", url,
                                                                             e.getMessage());
            throw new OrderSyncException("[연동][주문 요청 실패] - url : " + url +
                                         " / error : " + e.getMessage());
        }
    }

    // 외부 시스템 주문 반환 POST
    @Override
    public void sendOrders(String url, List<OrderDTO> orders) {
        try {
            webClient.post()
                    .uri(url)
                    .bodyValue(orders)
                    .exchangeToMono(response -> {
                        HttpStatusCode statusCode = response.statusCode();

                        // 성공
                        if (statusCode.is2xxSuccessful()) {
                            log.info("[연동][주문 반환 성공] - code : {}", statusCode.value());
                            
                            return response.bodyToMono(String.class);

                        // 실패
                        } else {
                            log.warn("[연동][주문 반환 실패] - code : {}", statusCode.value());
                            
                            return Mono.error(
                                    new OrderSyncException("[연동][주문 반환 실패] - code : " + statusCode.value()));
                        }
                    })
                    .block();

            log.info("[연동][주문 반환 성공] - count: {} / url : {}", orders.size(),
                                                                           url);

        } catch (WebClientException e) {
            log.error("[연동][주문 반환 실패] - count : {} / url : {} / error : {}", orders.size(),
                                                                                          url,
                                                                                          e.getMessage());

            throw new OrderSyncException("[연동][주문 반환 실패] - count : " + orders.size() +
                                         " / url : " + url +
                                         " / error : " + e.getMessage());
        }
    }
}
