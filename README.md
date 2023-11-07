# two_factor_authentication
This application will send you a One-Time Password (OTP) code either via SMS or email. By default, the OTP will be valid for 5 minutes, but you can modify this setting in the application.properties file.
The OTP codes are hashed and stored in Redis. Therefore, it's necessary to have Redis installed and running before you can run the application.
