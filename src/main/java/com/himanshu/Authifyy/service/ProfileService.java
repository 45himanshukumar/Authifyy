package com.himanshu.Authifyy.service;

import com.himanshu.Authifyy.io.ProfileRequest;
import com.himanshu.Authifyy.io.ProfileResponse;

import java.util.List;

public interface ProfileService {
    ProfileResponse createProfile(ProfileRequest request);
    ProfileResponse getProfile(String email);
    void sentResetOtp(String email);
    void resetPassword(String email,String otp,String newPassword);
    void sentOtp(String email);
    void verifyOtp(String email,String otp);
    String getLoggedInUserId(String email);
    List<ProfileResponse> getAllUsers();
    ProfileResponse updateUser(String userId, ProfileRequest request);
    void deleteUser(String userId);

}
