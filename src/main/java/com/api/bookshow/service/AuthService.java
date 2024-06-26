package com.api.bookshow.service;

import com.api.bookshow.dto.AuthenticationRequest;
import com.api.bookshow.dto.AuthenticationResponse;
import com.api.bookshow.dto.RegisterRequest;
import com.api.bookshow.exception.AgeNotAllowedException;
import com.api.bookshow.model.Role;
import com.api.bookshow.model.Users;
import com.api.bookshow.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.List;

@Service
public class AuthService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponse register(RegisterRequest request) {
        Role role = request.getRole() == 1 ? Role.ADMIN : Role.USER;
        Users user;
        ObjectMapper mapper = new ObjectMapper();
        try {
            user = mapper.readValue(mapper.writeValueAsString(request), Users.class);
            LocalDate dob = user.getDob().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int ageInYears = Period.between(dob, LocalDate.now()).getYears();
            logger.info("ageInYears: {}", ageInYears);
            if (ageInYears <= 18) throw new AgeNotAllowedException("Age not allowed");
        } catch (JsonProcessingException | AgeNotAllowedException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        if (userRepository.findByUsername(request.getUsername()).isEmpty()) {
            userRepository.save(user);
        }
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role.name()));
        String jwtToken = jwtService.generateToken(new User(user.getUsername(), user.getPassword(), user.getAuthorities()));

        return new AuthenticationResponse(jwtToken);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()));
        Users user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));
        var jwtToken = jwtService.generateToken(new User(user.getUsername(), user.getPassword(), user.getAuthorities()));
        return new AuthenticationResponse(jwtToken);
    }
}
