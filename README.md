# 🏗️공사 중 마실가실
마실가실 프로젝트는 23년 6월에 개발한 국내 여행 일정 생성 및 여행 후기 커뮤니티 사이트입니다.  
공사 중 마실가실 프로젝트는 '1년이 지난 후, 나는 얼마만큼 프로젝트에서 개선할 부분을 찾을 수 있을까?' 라는 작은 의문에서 시작되었습니다.  
코드 리팩토링 및 추가 기능 구현과 관련된 구체적인 내용은 [`🚀리팩토링 일지`](https://github.com/HJ0216/msgs-trip-project-hj/blob/main/refactoring_log.md) 또는 [`🔗마실가실 리팩토링 블로그`](https://hj0216.tistory.com/category/PlayGround/%EB%A7%88%EC%8B%A4%EA%B0%80%EC%8B%A4%20%EB%A6%AC%ED%8C%A9%ED%86%A0%EB%A7%81)를 클릭해주세요.


## 📜사용 기술 및 라이브러리
* Java 17
* Spring Boot 3.1.0, Spring Security 6.1.0, Spring Data JPA
* MySQL 8.0, Redis



## 🖥️로컬 환경에서 프로젝트 설치와 실행 방법
1. 프로젝트 Clone
```bash
git clone https://github.com/HJ0216/msgs-trip-project-hj.git
cd msgs-trip-project-hj
```

2. MySQL 설정  
```bash
mysql -u root -p
CREATE DATABASE msgs_hj;
USE msgs_hj

# 🚨username, password, database 이름이 application.yml과 상이할 경우, 
# MySQL 또는 application.yml 파일 수정이 필요합니다.
```

3. Redis Server 실행

4.  Gradle 의존성 설치 
```bash
# Root Directory 위치에서 실행
# Mac/Linux
./gradlew clean build
# Windows
gradlew.bat clean build
```

5. 애플리케이션 실행
```bash
# Root Directory 위치에서 실행
# Mac/Linux
./gradlew bootRun
# Windows
gradlew.bat bootRun
```

6. 애플리케이션 접속  
브라우저에서 http://localhost:8080 으로 접속



## Branch 관리
### Branch 전략
Git-Flow 브랜치 전략에 따라 기능별로 브랜치를 나누어 작업  
* main: 제품으로 출시될 수 있는 브랜치
* develop: 다음 출시 버전을 개발하는 브랜치, feature에서 PR이 완료된 브랜치를 Merge
* feature: 기능을 개발하는 브랜치
* fix : 출시 버전에서 발생한 버그를 수정하는 브랜치

### 개발 Flow
* 작업 시작 시: develop -> feature로 분기
  * feature/{issue-number}-기능요약 (feature/77-login-authentication)
* 작업 및 테스트 완료 시: develop에 feature 병합
* 상용 배포 시: develop -> main으로 병합



## 💡커밋 규칙
### 커밋 메시지 형식
> 50자를 넘기지 않게 명령형으로 작성  
> 한국어로 작성  
> 어떻게 보단 `무엇`을, `왜`에 초점을 두고 작성  
> Commit Body는 PR에 기재
```txt
CommitType : #issueNumber Subject

✨Feat: #-- 로그 기능 추가
🐛Fix: #-- 에러 메시지 오류 수정
🎨Style: #-- 회원가입 API 함수명 수정
```

### 커밋 타입

| 커밋 타입   | 설명                       |
| ---------- | --------------------------|
| ✨Feat     | 기능 추가                  |
| 🐛Fix      | 버그 수정                  |
| 🎨Style    | 코드 포맷팅, 함수명 수정 등  |
| ♻️Refactor | 코드 리펙토링              |
| 🧹Chore    | 그 외 자잘한 수정          |
| 💡Comment  | 주석 추가 및 수정          |
| ✅Test     | 테스트 코드 작성 및 수정    |
| 📝Docs     | 문서 작성 및 수정          |


## 코드 스타일
* [intellij-java-google-style](https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml) 적용
* IntelliJ에서 적용 시, 아래 설정 적용
  * Reformat code: 저장 시마다 formmating 적용
  * Optimize imports: 사용하지 않는 import문 제거
* 일부 파일 코드 스타일을 적용하지 않을 경우, Do not Formatter에 해당 파일 추가



## 💾 ERD
<img src="./images/msgs_refactoring_erd.png" alt="msgs_refactoring_erd"/>
