package com.himanshu.Authifyy.controller;

import com.himanshu.Authifyy.io.ProfileRequest;
import com.himanshu.Authifyy.io.ProfileResponse;
import com.himanshu.Authifyy.service.EmailService;
import com.himanshu.Authifyy.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;
    private final EmailService emailService;
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponse register(@Valid @RequestBody ProfileRequest request){
        ProfileResponse response= profileService.createProfile(request);
        emailService.sendWelcomeEmail(response.getEmail(),response.getName());
        return response;
    }
    @GetMapping("/profile")
    public ProfileResponse getProfile(@CurrentSecurityContext(expression = "authentication?.name") String email){
        return profileService.getProfile(email);
    }

    @GetMapping("/users")
    public List<ProfileResponse> getAllUsers() {
        return profileService.getAllUsers();
    }

    // New endpoint to update a user by their userId
    @PutMapping("/users/{userId}")
    public ProfileResponse updateUser(@PathVariable String userId, @Valid @RequestBody ProfileRequest request) {
        return profileService.updateUser(userId, request);
    }

    // New endpoint to delete a user by their userId
    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String userId) {
        profileService.deleteUser(userId);
    }
}



