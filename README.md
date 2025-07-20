# 주문 데이터 연동 인터페이스 시스템

휴머스온 채용 과제를 위해 구현한 외부 시스템과의 주문 데이터 연동 인터페이스입니다.

## 🛠 기술 스택

- **Java 17** (OpenJDK)
- **Spring Boot 3.5.3**
  - **Spring Web**: RESTful API 구현
  - **Spring WebFlux**: WebClient를 통한 비동기 HTTP 통신
  - **Spring Validation**: Jakarta Validation 기반 데이터 유효성 검증
- **Lombok**: 코드 간소화 (Getter, Setter, Builder 등)
- **Log4j2**: 고성능 로깅 (Logback 대신 사용)
- **Gradle 8.x**: 빌드 도구

## 📁 프로젝트 구조

```
src/
├─main/
│  ├─java/com/humuson/assignment/
│  │  │  AssignmentApplication.java              # 메인 애플리케이션 & 테스트 실행
│  │  ├─config/
│  │  │      ExternalSystemProperties.java       # 외부 시스템 설정 관리
│  │  │      WebClientConfig.java               # HTTP 클라이언트 설정
│  │  └─domain/order/
│  │      ├─controller/mock/
│  │      │      MockExternalOrderController.java # 외부 시스템 Mock API
│  │      ├─dto/
│  │      │      OrderDTO.java                   # 주문 데이터 전송 객체
│  │      │      OrderStatus.java               # 주문 상태 열거형
│  │      ├─external/
│  │      │  ├─exception/
│  │      │  │      OrderSyncException.java     # 연동 예외 클래스
│  │      │  └─service/
│  │      │          ExternalOrderClient.java    # 외부 시스템 통신 인터페이스
│  │      │          ExternalOrderClientImpl.java # HTTP 통신 구현체
│  │      │          ExternalOrderService.java   # 외부 연동 비즈니스 로직 인터페이스
│  │      │          ExternalOrderServiceImpl.java # 외부 연동 비즈니스 로직 구현
│  │      ├─repository/
│  │      │      InMemoryOrderRepository.java    # 메모리 기반 주문 저장소
│  │      │      OrderRepository.java            # 주문 데이터 접근 인터페이스
│  │      └─service/
│  │              OrderService.java               # 주문 비즈니스 로직 인터페이스
│  │              OrderServiceImpl.java           # 주문 비즈니스 로직 구현
│  └─resources/
│      │  application.properties                 # Spring Boot 설정
│      │  external-system-info.properties       # 외부 시스템 연동 정보
│      ├─static/                                # 정적 리소스
│      └─templates/                             # 템플릿 파일
└─test/java/com/humuson/assignment/
        AssignmentApplicationTests.java          # Spring Boot 테스트
```

## 🏗 시스템 아키텍처

### 클래스 다이어그램 주요 컴포넌트

#### 1. **외부 시스템 연동 계층**
- `ExternalOrderService`: 외부 시스템과의 연동 비즈니스 로직 담당
- `ExternalOrderClient`: HTTP 통신을 통한 외부 시스템 연결
- `ExternalSystemProperties`: 다중 외부 시스템 설정 관리

#### 2. **내부 시스템 비즈니스 계층**
- `OrderService`: 주문 관련 비즈니스 로직
- `OrderRepository`: 데이터 접근 추상화

#### 3. **데이터 계층**
- `InMemoryOrderRepository`: 메모리 기반 주문 데이터 저장
- `OrderDTO`: 주문 데이터 전송 객체 (유효성 검증 포함)

### 설계 특징

#### ✨ **확장성 고려**
- **인터페이스 기반 설계**: 모든 주요 컴포넌트가 인터페이스로 추상화되어 구현체 교체 용이
- **다중 외부 시스템 지원**: Properties를 통해 여러 외부 시스템 동시 관리
- **전략 패턴 적용**: Repository, Client 구현체를 쉽게 교체 가능

#### 🔒 **안정성 확보**
- **포괄적 예외 처리**: 네트워크 오류, 데이터 형식 오류, 유효성 검증 실패 등
- **타임아웃 설정**: 연결/응답 타임아웃 (3초) 설정으로 무한 대기 방지
- **동시성 안전**: `ConcurrentHashMap` 사용으로 멀티스레드 환경 대응

## 🚀 실행 방법

### 사전 요구사항
- **Java 17 이상** 설치 필요
- **Git** 설치 필요

### 1. 프로젝트 빌드 및 실행
```bash
# 프로젝트 클론
git clone [your-repository-url]
cd assignment

# 권한 부여 (Linux/Mac)
chmod +x gradlew

# 프로젝트 빌드 및 실행
./gradlew bootRun

# 또는 JAR 빌드 후 실행
./gradlew build
java -jar build/libs/assignment-0.0.1-SNAPSHOT.jar
```

### Windows에서 실행
```cmd
# Windows 환경
gradlew.bat bootRun

# 또는 빌드 후 실행
gradlew.bat build
java -jar build\libs\assignment-0.0.1-SNAPSHOT.jar
```

### 2. 외부 시스템 설정
`src/main/resources/external-system-info.properties` 파일에서 외부 시스템 정보를 관리합니다:

