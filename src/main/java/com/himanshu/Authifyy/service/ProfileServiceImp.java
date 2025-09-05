package com.himanshu.Authifyy.service;

import com.himanshu.Authifyy.entity.UserEntity;
import com.himanshu.Authifyy.io.ProfileRequest;
import com.himanshu.Authifyy.io.ProfileResponse;
import com.himanshu.Authifyy.repositry.UserRepositry;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileServiceImp implements ProfileService{
    private final UserRepositry userRepositry;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    @Override
    public ProfileResponse createProfile(ProfileRequest request) {

        UserEntity newProfile=convertToUserEntity(request);
        if(!userRepositry.existsByEmail(request.getEmail())) {
            newProfile = userRepositry.save(newProfile);
            return convertToProfileResponse(newProfile);
        }
        throw new ResponseStatusException(HttpStatus.CONFLICT,"emial already exist");
    }

    @Override
    public ProfileResponse getProfile(String email) {
        UserEntity existinguser=  userRepositry.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("User not found "+email));
        return convertToProfileResponse(existinguser);
    }

    @Override
    public void sentResetOtp(String email) {
        UserEntity exitingEntity=  userRepositry.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("User not found"+email));

        //generate the 6 digit 0tp
        String otp=String.valueOf(ThreadLocalRandom.current().nextInt(100000,1000000));

        //caculate the expire date and time(currenttime+15 minuts in milisecond)
        long expiryTime=  System.currentTimeMillis()+(15*60*1000);
        //update the profile
        exitingEntity.setResetOtp(otp);
        exitingEntity.setResetOtpExpireAt(expiryTime);

        //save the database
        userRepositry.save(exitingEntity);

        try{
            //send the reset otp
            emailService.sendResetOtpEmail(exitingEntity.getEmail(),otp);
        }
        catch (Exception ex){
            throw new RuntimeException("Unable ro send email");

        }

    }

    @Override
    public void resetPassword(String email, String otp, String newPassword) {
        UserEntity exitingUser= userRepositry.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found"+email));
        if(exitingUser.getResetOtp()==null || ! exitingUser.getResetOtp().equals(otp)){
            throw new RuntimeException("Invalid Otp");
        }
        if(exitingUser.getResetOtpExpireAt()<System.currentTimeMillis()){
            throw new RuntimeException("Otp Expired");
        }

        exitingUser.setPassword(passwordEncoder.encode(newPassword));
        exitingUser.setResetOtp(null);
        exitingUser.setResetOtpExpireAt(0L);

        userRepositry.save(exitingUser);
    }

    @Override
    public void sentOtp(String email) {
        UserEntity existingUser= userRepositry.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found  "+email));

        if(existingUser.getIsAccountVerified()!=null&& existingUser.getIsAccountVerified()){
            return;
        }
        //generate the 6 digit otp
        String otp=String.valueOf(ThreadLocalRandom.current().nextInt(100000,1000000));

        //caculate the expire date and time(currenttime+15 minuts in milisecond)
        long expiryTime=  System.currentTimeMillis()+(24*24*60*1000);

        existingUser.setVerifyOtp(otp);
        existingUser.setVerifyOtpExpireAt(expiryTime);
        userRepositry.save(existingUser);

        try{
            emailService.sendOtpEmail(existingUser.getEmail(),otp);
        }
        catch (Exception e){
            throw  new RuntimeException("Unable to Send Email");
        }
    }

    @Override
    public void verifyOtp(String email, String otp) {
        UserEntity existingUser=  userRepositry.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found "+email));
        if(existingUser.getVerifyOtp()==null||!existingUser.getVerifyOtp().equals(otp)){
            return;
        }
        if(existingUser.getVerifyOtpExpireAt()<System.currentTimeMillis()){
            throw new RuntimeException("Otp Expired");
        }
        existingUser.setIsAccountVerified(true);
        existingUser.setVerifyOtp(null);
        existingUser.setVerifyOtpExpireAt(0L);

        userRepositry.save(existingUser);
    }

    @Override
    public String getLoggedInUserId(String email) {
        UserEntity existingUser=  userRepositry.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("user not found"+email));
        return existingUser.getUserId();
    }

    @Override
    public List<ProfileResponse> getAllUsers() {
        return userRepositry.findAll().stream()
                .map(this::convertToProfileResponse)
                .collect(Collectors.toList());
    }
    @Override
    public ProfileResponse updateUser(String userId, ProfileRequest request) {
        UserEntity existingUser = userRepositry.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

        existingUser.setName(request.getName());
        existingUser.setEmail(request.getEmail());
        existingUser.setIsAccountVerified(request.getIsAccountVerified());

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        UserEntity updatedUser = userRepositry.save(existingUser);
        return convertToProfileResponse(updatedUser);
    }

    @Override
    public void deleteUser(String userId) {
        UserEntity userToDelete = userRepositry.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
        userRepositry.delete(userToDelete);
    }

    private ProfileResponse convertToProfileResponse(UserEntity newProfile) {
        return ProfileResponse.builder()
                .name(newProfile.getName())
                .email(newProfile.getEmail())
                .userId(newProfile.getUserId())
                .isAccountVerified(newProfile.getIsAccountVerified())
                .build();
    }


    private UserEntity convertToUserEntity(ProfileRequest request) {
        System.out.println("Encoded Password: " + passwordEncoder.encode(request.getPassword()));
        return UserEntity.builder()
                .email(request.getEmail())
                .userId(UUID.randomUUID().toString())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .isAccountVerified(false)
                .resetOtpExpireAt(0L)
                .verifyOtp(null)
                .verifyOtpExpireAt(0L)
                .resetOtp(null)
                .build();


    }

}
