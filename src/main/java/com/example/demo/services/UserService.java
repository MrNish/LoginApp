package com.example.demo.services;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.demo.entities.User;
import com.example.demo.repositories.UserRepository;

@Service
public class UserService {
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	JavaMailSender mailSender;

	public String registerUser(User user) {
		userRepo.save(user);
		return generateOtp(user.getEmail());
	}

	public String authenticateUser(String email, String password) {
		User user = userRepo.findByEmail(email);
		if (user != null && password.equals(user.getPassword())) {
			return user.getRole();
		} 
		return "invalidUser";
	}
	
	public String generateOtp(String email) {
		try {
			Random random = new Random();
			int randomOtp = 100000 + random.nextInt(900000);
			String otp = String.valueOf(randomOtp);
			User user = userRepo.findByEmail(email);
			if (user != null) {
				user.setOtp(otp);
				user.setOtpGeneratedTime(LocalDateTime.now());
				userRepo.save(user);
				
				String mail = user.getEmail();
				SimpleMailMessage message = new SimpleMailMessage();
				message.setTo(mail);
				message.setSubject("OTP for Verification");
				message.setText("Your OTP is: " + otp);
				mailSender.send(message);
				return otp;
			}
		} catch (Exception e) {
			return "Invalid";
		}
		return "Invalid";
	}
	
	public String validateOtp(String otp) {
		User user = userRepo.findByOtp(otp);
		if (user != null && otp.equals(user.getOtp())) {
			if (user.getOtpGeneratedTime() != null &&
		            LocalDateTime.now().isBefore(user.getOtpGeneratedTime().plusMinutes(1))) {
		            
		            return "login"; // OTP is valid, return role
		        } else {
		            return "Expired"; // OTP has expired
		        }
		}
		return "Invalid OTP";
	}


}
