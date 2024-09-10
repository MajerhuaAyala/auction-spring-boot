package com.auction.auction.service;

import com.auction.auction.dto.req.UserRequestDto;
import com.auction.auction.dto.res.UserResponseDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    List<UserResponseDto> getAllUser();
    public UserResponseDto createUser(UserRequestDto userRequestDto);
}
