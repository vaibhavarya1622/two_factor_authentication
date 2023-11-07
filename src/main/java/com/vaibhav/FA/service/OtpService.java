package com.vaibhav.FA.service;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.vaibhav.FA.Repository.TokenRepository;
import com.vaibhav.FA.config.TwilioConfig;
import com.vaibhav.FA.model.VerificationToken;

@Service
public class OtpService {
    @Autowired
    private Environment env;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private TwilioConfig twilioConfig;
    @Autowired
    private TokenRepository tokenRepository;

    public Integer getOtpToPhone(String user){
        Integer otp;
        if(tokenRepository.existsById(user) && tokenRepository.findById(user).get().expiry()>0){
                otp = generateOtp(tokenRepository.findById(user).get().value());
        }
        else{
            VerificationToken verificationToken = new VerificationToken(user, TimeUnit.MINUTES.toSeconds(Long.parseLong(
                Objects.requireNonNull(env.getProperty("redis.ttl"),"redis.ttl not specified in application.properties"))),generateToken());
            tokenRepository.save(verificationToken);
            otp = generateOtp(verificationToken.value());
        }
        sendOtpToPhone(user,otp);
        return otp;
    }
    public Integer getOtpToEmail(String user){
        Integer otp;
        if(tokenRepository.existsById(user) && tokenRepository.findById(user).get().expiry()>0){
            otp = generateOtp(tokenRepository.findById(user).get().value());
        }
        else{
            VerificationToken verificationToken = new VerificationToken(user, TimeUnit.MINUTES.toSeconds(Long.parseLong(Objects.requireNonNull(env.getProperty("redis.ttl","redis.ttl not specified in application.properties")))),generateToken());
            tokenRepository.save(verificationToken);
            otp = generateOtp(verificationToken.value());
        }
        sendOtpToEmail(user,otp);
        return otp;
    }
    private void sendOtpToEmail(String user, Integer otp){
        final String subject = "Register with Otp";
        final String confirmationUrl = "http://localhost:8080/auth/verifyOtp?user="+user+"&token="+otp;
        final String message = "To complete registration, Please click on the below link\n"+confirmationUrl;
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(user);
        email.setSubject(subject);
        email.setText(message);
        email.setFrom(env.getProperty("support.email"));
        mailSender.send(email);
    }
    private void sendOtpToPhone(String phone,Integer otp){
        Twilio.init(twilioConfig.getAccountSid(),twilioConfig.getAuthToken());
        String countryCode = "+91";
        PhoneNumber to = new PhoneNumber(countryCode+phone);

        PhoneNumber from = new PhoneNumber(twilioConfig.getTrialNumber());
        String otpMessage = String.format("Dear Customer, Your OTP for test application is: %d",otp);
        Message.creator(to,from,otpMessage).create();
    }

    private Integer generateOtp(String token){
        return Math.abs(token.hashCode())%1000006;
    }
    private String generateToken(){
        return UUID.randomUUID().toString();
    }
    public boolean verify(String user, Integer otp){
        Optional<VerificationToken> verificationToken = tokenRepository.findById(user);
        return verificationToken.filter(token -> generateOtp(token.value()).equals(otp)).isPresent();
    }
}
