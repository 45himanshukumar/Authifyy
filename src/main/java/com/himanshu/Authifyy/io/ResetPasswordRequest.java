package com.himanshu.Authifyy.io;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.xml.stream.XMLEventWriter;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest {
    @NotBlank(message = "New Password is Required")
    private String newPassword;
    @NotBlank(message = "Otp is required")
    private String otp;
    @NotBlank(message = "Email is required")
    private String email;
}