```properties
# 로컬 테스트용 시스템
external.systems.systemLocal.base-url=http://localhost:9000
external.systems.systemLocal.fetch-path=/api/mock-external/orders
external.systems.systemLocal.send-path=/api/mock-external/orders

# 외부 시스템 A (네트워크 오류 테스트용)
external.systems.systemA.base-url=https://not.exist.abc
external.systems.systemA.fetch-path=/api/orders
external.systems.systemA.send-path=/api/orders
```

## 📋 주요 기능

### 1. **주문 데이터 가져오기** (`fetchAndSaveOrders`)
- 외부 시스템에서 JSON 형식의 주문 데이터를 HTTP GET으로 수신
- 받은 데이터에 대한 유효성 검증 (`@NotBlank`, `@NotNull` 등)
- 검증 통과한 주문만 메모리에 저장
- 주문 ID 기준으로 중복 데이터 업데이트 처리

### 2. **주문 데이터 전송하기** (`sendOrdersToExternal`)
- 내부 시스템의 주문 데이터를 JSON 형식으로 변환
- HTTP POST를 통해 외부 시스템으로 전송

### 3. **주문 조회 기능**
- 외부 시스템별 전체 주문 목록 조회
- 주문 ID를 통한 개별 주문 조회

## 🧪 테스트 시나리오

애플리케이션 실행 시 `CommandLineRunner`를 통해 다음 시나리오들이 자동 실행됩니다:

### ✅ **정상 케이스**
```java
// systemLocal과 연동하여 주문 데이터 가져오기 및 전송
externalOrderService.fetchAndSaveOrders("systemLocal");
externalOrderService.sendOrdersToExternal("systemLocal");
```

### ❌ **예외 케이스**
1. **네트워크 오류**: 존재하지 않는 URL로 연결 시도
2. **시스템 미존재**: 설정되지 않은 시스템명 사용
3. **데이터 유효성 오류**: 빈 고객명 등 검증 실패

## 📊 샘플 데이터

Mock API에서 제공하는 테스트 데이터:

```json
[
  {
    "orderId": "1001",
    "customerName": "김민수",
    "orderDate": "2024-01-15T10:30:00",
    "orderStatus": "PENDING"
  },
  {
    "orderId": "1002", 
    "customerName": "이영호",
    "orderDate": "2024-01-14T14:20:00",
    "orderStatus": "SHIPPING"
  },
  {
    "orderId": "1003",
    "customerName": "장경철", 
    "orderDate": "2024-01-16T09:15:00",
    "orderStatus": "COMPLETED"
  },
  {
    "orderId": "9999",
    "customerName": "",  // 유효성 검증 실패 케이스
    "orderDate": "2024-01-16T12:00:00",
    "orderStatus": "PENDING"
  }
]
```

## 🔍 예외 처리 전략

### 1. **네트워크 관련 예외**
- **연결 타임아웃**: 3초 이내 연결 실패 시 예외 발생
- **응답 타임아웃**: 3초 이내 응답 없을 시 예외 발생
- **HTTP 상태 코드**: 2xx 외의 응답에 대한 적절한 예외 처리

### 2. **데이터 검증 예외**
- **필수 필드 누락**: `@NotBlank`, `@NotNull` 어노테이션을 통한 검증
- **날짜 형식 오류**: `@JsonFormat`을 통한 JSON 직렬화/역직렬화 검증
- **부분 실패 허용**: 일부 주문 데이터 검증 실패 시에도 정상 데이터는 저장

### 3. **시스템 설정 예외**
- **미등록 시스템**: 설정 파일에 없는 시스템명 사용 시 예외 발생

## 🔄 확장 가능성

### 1. **새로운 외부 시스템 추가**
```properties
# 새로운 시스템 설정 추가만으로 확장 가능
external.systems.newSystem.base-url=https://newsystem.com
external.systems.newSystem.fetch-path=/api/orders
external.systems.newSystem.send-path=/api/orders
```

### 2. **데이터 저장소 변경**
- 현재: `InMemoryOrderRepository` (메모리 기반)
- 확장 가능: `DatabaseOrderRepository` (DB 기반)
- 인터페이스 기반으로 구현체만 교체하면 됨

### 3. **통신 프로토콜 변경**
- 현재: HTTP/JSON 기반 `ExternalOrderClientImpl`
- 확장 가능: gRPC, Message Queue 기반 구현체
- 클라이언트 인터페이스를 구현한 새로운 구현체 추가

### 4. **인증/보안 강화**
```java
// WebClient 설정에 인증 헤더 추가 가능
.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
```

## 📝 실행 결과 예시

```
[외부][주문 요청 성공] - system : systemLocal / count : 4
[내부][주문 저장 실패] - system : systemLocal / orderId : 9999 / error : [외부][유효하지 않은 주문 정보][field : customerName -> 공백일 수 없습니다]
[내부][주문 저장 완료] - success : 3/4
[연동][주문 반환 성공] - count: 3 / url : http://localhost:9000/api/mock-external/orders
[systemA 연동 실패] - [연동][주문 요청 실패] - url : https://not.exist.abc/api/orders / error : ...
[미존재 연동 실패] - [시스템 설정 정보 없음] - system : invalidSystem
```

이 시스템은 요구사항을 충실히 반영하여 **확장성**, **안정성**, **유지보수성**을 고려한 설계로 구현되었습니다.
