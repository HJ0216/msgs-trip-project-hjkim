# 🏗️마실가실 Backend 리팩토링


## 🤹마실가실 리팩토링 블로그
[🔗마실가실 리팩토링](https://hj0216.tistory.com/category/PlayGround/%EB%A7%88%EC%8B%A4%EA%B0%80%EC%8B%A4%20%EB%A6%AC%ED%8C%A9%ED%86%A0%EB%A7%81)


## 📒마실가실 리팩토링 일지
| <div style="width:70px">Date</div> | <div>Description</div> |
| ------------ | --- |
| 2024.10.08 ~ | [자동화 및 표준화 작업](#자동화-및-표준화-작업) |
| 2024.10.03   | [로그 설정 추가](#로그-설정-추가) |
| 2024.09.17 ~ | [Custom Error Code 설정](#custom-error-code-설정) |
| 2024.09.03   | [도메인형 패키지 구조로 변경](#패키지-구조-변경) |
| 2024.08.11 ~ | [Spring Security, JWT 코드 리팩토링](#spring-security-jwt-코드-리팩토링) |
| 2024.08.05   | [일부 API 테스트 코드 추가](#test-code-추가) |
| 2024.08.04   | [RESTful API 구현](#restful-api-구현) |
| 2024.07.28   | [Entity 개선](#entity-개선) |
| 2024.07.27   | 환경 설정(로컬 DB 연결 및 JPA 설정) <br/> [ERD 수정](#erd-수정) |



### ERD 수정
<div>
    <img src="./images/msgs_previous_erd.png" alt="msgs_previous_erd" width="50%"/>
    <img src="./images/msgs_refactoring_erd.png" alt="msgs_refactoring_erd" width="45%"/>
</div>

(좌) 기존 ERD / (우) 리팩토링 ERD

🚨문제: 여행 일정 및 여행지 리뷰 테이블에 동일한 여행지 데이터가 중복으로 저장  
🤓개선 방안: 중복 데이터에 대하여 제1정규화 수행(여행지 테이블 별도 분리 후 관계 설정)


### Entity 개선
* Auditing 기능 추가
  * 엔티티의 생성 및 수정 시점을 자동으로 감지하여 기록
  * 공통 속성(생성 시각, 수정 시각 등)을 BaseEntity로 추출하여 관리


### RESTful API 구현
* URI에 자원 정보 표시 및 행위에 맞는 Http 메서드 사용


### Test Code 추가
* Controller Test Code를 통한 주요 API의 입출력 검증
* Service Test Code를 통한 비즈니스 로직 검증


### Spring Security, JWT 코드 리팩토링
* 토큰 재발급 기능 구현
* 로그아웃 기능 추가
* 특정 url을 제외한 필터 적용
* JWT 응답 방식 변경
  * Access Token: Body → Header
  * Refresh Token: Body → Cookie
* 응답 메시지에 한글이 출력될 수 있도록 UTF-8 인코딩 설정
* Filter에서 발생한 예외를 GlobalExceptionHandler에서 처리


### 패키지 구조 변경
* 도메인형 + 계층형 → 비즈니스 도메인을 중심으로 패키지 구조 재구성


### Custom Error Code 설정
* ErrorCode 인터페이스 정의: Name, HttpStatus, Message
* (C)CommonErrorCode: 애플리케이션 전반에서 발생할 수 있는 Error Code 정의
* (C)UserErrorCode: 사용자 관련 Error Code 정의


### 로그 설정 추가
* Console 출력 방식 → SLF4J 기반 로깅으로 변경


### 자동화 및 표준화 작업
* 정적 코드 분석 도구, SonarQube를 추가하여 코드 품질 자동 분석
* API 문서화 도구, Spring REST Docs 적용하여 문서 자동화 및 표준화
* Github Issue 및 PR에 표준 Template 적용
