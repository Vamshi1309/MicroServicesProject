package ccom.ragplatform.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ccom.ragplatform.auth.security.JwtUtil;

@RestController
@RequestMapping("/auth/test")
public class JwtTestController {

    private final JwtUtil jwtUtil;

    public JwtTestController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/generate")
    public String generateToken() {

        return jwtUtil.generateAccessToken(
                25L,
                "vamshi@gmail.com");
    }
}
