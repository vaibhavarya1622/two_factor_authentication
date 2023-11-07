package com.vaibhav.FA.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vaibhav.FA.config.ApiResponse;
import com.vaibhav.FA.service.OtpService;

@RestController
@RequestMapping("/auth/")
public class AuthenticationController {

    @Autowired
    private OtpService otpService;

    @GetMapping("getOtpThroughSms")
    public ResponseEntity<ApiResponse> sendOtpThroughSms(@RequestParam("user") String phone){
        int token = otpService.getOtpToPhone(phone);
        return new ResponseEntity<>(new ApiResponse(true,String.valueOf(token)), HttpStatus.OK);
    }

    @GetMapping("getOtpThroughEmail")
    public ResponseEntity<ApiResponse> sendOtpThroughEmail(@RequestParam("user") String email){
        int token = otpService.getOtpToEmail(email);
        return new ResponseEntity<>(new ApiResponse(true,String.valueOf(token)),HttpStatus.OK);
    }
    @GetMapping("verifyOtp")
    public ResponseEntity<ApiResponse> authenticate(@RequestParam String user,@Validated @RequestParam Integer token){
        if(otpService.verify(user,token))
            return new ResponseEntity<>(new ApiResponse(true,"Login Successful!!"),HttpStatus.OK);
        else{
            return new ResponseEntity<>(new ApiResponse(false,"the Otp doesn't match"),HttpStatus.UNAUTHORIZED);
        }
    }
}
