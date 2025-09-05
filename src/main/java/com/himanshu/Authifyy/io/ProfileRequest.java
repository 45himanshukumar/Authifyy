package com.himanshu.Authifyy.io;

import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@AllArgsConstructor
public class ProfileRequest {
    @NotBlank(message = "Name should be not empty")
    private String name;
    @Email(message = "Enter valid email address")
    @NotBlank(message ="Email should not be empty" )
    private String email;
    @Size(min = 6,message = "password most be atleast 6 character")
    private String password;

    private Boolean isAccountVerified;
}
