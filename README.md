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


### Entity 수정
* Auditing 기능 추가
  * 엔티티가 생성되고, 변경되는 시점을 감지하여 생성시각, 수정시각, 생성인, 수정인 등을 자동으로 기록
  * 여러 테이블의 공통 속성인 생성시각, 수정시각을 하나의 Entity(= BaseEntity)로 관리
```java
// 개선 전
@Entity
@Table(name="user")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
public class UserController2 {
    private final UserService2 userService;

    @PostMapping("/new")
    public String create(@RequestBody User user){
        Integer id = userService.create(user);
        return id.toString();
    }

    @GetMapping
    public ResponseEntity<?> getUser(@RequestParam String accessToken) {
        JSONObject user = userService.getUser(accessToken);
        return ResponseEntity.ok().body(user.toString());
    }
}
```


### Test Code 추가
```java
@SpringBootTest
@Transactional
public class UserServiceTest {
    @Autowired
    UserService2 userService;
    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("회원 가입")
    public void userSignUp() throws Exception {
        // given
        User user = new User();
        user.setStatus("M");
        user.setEmail("test@email.com");
        user.setPhone("01023456789");

        // when
        Integer createdId = userService.create(user);

        // then
        assertThat(user).isEqualTo(userRepository.findById(createdId));
    }
}
```



## 🤹마실가실 리팩토링 블로그
[🔗마실가실 리팰토링](https://hj0216.tistory.com/category/PlayGround/%EB%A7%88%EC%8B%A4%EA%B0%80%EC%8B%A4%20%EB%A6%AC%ED%8C%A9%ED%86%A0%EB%A7%81)