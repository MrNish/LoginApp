package com.example.demo.controllers;

import com.example.demo.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.demo.services.UserService;

@Controller
@RequestMapping("/api")
public class UserController {
	
	@Autowired
	UserService userServ;
	
	@GetMapping("/")
	public String home() {
		return "register";
	}

	@PostMapping("/register")
	public String registerUser(@ModelAttribute User user) {
		String otp = userServ.registerUser(user);
		if (otp != null) {
			if (otp.equals("invalidUser")){
				return "invalid";
			}
			return "otpVerify";
		}
		return "invalid";
	}

	@Autowired
	JavaMailSender mailSender;

	@GetMapping("/test-email")
	public String testEmail() {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo("nishpatel0512@gmail.com");  // Replace with your own
			message.setSubject("Test Email from Render");
			message.setText("This is a test email from the deployed app.");
			mailSender.send(message);
			return "✅ Email sent successfully!";
		} catch (Exception e) {
			e.printStackTrace();
			return "❌ Failed to send email: " + e.getMessage();
		}
	}

	@GetMapping("/login")
	public String showLoginPage() {
		return "login";  // You must have a login.html template in /templates
	}
	
	@PostMapping("/login")
	public String authenticateUser(@ModelAttribute User user) {
		String role = userServ.authenticateUser(user.getEmail(), user.getPassword());
		if (role.equals("admin")) {
			return "admin";
		} else if (role.equals("customer")) {
			return "customer";
		} else {
			return "invalidUser";
		}
	}
	
	@PostMapping("/validateOtp")
	public String validateOtp(@RequestParam String otp) {
		String valid = userServ.validateOtp(otp);
		if (valid != null) {
			if (valid.equals("login")) {
				return "login";
			}
		}
		return "invalid";
	}
}
