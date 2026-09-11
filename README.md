# 붉은 달의 성채 (Crimson Citadel) — Game Basic Assignment
(레벨 별로 브랜치 만들어서 커밋했습니다.)

로그라이크 카드 게임 "붉은 달의 성채"의 백엔드 API 서버입니다. Spring Boot 기반으로 게임 진행 상태를 저장·관리하고, 외부 랭킹 API를 연동하여 시즌 랭킹을 제공합니다.

## 기술 스택

- **Language**: Java 21
- **Framework**: Spring Boot 4.1.0
- **ORM**: Spring Data JPA (Hibernate)
- **DB**: MySQL 8
- **Build Tool**: Gradle
- **Container**: Docker
- **API 통신**: Spring RestClient (외부 랭킹 API 연동)

## 실행 방법

### 1. MySQL 컨테이너 실행

```bash
docker run -d \
  --name mysql-db \
  --network my-network \
  -e MYSQL_ROOT_PASSWORD=12345678 \
  -e MYSQL_DATABASE=mydb \
  -p 3307:3306 \
  mysql:8
```

> Docker 네트워크가 없다면 먼저 생성합니다: `docker network create my-network`

### 2. 환경 설정 파일 (`src/main/resources/application.properties`)

```properties
spring.datasource.url=jdbc:mysql://mysql-db:3306/mydb
spring.datasource.username=root
spring.datasource.password=12345678
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 3. 애플리케이션 실행

## API 명세

| 기능 | Method | Endpoint |
|---|---|---|
| 게임 목록 조회 | GET | `/games` |
| 게임 생성 | POST | `/games` |
| 게임 상세 조회 | GET | `/games/{gameId}` |
| 진행·전체 덱 저장 | PUT | `/games/{gameId}/progress` |
| 플레이어 이름 변경 | PATCH | `/games/{gameId}` |
| 게임 삭제 | DELETE | `/games/{gameId}` |
| 시즌 랭킹 조회 | GET | `/rankings` |

### 주요 응답 규칙

- 게임을 찾을 수 없으면 `404 Not Found`
- 이미 종료(`CLEARED`/`FAILED`)된 게임에 진행 저장 요청 시 `409 Conflict`
- 요청 값 검증 실패 시 `400 Bad Request` + 상세 메시지

## 아키텍처 개요

```
Client (JSON)
   │
   ▼
Controller  ── HTTP 요청/응답 처리
   │
   ▼
Service     ── 비즈니스 로직 (Entity 중심)
   │
   ├──▶ Repository ──▶ MySQL (게임/카드 데이터)
   │
   └──▶ RankingClient ──▶ 외부 랭킹 API (RestClient)
```

- **Entity ↔ DB**: `Game`, `RunCard` 등은 JPA Repository를 통해 DB와 매핑
- **DTO ↔ Client**: 요청(`*Request`)/응답(`*Response`) DTO로 클라이언트와 통신
- **더티 체킹**: `@Transactional` 내에서 엔티티 필드만 변경하면 트랜잭션 종료 시 자동 UPDATE
- **N+1 방지**: 게임별 카드 수 집계는 `group by` + JPQL `select new` 구문으로 단일 쿼리 처리
- **공통 감사(Audit) 필드**: `BaseEntity`를 상속하여 `createdAt`/`updatedAt` 자동 관리

## 랭킹 시스템

외부 API(`RankingClient`, `RestClient` 기반)에서 원본 기록을 받아온 뒤, 서버에서 아래 순서로 가공하여 응답합니다.

1. **순위 대상 필터링**: `run.status == CLEARED && run.clearedFloor == 10`
2. **정상 기록 검증**: 클리어 시간, 남은 HP, 덱 크기, 카드 타입, 획득 층, 보스 페이즈, 마무리 카드 조건을 모두 만족하는 기록만 채택
3. **정렬**: `clearTimeSeconds` 오름차순 → `remainingHp` 내림차순 → `records.id` 오름차순
4. **플레이어당 하나만 유지**: 동일 플레이어의 기록 중 정렬 순서상 가장 앞선 것만 채택
5. **순위(rank) 부여**: 최종 리스트에 1부터 순위 부여

## 예외 처리

`GlobalExceptionHandler`에서 전역으로 처리하며, 상태 코드와 메시지를 일관된 형식(`ErrorResponse`)으로 응답합니다.

| 예외 | 상태 코드 |
|---|---|
| `GameNotFoundException` | 404 |
| `GameFinishedException` | 409 |
| Bean Validation 실패 (`MethodArgumentNotValidException` 등) | 400 |
| 요청 본문/파라미터 형식 오류 | 400 |

## 프로젝트 구조

```
src/main/java/com/gamebasic/
├── common/
│   └── exception/       # 커스텀 예외, GlobalExceptionHandler
├── game/
│   ├── controller/
│   ├── dto/
│   ├── entity/          # Game, GamePhase, GameStatus
│   ├── repository/
│   └── service/
├── runcard/
│   ├── dto/
│   ├── entity/
│   └── repository/
├── ranking/
│   ├── client/           # RankingClient (RestClient)
│   ├── dto/              # 외부 API 응답 DTO + 최종 응답 DTO
│   ├── controller/
│   └── service/
└── GameBasicApplication.java
```
