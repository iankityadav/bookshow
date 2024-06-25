package com.api.bookshow.service;

import com.api.bookshow.dto.AuthenticationRequest;
import com.api.bookshow.dto.AuthenticationResponse;
import com.api.bookshow.dto.RegisterRequest;
import com.api.bookshow.model.Role;
import com.api.bookshow.model.Users;
import com.api.bookshow.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

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
        Role role = request.getRole() == 1 ? Role.USER : Role.ADMIN;
        Users user = new Users();
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUsername(request.getUsername());
        user.setName(request.getName());
        user.setRole(role);
        if(userRepository.findByUsername(request.getUsername()).isEmpty()){
            userRepository.save(user);
        }
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role.name()));
        System.out.println("authorities = " + authorities);
        String jwtToken = jwtService.generateToken(new User(user.getUsername(), user.getPassword(),user.getAuthorities()));

        return new AuthenticationResponse(jwtToken);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()));
        Users user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));
        System.out.println("authorities = " + authorities);
        var jwtToken = jwtService.generateToken(new User(user.getUsername(), user.getPassword(),user.getAuthorities()));
        return new AuthenticationResponse(jwtToken);
    }
}
