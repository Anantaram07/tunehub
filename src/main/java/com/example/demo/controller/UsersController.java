package com.example.demo.controller;






import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entities.PasswordResetForm;
import com.example.demo.entities.Users;
import com.example.demo.services.UsersService;

import jakarta.servlet.http.HttpSession;



@Controller
public class UsersController {
	@Autowired
	UsersService service;
	@PostMapping("/register")
	public String addUsers(@ModelAttribute Users user) {
		
		boolean userStatus=service.emailExists(user.getEmail());
		if(userStatus==false) {
			service.addUser(user);
			System.out.println("User added successfully!");
			return "login";
		}
		else {
			System.out.println("User already exists!");
		}
		
		return "index";
	}

	@PostMapping("/validate")
	public String validate(@RequestParam("email") String email, @RequestParam String password, HttpSession session, Model model) {
	    Users user = service.getUser(email);

	    if (user != null && service.validateUser(email, password)) {
	        String role = service.getRole(email);
	        session.setAttribute("email", email);

	        if (role.equals("admin")) {
	            return "adminHome";
	        } else {
	            boolean userStatus = user.isPremium();
	            model.addAttribute("isPremium", userStatus);
	            return "customerHome";
	        }
	    } else {
	        model.addAttribute("loginError", true);
	        return "login";
	    }
	}

	
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "index";
	}
	
	 @GetMapping("/forgotPassword")
	    public String showForgotPasswordForm(Model model) {
	        model.addAttribute("passwordResetForm", new PasswordResetForm());
	        return "passwordReset";
	    }

	 @PostMapping("/resetPassword")
	  public String resetPassword(@ModelAttribute PasswordResetForm form, Model model) {
	        String email = form.getEmail();
	        String newPassword = form.getPassword();
	        String confirmPassword = form.getConfirmPassword();

	        if (!newPassword.equals(confirmPassword)) {
	            model.addAttribute("passwordMismatch", true);
	            return "passwordReset";
	        }
	        Users user = service.getUser(email);
	        if (user != null) {
	            user.setPassword(newPassword);
	            service.updateUser(user);
	            model.addAttribute("success", true);
	            return "passwordReset";
	        } else {
	            model.addAttribute("userNotFound", true);
	            return "passwordReset";
	        }
	    }
	
	
}