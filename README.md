# Spring Security README

## Overview

Security is a crucial aspect of software application design, ensuring that only authorized users can access secured resources. When it comes to securing an application, two fundamental aspects to consider are **authentication** and **authorization**.

- **Authentication:** The process of verifying the user's identity, typically by requesting credentials.
  
- **Authorization:** The process of verifying whether a user is allowed to perform a specific activity.

**Spring Security** is a powerful and flexible security framework designed for securing Java-based web applications. While commonly used with Spring-based applications, it can also be employed to secure non-Spring-based web applications.

## Spring Boot Security

Spring Security can be applied at various layers of an application, including web URLs and service layer methods. By adding the Spring Security starter (`spring-boot-starter-security`) to a Spring Boot application, you can:

1. Enable **HTTP basic security**.
2. Register the authentication manager bean with an in-memory store and a single user.
3. Ignore paths for commonly used static resource locations (e.g., `/css/**`, `/js/**`, `/images/**`).
4. Enable common low-level features such as XSS, CSRF, caching, etc.

## Security Concepts

### Authentication

Authentication involves checking the user ID and password against credentials stored in the application or a database.

### Authorization

Authorization checks whether a user has the authorized role to perform a specific activity.

## Declarative Security

Spring Security supports declarative security, allowing you to define the application's security constraints in configuration:

- All Java config (`@Configuration`, no XML).
- Spring XML config.

This approach provides a clear separation of concerns between application code and security, making it easier to manage and maintain.

## Getting Started
In the world of web development, security is of utmost importance. Building a robust authentication and authorization system is essential to protect sensitive user data and resources. This tutorial will guide you through the process of creating a secure web application using the Spring Framework, specifically leveraging Spring Boot, Spring Security, and the Java Persistence API (JPA).

### Setting up the Project

The first step in building our secure Spring application is to set up the project. We’ll use Spring Initializr, a web-based tool for generating Spring Boot projects with all the required dependencies. Follow these steps:

