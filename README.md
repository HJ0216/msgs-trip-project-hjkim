# 🏗️공사 중 마실가실

'1년이 지난 후, 나는 얼마만큼 프로젝트에서 개선할 부분을 찾을 수 있을까?' 라는 작은 의문에서 시작한 공사 중 마실가실입니다.


## 🛠️사용한 기술
* Java 17
* SpringBoot 3.1.0
* Spring Data JPA
* MySQL 8.0


## 📝주요 개선 내용
### ERD 수정
<div>
    <img src="./images/msgs_previous_erd.png" alt="msgs_previous_erd" width="50%"/>
    <img src="./images/msgs_refactoring_erd.png" alt="msgs_refactoring_erd" width="45%"/>
</div>

(좌) 기존 ERD / (우) 리팩토링 ERD

* 문제: 여행 일정 상세 테이블에서 여행지 정보 관련 내용 수정 시, 일부 데이터가 누락됨
* 원인: 여행지 데이터가 중복 저장되어 데이터의 불일치가 발생하기 쉬움
* 해결: 중복 데이터에 대하여 제 1 정규화 수행 -> 여행지 테이블 별도 분리 후 관계 설정


### Entity 개선
* Auditing 기능 추가
  * 엔티티가 생성되고, 변경되는 시점을 감지하여 생성시각, 수정시각, 생성인, 수정인 등을 자동으로 기록
  * 여러 테이블의 공통 속성인 생성시각, 수정시각을 하나의 Entity(= BaseEntity)로 관리
```java
// 개선 전
@Entity
public class UserEntity implements UserDetails {
   // ...
   
   @Column(name = "reg_date", nullable = false)
   private LocalDate regDate;
   @Column(name = "mod_date", nullable = false)
   private LocalDate modDate;
}
```
```java
// 개선 후
@Entity
public class User extends BaseEntity implements UserDetails {
    // ...
}

@Getter
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class BaseEntity {
    @Column(nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdDate;

    @Column(nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedDate;
}
```


### REST API 구현
* URI에 자원의 정보를 표시
* 행위에 맞는 Http 메서드로 수정
```java
// 개선 전
@RestController
@RequestMapping("user")
public class UserController {
    @PostMapping("/signup")
    public void userSignUp(@RequestBody UserEntity userEntity) {
        userService.signUp(userEntity);
    }
    
    @PostMapping("/info")
    public ResponseEntity<?> getUserInfo(@RequestParam String accessToken) {
        JSONObject userInfo = userService.getUserInfo(accessToken);
        return ResponseEntity.ok().body(userInfo.toString());
    }

    // ...
}
```

```java
// 개선 후
@RestController
@RequestMapping("api/v2/users")
@RequiredArgsConstructor
public class UserController {
    @PostMapping("/new")
    @ResponseStatus(HttpStatus.OK)
    public void create(@RequestBody SignUpRequestDTO dto){
        dto.validUserDto();
        userService.create(dto);
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO findMyInfo(){
        return userService.findMyInfo();
    }
}
```


### Test Code 추가
```java
@SpringBootTest
@Transactional
public class UserServiceTest {
    @Autowired UserService userService;
    @Autowired UserRepository userRepository;

    @Test
    @DisplayName("회원 가입")
    void userSignUp() throws Exception {
        // given
        SignUpRequestDTO dto = SignUpRequestDTO.builder()
                .status("M")
                .email("test0907@email.com")
                .phone("01075395468")
                .nickname("hello")
                .password("1234")
                .build();

        // when
        userService.create(dto);

        // then
        User savedUser = userRepository.findByEmail(dto.getEmail()).orElseThrow(
                () -> new BusinessException(NOT_FOUND_MEMBER));

        // 필드 값 비교
        assertThat(savedUser.getEmail()).isEqualTo(dto.getEmail());
        assertThat(savedUser.getPhone()).isEqualTo(dto.getPhone());
    }
}
```


### Spring Security, JWT 학습
>SpringSecurity와 JWT가 Login 시 동작하는 과정

