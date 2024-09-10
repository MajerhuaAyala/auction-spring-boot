package com.auction.auction.service.impl;

import com.auction.auction.config.AuthConfig;
import com.auction.auction.dto.req.UserRequestDto;
import com.auction.auction.dto.res.UserResponseDto;
import com.auction.auction.entity.UserEntity;
import com.auction.auction.exp.UserAlreadyExistsException;
import com.auction.auction.repo.UserRepo;
import com.auction.auction.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserImpl implements UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthConfig authConfig;

    @Override
    public List<UserResponseDto> getAllUser() {
        return List.of();
    }

    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        Optional<UserEntity> foundUser = this.userRepo.findByEmail(userRequestDto.getEmail());

        if (foundUser.isPresent()) {
            throw new UserAlreadyExistsException("User with email " + userRequestDto.getEmail() + " already exists");
        }

        UserEntity user = this.userReqDtoToUserEntity(userRequestDto);
        user.setPassword(authConfig.passwordEncoder().encode(user.getPassword()));
        UserEntity createdUser = userRepo.save(user);
        return this.userEntityToUserRespDto(createdUser);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public UserEntity userReqDtoToUserEntity(UserRequestDto userRequestDto) {
        return this.modelMapper.map(userRequestDto, UserEntity.class);
    }

    public UserResponseDto userEntityToUserRespDto(UserEntity user) {
        return this.modelMapper.map(user, UserResponseDto.class);
    }
}
