package org.example.spring_security.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.spring_security.dto.request.RefreshTokenRequestDto;
import org.example.spring_security.dto.request.UserLoginRequestDto;
import org.example.spring_security.dto.request.UserRegistrationRequestDto;
import org.example.spring_security.dto.response.UserLoginResponseDto;
import org.example.spring_security.dto.response.UserRegisterResponseDto;
import org.example.spring_security.entity.User;
import org.example.spring_security.enums.Role;
import org.example.spring_security.mapper.UserRegisterDtoToUserMapper;
import org.example.spring_security.mapper.UserToUserResponseDtoMapper;
import org.example.spring_security.repository.UserRepository;
import org.example.spring_security.service.AuthService;
import org.example.spring_security.service.JWTService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.HashMap;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRegisterDtoToUserMapper userRegisterDtoToUserMapper;
    private final UserToUserResponseDtoMapper userResponseDtoMapper;

    @Override
    public UserRegisterResponseDto registerUser(UserRegistrationRequestDto registerRequest) {
        if(!registerRequest.password().equals(registerRequest.confirmPassword())){
            throw new RuntimeException("passwords aren't match");
        }
        User user = userRegisterDtoToUserMapper.apply(registerRequest);
        userRepository.save(user);
        return userResponseDtoMapper.apply(user);
    }

    @Override
    public UserRegisterResponseDto registerAdmin(UserRegistrationRequestDto adminDto) {
        if(!adminDto.password().equals(adminDto.confirmPassword())){
            throw new RuntimeException("passwords aren't match");
        }
        User user = userRegisterDtoToUserMapper.apply(adminDto, Role.ADMIN);
        userRepository.save(user);
        return userResponseDtoMapper.apply(user);
    }

    @Override
    public UserLoginResponseDto loginUser(UserLoginRequestDto loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email(),loginRequest.password()));

        User user = userRepository.findByEmail(loginRequest.email()).orElseThrow(
                ()-> new RuntimeException("user not found")
        );
        checkPasswordsMatch(loginRequest.password(), user.getPassword());

        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(new HashMap<>(),user);

        return UserLoginResponseDto.builder()
                .access_token(jwtToken)
                .refresh_token(refreshToken)
                .build();
    }

    @Override
    public UserLoginResponseDto refreshToken(RefreshTokenRequestDto refreshTokenRequestDto){
        String userEmail = jwtService.extractUserEmail(refreshTokenRequestDto.token());
        User user = userRepository.findByEmail(userEmail).orElseThrow(
                ()-> new NotFoundException("user not found")
        );

        if(jwtService.isTokenValid(refreshTokenRequestDto.token(),user)){
            var jwtToken = jwtService.generateToken(user);
            return UserLoginResponseDto.builder()
                    .access_token(jwtToken)
                    .refresh_token(refreshTokenRequestDto.token())
                    .build();
        }
        throw new RuntimeException("Token is invalid");
    }

    private void checkPasswordsMatch(String pass1,String pass2){
        if (!passwordEncoder.matches(pass1, pass2)) {
          throw new BadCredentialsException("Invalid password");
        }
   }

}

