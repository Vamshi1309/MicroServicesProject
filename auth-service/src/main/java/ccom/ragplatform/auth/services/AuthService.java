package ccom.ragplatform.auth.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ccom.ragplatform.auth.dto.AuthResponse;
import ccom.ragplatform.auth.dto.LoginRequest;
import ccom.ragplatform.auth.dto.RegisterRequest;
import ccom.ragplatform.auth.entity.User;
import ccom.ragplatform.auth.repository.UserRepo;
import ccom.ragplatform.auth.security.JwtUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
        private final AuthenticationManager authenticationManager;
        private final JwtUtil jwtUtil;
        private final UserRepo userRepo;
        private final PasswordEncoder passwordEncoder;

        public AuthResponse login(LoginRequest req) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));

                User user = userRepo.findByEmail(req.getEmail())
                                .orElseThrow(() -> new UsernameNotFoundException("user not found"));

                String token = jwtUtil.generateAccessToken(user.getId(),
                                user.getEmail());

                return new AuthResponse(token);
        }

        public void register(RegisterRequest req) {
                if (userRepo.findByEmail(req.getEmail()).isPresent()) {
                        throw new IllegalArgumentException("Email already registered");
                }

                User user = new User();

                user.setPassword(
                                passwordEncoder.encode(req.getPassword()));

                user.setEmail(req.getEmail());

                userRepo.save(user);
        }
}