>1. 애플리케이션 시작 → SpringConfig  
>\* Spring Security의 초기화 및 설정 과정: JwtAuthenticationFilter 등
>2. API 호출 → JwtAuthenticationFilter 요청 처리  
>🚨 필터에서 오류가 발생할 경우, Controller에 도달하지 못하므로 Debugging 시, 유의
>3. Controller → UserService 호출
>4. UserService: AuthenticationManagerBuilder.authenticate() 호출  
>\* 전달된 인증 객체(Authentication)를 사용하여 인증되지 않은 사용자에 대한 인증을 수행  
>\* 내부적으로 설정된 AuthenticationProvider들을 순차적으로 사용하여 자격 증명을 검증
>5. AuthenticationProvider → UserDetailsService 호출
>6. UserDetailsService
>\* 내부적으로 loadUserByUsername()를 호출하여 데이터베이스에서 해당 이메일을 가진 사용자를 찾아 UserDetails 객체로 반환  
>\* 해당 이메일이 존재하지 않는다면, UsernameNotFoundException가 발생하여 인증 실패
>7. Authentication 객체: SecurityContextHolder에 저장
>8. 토큰 발행


### Refresh Token을 활용한 Access Token 재발급 기능 추가
```java
public TokenInfo reissue(TokenInfo reIssueDto) {
    try {
        jwtTokenProvider.getExpiration(reIssueDto.getAccessToken());
        throw new BusinessException(VALID_ACCESS_TOKEN);
    } catch (ExpiredJwtException e) {
        // Refresh Token 유효성 검사
        if (!jwtTokenProvider.isValidToken(reIssueDto.getRefreshToken())) {
            throw new BusinessException(INVALID_REFRESH_TOKEN);
        }

        // Redis에 Refresh Token 존재 확인
        boolean hasStoredRefreshToken = redisTemplate.hasKey("RT:" + reIssueDto.getRefreshToken());
        if(!hasStoredRefreshToken) {
            throw new BusinessException(LOGOUT_MEMBER);
        }

        String email = redisTemplate.opsForValue().get("RT:" + reIssueDto.getRefreshToken());
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new BusinessException(NOT_FOUND_MEMBER));

        // AccessToken 재발급

        // User의 role -> 스프링시큐리티의 GrantedAuthority로 변경
        // 여러개의 role을 가질수 있으므로 Set
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Set.of(SecurityUtils.convertToAuthority(user.getRole()))
        );

        return jwtTokenProvider.generateAccessToken(userDetails);
    }
}
```


### 패키지 구조 변경
> 도메인형 + 계층형 -> 도메인형으로 통일
```txt
├─domain
│  ├─tripschedule
│  │  ├─controller
│  │  ├─domain
│  │  ├─dto
│  │  ├─exception
│  │  ├─repository
│  │  └─service
│  ├─tripstory
│  │  ├─controller
│  │  ├─domain
│  │  ├─dto
│  │  ├─exception
│  │  ├─repository
│  │  └─service
│  └─user
│      ├─controller
│      ├─domain
│      ├─dto
│      ├─exception
│      ├─repository
│      └─service
├─global
│  ├─common
│  │  ├─error
│  │  ├─jwt
│  │  ├─model
│  │  └─redis
│  ├─config
│  └─util
└─infra
    ├─chatbot
    └─imageupload
```


### 정적 코드 분석 도구, SonarQube 추가
> 코드 품질 검사용 오픈 소스를 활용하여 버그 수정 및 유지보수성 등을 개선

![image](https://github.com/user-attachments/assets/9735e3d9-6599-4b65-af2f-8a376bcaec2d)







## 🤹마실가실 리팩토링 블로그
[🔗마실가실 리팰토링](https://hj0216.tistory.com/category/PlayGround/%EB%A7%88%EC%8B%A4%EA%B0%80%EC%8B%A4%20%EB%A6%AC%ED%8C%A9%ED%86%A0%EB%A7%81)
