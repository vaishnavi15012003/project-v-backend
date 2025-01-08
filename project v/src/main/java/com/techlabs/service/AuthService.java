package com.techlabs.service;

import com.techlabs.dto.LoginDto;
import com.techlabs.dto.RegisterDto;

public interface AuthService {
    String login(LoginDto loginDto);

    String register(RegisterDto registerDto);
}
