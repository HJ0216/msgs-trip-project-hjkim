# 🏗️마실가실 Backend 리팩토링

## 🤹마실가실 리팩토링 블로그
[🔗마실가실 리팩토링](https://hj0216.tistory.com/category/PlayGround/%EB%A7%88%EC%8B%A4%EA%B0%80%EC%8B%A4%20%EB%A6%AC%ED%8C%A9%ED%86%A0%EB%A7%81)

## 📒마실가실 리팩토링 일지
| <div style="width:70px">Date</div> | <div>Description</div> |
| ---------- | --- |
| 2024.10.03 | [로그 설정 추가](#로그-설정-추가) |
| 2024.09.29 | [회원가입 입력값 검증을 GlobalUtils → DTO 내 검증으로 변경](#회원가입-입력값-검증) |
| 2024.09.18 | [Spring Security url별 필터 적용 기능 추가](#spring-security-특정-url을-제외한-필터-적용) |
| 2024.09.17 | Custom Error Response 설정 |
| 2024.09.16 | 로그아웃 기능 추가 |
| 2024.09.14 | [Token 재발급 기능 추가](#refresh-token을-활용한-access-token-재발급-기능-추가) |
| 2024.09.03 | [도메인형 패키지 구조로 변경](#패키지-구조-변경) |
| 2024.08.23 | 로그아웃 구현을 위한 Redis 설정 추가 |
| 2024.08.11 | Spring Security, JWT 코드 리팩토링 |
| 2024.08.10 | CustomException 추가 |
| 2024.08.05 | [일부 API 테스트 코드 추가](#test-code-추가) |
| 2024.08.04 | [일부 기존 API → REST API 형식으로 수정](#rest-api-구현) |
| 2024.07.28 | [Entity 개선](#entity-개선) |
| 2024.07.27 | 환경 설정(로컬 DB 연결 및 JPA 설정) <br/> [ERD 수정](#erd-수정) |



### ERD 수정
<div>
    <img src="./images/msgs_previous_erd.png" alt="msgs_previous_erd" width="50%"/>
    <img src="./images/msgs_refactoring_erd.png" alt="msgs_refactoring_erd" width="45%"/>
</div>

(좌) 기존 ERD / (우) 리팩토링 ERD

🚨문제: 여행 일정 및 여행지 리뷰 테이블에서 동일한 여행지 데이터가 중복으로 저장  
🤓개선 방안: 중복 데이터에 대하여 제1정규화 수행 -> 여행지 테이블 별도 분리 후 관계 설정


### Entity 개선
* Auditing 기능 추가
  * 엔티티가 생성되고, 변경되는 시점을 감지하여 생성시각, 수정시각, 생성일, 수정일 등을 자동으로 기록
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
    @ResponseStatus(HttpStatus.CREATED)
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
* TooManyActualInvocations을 방지하기 위해 @BeforeEach 대신 @MockBean의 doThrow()를 사용
```java
@SpringBootTest
@AutoConfigureMockMvc
public class UserServiceTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean UserService userService;

    @Test
    @DisplayName("Controller: 회원 가입 실패, 중복된 이메일")
    void createFailDuplicateEmail() throws Exception {
        // given
        SignUpRequestDTO signUpDto = SignUpRequestDTO.builder()
                .status("M")
                .email("temp@email.com")
                .phone("01023698745")
                .nickname("name")
                .password("temp123!")
                .confirmPassword("temp123!")
                .build();

        // when // then
        doThrow(new BusinessException(ErrorCode.DUPLICATED_EMAIL))
                .when(userService).create(any(SignUpRequestDTO.class));

        mockMvc.perform(post("/api/v2/users/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("이미 존재하는 이메일 입니다."));

        verify(userService).create(refEq(signUpDto));
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



### Refresh Token을 활용한 Access Token 재발급 기능 추가
```java
public TokenInfo reissue(TokenInfo reissueRequestDto) {
    try {
        jwtTokenProvider.getExpiration(reissueRequestDto.getAccessToken());
        throw new BusinessException(VALID_ACCESS_TOKEN);
    } catch (ExpiredJwtException e) {
        // Redis에 Refresh Token 존재 확인
        boolean hasStoredRefreshToken = redisUtil.hasKey("RT:" + reissueRequestDto.getRefreshToken());
        if(!hasStoredRefreshToken) {
            throw new BusinessException(LOGOUT_MEMBER);
        }

        String email = (String) redisUtil.get("RT:" + reissueRequestDto.getRefreshToken());
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new BusinessException(NOT_FOUND_MEMBER));

        // AccessToken 재발급
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Set.of(SecurityUtils.convertToAuthority(user.getRole()))
        );

        return jwtTokenProvider.generateAccessToken(userDetails);
    }
}
```


### Spring Security: 특정 url을 제외한 필터 적용
🚨문제: JwtAuthentificationFilter에서 Access Token의 유효성 검증을 수행하는데, 유효성 검증이 필요없는 회원가입이나 Access Token 재발행에서도 Filter가 동작  
🤓개선 방안: 특정 url에서만 filter가 동작하도록 수정  

🛠️해결1, **shouldNotFilter**  
\* 특정 경로에 대한 특정 필터만 제외
```java
public JwtAuthenticationFilter jwtAuthenticationFilterForSpecificUrls() {
    return new JwtAuthenticationFilter(jwtTokenProvider) {
        @Override
        protected boolean shouldNotFilter(HttpServletRequest request) {
            String path = request.getServletPath();
            return !("/api/v2/users/login".equals(path) || "/api/v2/users/me".equals(path) || "/api/v2/users/logout".equals(path));
        }
    };
}

@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.httpBasic(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .cors(httpSecurityCorsConfigurer -> corsConfigurationSource())
            .sessionManagement(sessionManagement ->
                    sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            ).authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/v2/users/login").permitAll()
                    .requestMatchers("/api/v2/users/new").permitAll()
                    .requestMatchers("/api/v2/users/me", "/api/v2/users/logout").hasRole("USER")
                    .requestMatchers("/api/v2/users/reissue").permitAll()
                    .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilterForSpecificUrls(), UsernamePasswordAuthenticationFilter.class);

    return http.build();
}
```

🛠️해결2, **WebSecurityCustomizer**  
\* 경로에 대해 모든 필터 체인을 비활성화
```java
@Bean
public WebSecurityCustomizer webSecurityCustomizer() {
    return (web) -> web.ignoring()
            .requestMatchers("/api/v2/users/new", "/api/v2/users/reissue");
}

@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.httpBasic(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .cors(httpSecurityCorsConfigurer -> corsConfigurationSource())
            .sessionManagement(sessionManagement ->
                    sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            ).authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/v2/users/login").permitAll()
                    .requestMatchers("/api/v2/users/new").permitAll()
                    .requestMatchers("/api/v2/users/me", "/api/v2/users/logout").hasRole("USER")
                    .requestMatchers("/api/v2/users/reissue").permitAll()
                    .anyRequest().authenticated()
            )
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

    return http.build();
}
```



### 회원가입 입력값 검증
```java
@Builder
@AllArgsConstructor
@Getter
public class SignUpRequestDTO {

  @NotNull(message = "회원 타입은 필수 값입니다.")
  private String userType;

  @NotBlank(message = "이메일을 입력해 주세요.")
  @Pattern(regexp = EMAIL_REGEX, message = "이메일 형식이 올바르지 않습니다.")
  private String email;

  @NotBlank(message = "전화번호를 입력해 주세요.")
  @Pattern(regexp = PHONE_REGEX, message = "전화번호 형식이 올바르지 않습니다.")
  private String phone;

  @Pattern(regexp = NICKNAME_REGEX, message = "닉네임 형식이 올바르지 않습니다.")
  private String nickname;

  @NotBlank(message = "비밀번호를 입력해 주세요.")
  @Pattern(regexp = PASSWORD_REGEX, message = "비밀번호 형식이 올바르지 않습니다.")
  private String password;

  @NotBlank(message = "비밀번호 확인을 입력해 주세요.")
  private String confirmPassword;

  @Builder.Default
  private String role = "USER";

  public User toEntity() {
    return User.builder()
               .status(userType.substring(0, 1).toUpperCase())
               .userType(UserType.valueOf(userType.toUpperCase()))
               .email(email)
               .phone(phone)
               .nickname(nickname)
               .password(password)
               .role(role)
               .build();
  }

  public boolean validateSignUpRequest() {
    try {
      validateUserType();
      isPasswordConfirmed();
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  private void validateUserType() {
    if (!UserType.isValidUserType(userType)) {
      throw new BusinessException(USERTYPE_VALIDATION);
    }
  }

  @AssertTrue(message = "비밀번호와 비밀번호 확인이 일치하지 않습니다.")
  private boolean isPasswordConfirmed() {
    return password.equals(confirmPassword);
  }
}
```



### 로그 설정 추가
```java
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
  public TokenInfo login(LoginRequestDTO loginRequestDTO) {
    User user = userRepository.findByEmail(loginRequestDTO.getEmail()).orElseThrow(
        () -> {
          log.warn("User not found for email: {}", loginRequestDTO.getEmail());
          throw new BusinessException(NOT_FOUND_MEMBER);
        });

    if (!loginRequestDTO.getPassword().equals(user.getPassword())) {
      log.warn("Password validation failed for user: {}", loginRequestDTO.getEmail());
      throw new BusinessException(PASSWORD_CONFIRM_VALIDATION);
    }

    Authentication authentication = authenticationManagerBuilder
        .getObject()
        .authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

    UserDetails userDetails = (UserDetails) authentication.getPrincipal();

    TokenInfo tokenInfo = jwtTokenProvider.generateToken(userDetails);

    log.info("Generating token for user: {}", user.getEmail());

    return tokenInfo;
  }
}
```



### 정적 코드 분석 도구, SonarQube 추가
> 코드 품질 검사용 오픈 소스를 활용하여 버그 수정 및 유지보수성 등을 개선

![image](https://github.com/user-attachments/assets/9735e3d9-6599-4b65-af2f-8a376bcaec2d)
