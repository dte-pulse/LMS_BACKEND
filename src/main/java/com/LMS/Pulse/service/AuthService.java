package com.LMS.Pulse.service;

import com.LMS.Pulse.Dto.*;
import com.LMS.Pulse.model.Role;
import com.LMS.Pulse.model.User;
import com.LMS.Pulse.model.UserGroupMapping;
import com.LMS.Pulse.repository.UserGroupMappingRepository;
import com.LMS.Pulse.repository.UserRepository;
import com.LMS.Pulse.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserGroupMappingRepository userGroupMappingRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Value("${jwt.cookieName}")
    private String jwtCookieName;

    @Transactional
    public ResponseCookie register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalStateException("Error: Username is already taken!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Error: Email is already in use!");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtTokenProvider.generateToken(authentication);
        return createSecureJwtCookie(jwt);
    }

    public ResponseCookie login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtTokenProvider.generateToken(authentication);
        return createSecureJwtCookie(jwt);
    }

    @Transactional
    public UserDetailsDto updateProfile(UserUpdateDto dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setDateOfJoining(dto.getDateOfJoining());

        User updatedUser = userRepository.save(user);
        return convertToUserDetailsDto(updatedUser);
    }

    @Transactional(readOnly = true)
    public UserDetailsDto getUserDetails(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return convertToUserDetailsDto(user);
    }

    private UserDetailsDto convertToUserDetailsDto(User user) {
        Optional<UserGroupMapping> groupMapping = userGroupMappingRepository.findByEmail(user.getEmail());
        return new UserDetailsDto(
                user.getUsername(),
                user.getEmail(),
                user.getDateOfJoining(),
                groupMapping,
                user.getRole()
        );
    }

    private ResponseCookie createSecureJwtCookie(String jwt) {
        return ResponseCookie.from(jwtCookieName, jwt)
                .path("/api").httpOnly(true).secure(true)
                .sameSite("None").maxAge(24 * 60 * 60).build();
    }
}
