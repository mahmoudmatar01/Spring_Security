package org.example.spring_security.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.spring_security.dto.request.UserLoginRequestDto;
import org.example.spring_security.dto.request.UserRegistrationRequestDto;
import org.example.spring_security.dto.response.UserRegisterResponseDto;
import org.example.spring_security.entity.UserData;
import org.example.spring_security.enums.Role;
import org.example.spring_security.mapper.UserRegisterDtoToUserMapper;
import org.example.spring_security.mapper.UserToUserResponseDtoMapper;
import org.example.spring_security.repository.UserRepository;
import org.example.spring_security.service.AuthService;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


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

