# 주식 리포트 관리 사이트 구현 가이드

## 1. 목표

네이버 금융의 기업 리서치 목록을 일자별로 수집하고, 수집한 보고서를 조회/관리하는 웹 사이트를 만든다.

- 프레임워크: Spring Boot
- 언어: Java 21
- DB: SQLite
- 템플릿 엔진: Thymeleaf
- 수집 대상
  - 종목명
  - 제목
  - 증권사
  - PDF 다운로드 링크
  - 보고서 날짜
- 추가 표시 항목
  - 종목명 옆 시가총액

## 2. 수집 대상 URL

- 리포트 목록
  - `https://finance.naver.com/research/company_list.naver`
- 시가총액 목록
  - `https://finance.naver.com/sise/sise_market_sum.naver`

## 3. 핵심 요구사항 정리

### 3.1 리포트 수집

리포트 목록 페이지에서 아래 항목을 수집한다.

- 종목명
- 제목
- 증권사
- PDF 다운로드 링크
- 보고서 날짜

### 3.2 일자별 수집

사용자가 특정 날짜를 기준으로 수집을 실행할 수 있어야 한다.

권장 동작:

- 관리 화면 또는 관리자 API에서 수집 대상 날짜를 입력한다.
- 크롤러는 목록 페이지를 순회하면서 해당 날짜의 리포트만 저장한다.
- 이미 저장된 리포트는 중복 저장하지 않는다.

### 3.3 시가총액 표시

시가총액은 별도 페이지에서 수집하되, 운영 방식은 수동 반영으로 시작한다.

권장 동작:

- 시가총액 데이터는 별도 테이블에 저장한다.
- 관리자가 수동으로 업로드하거나 수동 수집 버튼으로 갱신한다.
- 리포트 목록 조회 시 종목명 기준으로 시가총액을 매칭해 함께 보여준다.

## 4. 전체 아키텍처

### 4.1 계층 구조

- `web`
  - 화면 컨트롤러, 관리자 컨트롤러
- `application`
  - 리포트 수집 서비스
  - 시가총액 동기화 서비스
  - 조회 서비스
- `domain`
  - 엔티티
  - 리포지토리
- `infrastructure`
  - 네이버 크롤러
  - SQLite 설정
  - HTML 파서

### 4.2 추천 패키지 구조

```text
com.example.jusikreport
├─ JusikReportApplication
├─ config
├─ report
│  ├─ controller
│  ├─ service
│  ├─ domain
│  ├─ repository
│  └─ crawler
├─ marketcap
│  ├─ controller
│  ├─ service
│  ├─ domain
│  ├─ repository
│  └─ crawler
└─ common
   ├─ exception
   ├─ util
   └─ dto
```

## 5. 기술 선택 기준

### 5.1 HTML 수집

네이버 금융 HTML을 파싱해야 하므로 `Jsoup` 사용을 권장한다.

이유:

- 정적 HTML 파싱이 간단하다.
- CSS Selector 기반으로 원하는 셀을 추출하기 쉽다.
- 스프링부트와 함께 쓰기 부담이 적다.

### 5.2 DB 접근

초기 구현은 `Spring Data JPA + SQLite` 또는 `MyBatis + SQLite` 둘 다 가능하다.

이번 프로젝트는 CRUD와 단순 조회 중심이므로 아래를 권장한다.

- `Spring Data JPA`
- `Hibernate` 사용
- SQLite dialect 설정 추가

주의:

- SQLite는 대규모 동시성 DB가 아니므로 초기 관리형 사이트에 적합하다.
- 날짜/시간 타입과 자동 증가 컬럼 처리 정책은 초기에 명확히 고정해야 한다.

## 6. 데이터 모델 설계

### 6.1 리포트 테이블

테이블명: `reports`

권장 컬럼:

- `id`
- `stock_name`
- `report_title`
- `securities_firm`
- `pdf_url`
- `report_date`
- `source_page`
- `created_at`
- `updated_at`

중복 방지용 유니크 키 후보:

- `stock_name + report_title + securities_firm + report_date`

가능하면 PDF 링크까지 포함해 중복 판정 보조값으로 사용한다.

예시:

```sql
create table reports (
    id integer primary key autoincrement,
    stock_name varchar(100) not null,
    report_title varchar(255) not null,
    securities_firm varchar(100) not null,
    pdf_url varchar(500) not null,
    report_date date not null,
    source_page integer,
    created_at datetime not null,
    updated_at datetime not null
);
```

### 6.2 시가총액 테이블

테이블명: `market_caps`

권장 컬럼:

- `id`
- `stock_name`
- `market_cap_text`
- `market_cap_value`
- `rank_no`
- `collected_at`
- `updated_at`

설명:

