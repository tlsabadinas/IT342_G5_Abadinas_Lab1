package com.citu.lab2.controller;

import com.citu.lab2.entity.User;
import com.citu.lab2.repository.UserRepository;
import com.citu.lab2.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth") // All URLs start with /api/auth
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    public AuthController (UserRepository userRepository){
        this.userRepository=userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @GetMapping("/hi")
    public String hello()
            {return "bayot";}


    // This fulfills: POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(user.getEmail())){
            return ResponseEntity.badRequest().body("Error: Email is already taken!");
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {

        // 1. Find user
        var userDB = userRepository.findByUsername(user.getUsername());

        if (userDB.isEmpty()) {
            return ResponseEntity.status(401).body("Error: User not found!");
        }

        // 2. Check Password
        if (!passwordEncoder.matches(user.getPassword(), userDB.get().getPassword())) {
            return ResponseEntity.status(401).body("Error: Invalid password!");
        }

        // 3. Generate Token
        String token = jwtService.generateToken(userDB.get().getUsername());

        // 4. Return Token in a JSON Object
        // Using a Map is the easiest way to return JSON without creating a new DTO class
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("message", "Login successful!");

        return ResponseEntity.ok(response);
    }

}