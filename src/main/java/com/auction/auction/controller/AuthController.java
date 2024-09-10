package com.auction.auction.controller;

import com.auction.auction.Auth.JwtHelper;
import com.auction.auction.dto.req.JwtRequest;
import com.auction.auction.dto.res.JwtResponse;
import com.auction.auction.dto.res.UserResponseDto;
import com.auction.auction.exp.UserAlreadyExistsException;
import com.auction.auction.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.auction.auction.config.AuthConfig;
import com.auction.auction.dto.req.UserRequestDto;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthConfig authConfig;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private AuthenticationManager manager;
    @Autowired
    private JwtHelper helper;

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<JwtResponse> createUser(@RequestBody UserRequestDto userRequestDto) {
        try {
            UserResponseDto userResponseDto = userService.createUser(userRequestDto);
            UserDetails userDetails = userDetailsService.loadUserByUsername(userResponseDto.getEmail());
            String token = this.helper.generateToken(userDetails);
            JwtResponse jwtResponse = JwtResponse.builder().token(token).build();
            return new ResponseEntity<>(jwtResponse, HttpStatus.CREATED);
        } catch (UserAlreadyExistsException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new JwtResponse("User already exists: " + ex.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest jwtRequest) {
        this.doAuthenticate(jwtRequest.getEmail(), jwtRequest.getPassword());
        UserDetails userDetails = userDetailsService.loadUserByUsername(jwtRequest.getEmail());
        System.out.println("UserDetails: " + userDetails);
        String token = this.helper.generateToken(userDetails);
        System.out.println("Token: " + token);
        JwtResponse jwtResponse = JwtResponse.builder().token(token).build();
        System.out.println("jwtResponse: " + jwtRequest);
        return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
    }

    private void doAuthenticate(String email, String password) {
        System.out.println("Login Info");
        System.out.println(email);
        System.out.println(password);
        System.out.println("------");

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);

        System.out.println(authenticationToken + "  do authenticarte");
        try {
            System.out.println("Started");
            manager.authenticate(authenticationToken);
            System.out.println("Eneded");
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            System.out.println("Authentication successful for user: " + email);
        } catch (BadCredentialsException e) {
            System.out.println("Authentication not-successful for user: " + email);
            throw new BadCredentialsException(" Invalid Username or Password");
        }
    }
}