- `market_cap_text`: 원문 표시값. 예: `25조 3,421억`
- `market_cap_value`: 정렬/비교용 숫자값
- `stock_name`: 리포트와 매칭할 기준값

예시:

```sql
create table market_caps (
    id integer primary key autoincrement,
    stock_name varchar(100) not null unique,
    market_cap_text varchar(100) not null,
    market_cap_value bigint not null,
    rank_no integer,
    collected_at datetime not null,
    updated_at datetime not null
);
```

## 7. 종목명 매칭 전략

시가총액은 리포트 수집 시점마다 다시 가져올 필요가 없고, 보유한 데이터를 기준으로 보여주면 된다.

기본 매칭 규칙:

- 리포트의 `stock_name`
- 시가총액 테이블의 `stock_name`
- 완전 일치 우선

후속 보완 규칙:

- 공백 제거 후 비교
- 우선주/보통주 표기 보정
- 괄호 포함 이름 정규화

초기 버전에서는 완전 일치만 먼저 구현하고, 불일치 케이스는 관리 화면에서 확인할 수 있게 한다.

## 8. 리포트 수집 방식

### 8.1 수집 단위

일자별 수집을 중심으로 설계한다.

입력:

- `LocalDate targetDate`

출력:

- 해당 날짜 리포트 저장 건수
- 중복 제외 건수
- 실패 건수

### 8.2 페이지 순회 전략

리포트 목록은 보통 페이지네이션 구조다. 따라서 아래 방식으로 수집한다.

1. 첫 페이지 요청
2. 목록 행 파싱
3. 각 행에서 날짜 추출
4. 날짜가 `targetDate`와 같으면 저장
5. 날짜가 `targetDate`보다 과거만 남는 시점이면 수집 종료
6. 다음 페이지 이동

이 방식의 장점:

- 전체 데이터를 전부 읽지 않아도 된다.
- 특정 날짜만 빠르게 수집 가능하다.

### 8.3 파싱 대상 항목

목록 행에서 추출할 값:

- 종목명
- 제목
- 증권사
- PDF 링크
- 날짜

가능하면 원본 상세 링크도 확보해 두는 것이 좋다. 나중에 중복 판정이나 디버깅에 유리하다.

### 8.4 PDF 링크 처리

PDF 링크는 상대경로일 가능성이 있으므로 절대경로로 변환해 저장한다.

예시 처리:

- 상대경로면 `https://finance.naver.com` 를 prefix로 붙인다.
- 이미 절대경로면 그대로 저장한다.

### 8.5 날짜 파싱

네이버 페이지 날짜 표기가 `YYYY.MM.DD` 형태일 가능성이 높다.

권장 처리:

- 원문 문자열 trim
- `DateTimeFormatter.ofPattern("yyyy.MM.dd")`
- `LocalDate` 변환

## 9. 시가총액 수집 방식

### 9.1 운영 전략

시가총액은 리포트처럼 매일 강하게 변하는 업무 핵심 데이터는 아니므로, 초기에는 수동 동기화로 시작한다.

권장 방식:

- 관리자 메뉴에서 "시가총액 동기화" 버튼 제공
- 동기화 시 전체 목록 또는 필요한 범위만 읽어서 저장
- 종목명 기준 upsert 수행

### 9.2 수집 항목

시가총액 페이지에서 아래 항목을 수집한다.

- 종목명
- 시가총액 표시값
- 시가총액 숫자값
- 순위

### 9.3 숫자 변환 정책

표시 문자열 예시:

- `412조 3,551억`
- `9조 5,120억`

숫자 변환 규칙 예시:

- `1조 = 1_0000_0000_0000`
- `1억 = 100_000_000`

즉, 문자열을 파싱해 `long` 값으로 환산한 `market_cap_value`를 저장한다.

## 10. 화면 설계

### 10.1 사용자 화면

메인 목록 페이지에서 리포트를 조회한다.

표시 컬럼:

- 보고서 날짜
- 종목명
- 시가총액
- 제목
- 증권사
- PDF 다운로드

검색/필터:

- 날짜
- 종목명 키워드
- 증권사

정렬:

- 기본: 보고서 날짜 내림차순
- 선택: 시가총액 내림차순

### 10.2 관리자 화면

최소 기능:

- 특정 날짜 리포트 수집 실행
- 시가총액 수동 동기화 실행
- 리포트 목록 조회
- 수집 결과 로그 확인

## 11. Thymeleaf 구성 방향

### 11.1 템플릿 분리

- `templates/report/list.html`
- `templates/admin/report-crawl.html`
- `templates/admin/market-cap-sync.html`
- `templates/fragments/header.html`
- `templates/fragments/pagination.html`

### 11.2 목록 표시 규칙

리포트 목록에서 종목명 옆에 시가총액을 함께 표시한다.

