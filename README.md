# Spring Security README

## Overview

Security is crucial to software application design, ensuring only authorized users can access secured resources. Two fundamental aspects to consider when securing an application are authentication and authorization.

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
 Spring initializer

Now that we have our project set up, unzip the archive and open it in your chosen development environment. We’re ready to start building our secure Spring application.

In the next section, we’ll dive into creating the database model for our application, defining the entities, and using JPA for data persistence.

### Creating the Database Model
With our Spring project set up, it’s time to define the data model for our application. In this section, we’ll create the necessary entity classes and annotate them with JPA annotations for data persistence.

#### Defining the User Entity
The first entity we’ll define is the User entity, which will represent user data in our application. Here's an example of how to create a simple User entity:

```java

@Entity
@Table(name = "USERS",  indexes = {
        @Index(name = "idx_user_email", columnList = "email", unique = true),
        @Index(name = "idx_user_uuid", columnList = "uuidCode", unique = true)}
)
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
@Getter
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private UUID uuidCode;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Role role;

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
public interface UserRepository extends JpaRepository<User,Long> {
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
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter authFilter;
    private final AuthenticationProvider authenticationProvider;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain authenticatedFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request ->
                        request.requestMatchers(
                                        "/api/v1/auth/**",
                                        "/v3/api-docs/**",
                                        "/swagger-ui/**"
                                ).permitAll()
                                .requestMatchers("/api/v1/user").hasAnyAuthority(Role.USER.name())
                                .requestMatchers("/api/v1/admin").hasAnyAuthority(Role.ADMIN.name())
                                .anyRequest()
                                .authenticated())
                .exceptionHandling(e->e.accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
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
