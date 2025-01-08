package com.techlabs.service;

import com.techlabs.entity.Role;
import com.techlabs.entity.User;
import com.techlabs.exception.StudentApiException;
import com.techlabs.dto.LoginDto;
import com.techlabs.dto.RegisterDto;
import com.techlabs.repository.RoleRepository;
import com.techlabs.repository.UserRepository;
import com.techlabs.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public String login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getMobileNumber(),
                        loginDto.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtTokenProvider.generateToken(authentication);
    }

    @Override
    public String register(RegisterDto registerDto) {
        // Validate fields
        if (registerDto.getFirst_name() == null || registerDto.getFirst_name().trim().isEmpty()) {
            throw new StudentApiException(HttpStatus.BAD_REQUEST, "First name is required.");
        }
        if (registerDto.getLast_name() == null || registerDto.getLast_name().trim().isEmpty()) {
            throw new StudentApiException(HttpStatus.BAD_REQUEST, "Last name is required.");
        }
        if (registerDto.getMobile_number() == null || registerDto.getMobile_number().trim().isEmpty()) {
            throw new StudentApiException(HttpStatus.BAD_REQUEST, "Mobile number is required.");
        }

        // Check if mobile number exists
        if (userRepository.existsByMobileNumber(registerDto.getMobile_number())) {
            throw new StudentApiException(HttpStatus.BAD_REQUEST, "Mobile number already exists!");
        }

        // Map RegisterDto to User entity
        User user = new User();
        user.setFirstName(registerDto.getFirst_name());
        user.setLastName(registerDto.getLast_name());
        user.setMobileNumber(registerDto.getMobile_number());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        // Assign default role
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("ROLE_USER").orElseThrow(
                () -> new StudentApiException(HttpStatus.BAD_REQUEST, "Default role not found!")
        );
        roles.add(userRole);
        user.setRoles(roles);

        // Save user
        userRepository.save(user);

        return "User registered successfully!";
    }
}