예시 표현:

- `삼성전자 (412조 3,551억)`

시가총액이 없으면:

- `삼성전자 (-)`

## 12. API/컨트롤러 초안

### 12.1 화면 컨트롤러

- `GET /reports`
  - 리포트 목록 조회
- `GET /admin/reports/crawl`
  - 수집 화면
- `POST /admin/reports/crawl`
  - 특정 날짜 수집 실행
- `GET /admin/market-caps`
  - 시가총액 동기화 화면
- `POST /admin/market-caps/sync`
  - 시가총액 동기화 실행

### 12.2 서비스 메서드 예시

```java
public interface ReportCrawlService {
    CrawlResult crawlByDate(LocalDate targetDate);
}

public interface MarketCapSyncService {
    SyncResult sync();
}

public interface ReportQueryService {
    Page<ReportListItem> search(ReportSearchCondition condition, Pageable pageable);
}
```

## 13. 크롤러 구현 원칙

### 13.1 공통 원칙

- 네트워크 호출과 HTML 파싱 로직을 분리한다.
- CSS Selector 문자열은 한 곳에 모은다.
- 파싱 실패 row는 전체 실패로 만들지 말고 건별 로그 처리한다.
- User-Agent 를 설정한다.
- 타임아웃을 명확히 설정한다.

### 13.2 권장 클래스 예시

```text
report/crawler
├─ NaverResearchCrawler
├─ NaverResearchRowParser
└─ ResearchFetchClient

marketcap/crawler
├─ NaverMarketCapCrawler
├─ NaverMarketCapRowParser
└─ MarketCapValueParser
```

### 13.3 예외 처리

- HTTP 요청 실패
- HTML 구조 변경
- 날짜 파싱 실패
- PDF 링크 누락
- 종목명 누락

예외는 운영 로그에 남기고, 관리 화면에서 수집 결과를 확인할 수 있게 한다.

## 14. 중복 저장 방지

중복 방지는 반드시 DB 레벨과 서비스 레벨 둘 다 둔다.

권장 방식:

- 서비스에서 기존 존재 여부 확인
- DB 유니크 인덱스로 최종 방어

예시 유니크 인덱스:

```sql
create unique index ux_reports_unique
on reports(stock_name, report_title, securities_firm, report_date);
```

## 15. 구현 순서

### 1단계

프로젝트 기본 세팅

- Spring Boot 3.x
- Java 21
- Thymeleaf
- Spring Data JPA
- SQLite
- Jsoup

### 2단계

DB 스키마 및 엔티티 구성

- `Report`
- `MarketCap`

### 3단계

리포트 크롤러 구현

- 목록 페이지 요청
- row 파싱
- 날짜 필터링
- 저장

### 4단계

시가총액 크롤러 구현

- 목록 페이지 요청
- 종목명/시가총액 파싱
- 종목명 기준 upsert

### 5단계

조회 화면 구현

- 리포트 목록
- 날짜/종목명/증권사 검색
- 종목명 옆 시가총액 표시

### 6단계

관리 화면 구현

- 일자별 리포트 수집
- 시가총액 수동 동기화

## 16. 초기 쿼리 전략

목록 조회는 리포트와 시가총액을 조인하거나, 조회 DTO에서 후처리 매칭할 수 있다.

초기에는 아래를 권장한다.

- `reports left join market_caps on reports.stock_name = market_caps.stock_name`

이유:

- 화면 표시가 단순하다.
- 페이지네이션에 유리하다.

## 17. 검증 포인트

네이버 HTML 구조는 변경될 수 있으므로, 실제 구현 전 아래를 먼저 확인해야 한다.

- 리포트 목록 테이블 selector
- 각 컬럼의 실제 순서
- PDF 링크가 직접 링크인지 onclick 기반인지
- 날짜 형식
- 페이지네이션 링크 구조
- 시가총액 페이지의 종목명/시가총액 컬럼 위치

이 문서는 현재 요구사항 기준의 구현 설계 문서이며, 실제 코딩 단계에서는 위 selector를 먼저 검증한 뒤 크롤러를 고정한다.

## 18. 초기 완료 기준

1차 완료 기준은 아래와 같다.

- 특정 날짜 리포트 수집 가능
- 중복 없이 SQLite 저장 가능
- 시가총액 수동 동기화 가능
- 리포트 목록에서 종목명 옆 시가총액 표시 가능
- Thymeleaf 기반 관리 화면 제공

## 19. 다음 작업 제안

다음 단계에서는 아래 순서로 진행하면 된다.

1. Spring Boot 프로젝트 초기 생성
2. 의존성 및 SQLite 설정
3. 엔티티/리포지토리 작성
4. 네이버 크롤러 구현
5. 관리자 화면 및 목록 화면 구현