1. **Access Spring Initializr:**
   Open your web browser and navigate to [Spring Initializr](https://start.spring.io/).

2. **Project Configuration:**
   - Choose the latest stable version of Spring Boot.
   - Set the project type to “Maven” or “Gradle,” depending on your preference.
   - Define the project’s metadata, such as the group, artifact, and name.

3. **Dependencies:**
   In the “Dependencies” section, search for and add the following dependencies:
   - “Spring Web” for building web applications.
   - “Spring Security” for securing our application.
   - “Spring Data JPA” for data access and persistence.
     
 ```xml
 <dependency>
     <groupId>org.springframework.boot</groupId>
     <artifactId>spring-boot-starter-security</artifactId>
 </dependency>
```

4. **Generate the Project:**
   Click the “Generate” button to create your project archive (ZIP file).


 <img src="https://miro.medium.com/v2/resize:fit:828/format:webp/1*kODiNBLonXUX__jgiZS5yw.png" alt="Generate Project">
 Spring intializr

Now that we have our project set up, unzip the archive and open it in your chosen development environment. We’re ready to start building our secure Spring application.

In the next section, we’ll dive into creating the database model for our application, defining the entities, and using JPA for data persistence.

### Creating the Database Model
With our Spring project set up, it’s time to define the data model for our application. In this section, we’ll create the necessary entity classes and annotate them with JPA annotations for data persistence.

#### Defining the User Entity
The first entity we’ll define is the User entity, which will represent user data in our application. Here's an example of how to create a simple User entity:

```java
@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UserData implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Role role;
    private String accessToken;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return firstName+lastName;
    }

    @Override
    public String getPassword(){
        return password;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
```

In this example, we’ve used JPA annotations to mark the class as an entity `(@Entity)`. The `@Id` annotation designates the primary key.


### Defining the UserRepository
Create the UserRepository Interface: Create the UserRepository interface and extend `JpaRepository<User, Long>`. This interface extends Spring Data JPA's `JpaRepository` interface and provides various methods for common database operations.

```java
@Repository
public interface UserRepository extends JpaRepository<UserData,Long> {
    Optional<UserData>findByEmail(String email);
}
```

In this example:

- `@Repository` annotation indicates that this interface is a Spring repository.
- `JpaRepository<User, Long>` specifies that this repository deals with the `User` entity, and the primary key of the entity is of type `Long`.

### Configuring Spring Security
Spring Security’s power lies in its flexibility and configurability. In this section, we’ll demonstrate how to configure Spring Security to protect your application and specify access rules.

#### Configuration Class
In your Spring Boot project, create a configuration class to customize Spring Security settings. You can do this by extending` WebSecurityConfigurerAdapter` and overriding its methods. Here's a basic example:

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter authFilter;
    private final AuthenticationProvider authenticationProvider;


    @Bean
    public SecurityFilterChain notAuthenticatedFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request ->
                        request.requestMatchers(
                                        "/api/v*/**"
                                ).permitAll()
                                .anyRequest()
                                .authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(
                        authFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

}
```

In this example:

- `@Configuration` indicates that this class contains Spring configuration.
- `@EnableWebSecurity` enables Spring Security for the application.
- The configure`(HttpSecurity http)` method defines access rules and authentication settings. In this case:
- The root and `/home` paths are accessible to everyone `(permitAll())`.
- The `/admin/**` path is restricted to users with the` “ADMIN” `role.
- All other requests require authentication.
- A custom` login` page is specified, and `logout` is permitted.

### UserDetailsService

To load user details from your database, you can create a custom `UserDetailsService` implementation. Here's a simplified example:

```java
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtServiceImpl jwtTokenUtils;
    private final UserRegisterDtoToUserMapper userRegisterDtoToUserMapper;
    private final UserToUserResponseDtoMapper userResponseDtoMapper;

    @Override
    public UserRegisterResponseDto registerUser(UserRegistrationRequestDto registerRequest) {
        if(!registerRequest.password().equals(registerRequest.confirmPassword())){
            throw new RuntimeException("passwords aren't match");
        }
        UserData user = userRegisterDtoToUserMapper.apply(registerRequest);
        userRepository.save(user);
        return userResponseDtoMapper.apply(user);
    }

    @Override
    public UserRegisterResponseDto registerAdmin(UserRegistrationRequestDto adminDto) {
        if(!adminDto.password().equals(adminDto.confirmPassword())){
            throw new RuntimeException("passwords aren't match");
        }
        UserData user = userRegisterDtoToUserMapper.apply(adminDto, Role.User_Admin);
        userRepository.save(user);
        return userResponseDtoMapper.apply(user);
    }
    @Override
    public String loginUser(UserLoginRequestDto loginRequest) {
        UserData user = userRepository.findByEmail(loginRequest.email()).orElseThrow(
                ()-> new RuntimeException("user not found")
        );
        checkPasswordsMatch(loginRequest.password(), user.getPassword());
        String jwtToken = jwtTokenUtils.generateToken(user);
        user.setAccessToken(jwtToken);
        user=userRepository.save(user);
        return user.getAccessToken();
    }

  private void checkPasswordsMatch(String pass1,String pass2){
      if (!passwordEncoder.matches(pass1, pass2)) {
          throw new BadCredentialsException("Invalid password");
      }
  }


}
```

In this example, we retrieve the user details from the database using a custom `UserRepository` and build a` UserDetails` object.


### Password Encoding

Security best practices recommend hashing passwords. You can configure Application Config to use a specific password encoder. For example, using `BCrypt`:

```java
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return email ->
                userRepository.findByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException("There is no user with that name"));
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }


    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http)
            throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authenticationProvider());
        return authenticationManagerBuilder.build();
    }



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}

```

### Creating Controllers
Now that we have our database model defined, it’s time to create controllers to handle incoming HTTP requests and interact with our data. In a Spring application, controllers are responsible for processing requests, invoking the necessary business logic, and returning responses.

#### Spring MVC Controllers
Spring provides a powerful and flexible web framework called Spring `MVC (Model-View-Controller)` for building web applications. In Spring MVC, controllers are typically annotated with `@Controller` and handle specific request mappings.

Here’s an example of a simple controller for managing user-related operations:

```java
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(value= "/user/register", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> registerUser(@ModelAttribute UserRegistrationRequestDto registerRequest) {

        var registeredUser = authService.registerUser(registerRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(registeredUser);
    }

    @PostMapping(value= "/admin/register", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> registerAdmin(@ModelAttribute UserRegistrationRequestDto registerRequest) {

        var admin = authService.registerAdmin(registerRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(admin);
    }

    @PostMapping(value = "/login", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> loginUser( @ModelAttribute UserLoginRequestDto loginRequest) {

        String authToken = authService.loginUser(loginRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(authToken);
    }

}
```
