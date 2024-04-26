package com.MainApp.Controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.MainApp.Entity.Contact;
import com.MainApp.Entity.User;
import com.MainApp.Helper.Message;
import com.MainApp.Repository.ContactRepository;
import com.MainApp.Repository.UserRepository;
import com.MainApp.Service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	@Autowired
	private UserRepository uRepo;
	
	@Autowired
	private ContactRepository cRepo;
	
	@Autowired
	private UserService uService;
	
	//Home Page:
	@RequestMapping("/")
	public String home(Model model)
	{
		model.addAttribute("title", "Home - Smart Contact Manager");
		return "home";
	} 
	
	
	//About Page:
	@RequestMapping("/about")
	public String about(Model model)
	{
		model.addAttribute("title", "About - Smart Contact Manager");
		return "about";
	}
	
	
	//SignUp Page:
	@RequestMapping("/signup/")
	public String signup(Model model)
	{
		model.addAttribute("title", "Register - Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}
	
//	handler for register user:
	@RequestMapping(value="/do_register",method = RequestMethod.POST)
	public String registeruser(@ModelAttribute("user") User user, @RequestParam(value="aggrement",defaultValue="false") boolean aggrement, Model model,HttpSession session)
	{ 
		try {
			if(!aggrement)
			{
				System.out.println("You havn't agreed the terms and conditions");
				throw new Exception("You have not agreedthe terms and conditions");
			}
			
			user.setRole("Role_User");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			
			User result = this.uRepo.save(user);
			
			model.addAttribute("user", new User());
			session.setAttribute("message", new Message("Register Successfully!!", "alert-success"));
			
			model.addAttribute("title", "Register Page");
			return "signup";
			
			} 
			catch (Exception e) { 
				e.printStackTrace();
				model.addAttribute("user", user);
				session.setAttribute("message", new Message("Something Went wrong!!"+e.getMessage(), "alert-danger"));
				
				
				return "signup";
			}	
	}
	
	
	//Login Handler
	@RequestMapping("/login")
	public String customLogin(@ModelAttribute User u,Model model,HttpSession session)
	{		
		String s = uService.checkUsers(u.getEmail(), u.getPassword());
		if(s.equals("exists"))
		{
			String userName=u.getEmail();
			String password=u.getPassword();
			System.out.println("UserName is:"+userName);
			
			User user= uRepo.findByEmailAndPassword(userName, password);
			System.out.println(user);
			model.addAttribute("user", user);
			
			model.addAttribute("title","User DashBoard Page");
			return "normal/user_dashboard";
		}
		else {
			model.addAttribute("title","Login Page");
			return "login"; 
		}	
	}
	
	
	//UserDashboard for login user:
	@RequestMapping("/dashboard")
	public String userDashboard(@RequestParam(value="name") String name ,Model model)
	{		
		if (name != null && !name.isEmpty()) {
	        User user = new User();
	        user.setName(name);
	        model.addAttribute("user", user);
	    } else {
	    	System.out.println("Name is null");
	    }
			model.addAttribute("title", "User DashBoard page");
			return "normal/user_dashboard";	
	}
	
	
	//User Logout handler:
	@RequestMapping("/logout")
	public String logout(Model m)
	{
		m.addAttribute("title", "Home Page");
		return "home";
	}
	
	//User Add Contact Handler:
	@RequestMapping("/add-contact")
	public String openAddContactForm(@RequestParam(value="name") String name ,Model model)
	{
		
		if (name != null && !name.isEmpty()) {
	        User user = new User();
	        user.setName(name);
	        model.addAttribute("user", user);
	    }
		else {
	    	System.out.println("Name is null");
	    }
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		
		return "normal/add_contact";
	}
	
	
	//Save User add Contact handler:
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam(value="name") String name ,Model model)
	{
		String cleanedName = name.replaceFirst("^\\W+", "");
		String trimmedName = cleanedName.trim();
		if (trimmedName != null && !trimmedName.isEmpty()) {
			System.out.println("Name of the User is:"+trimmedName);
	        User user = new User();
	        user.setName(trimmedName);
	        
	        model.addAttribute("user", user);
	         
	        User user1 = uRepo.findByName(trimmedName);
	        if (user1 != null) { 
	            contact.setUser(user1);
	            user1.getContacts().add(contact);
	            uRepo.save(user1);
	            System.out.println("Added to database");
	        } else {
	            System.out.println("User not found with name: " + trimmedName);
	        }
		}
		else {
	    	System.out.println("Name is null");
	    }
		System.out.println("Data" + contact);
		return "normal/add_contact"; 
	}
	
	
	//User's Show Contacts handler:
	@RequestMapping("/show-contacts")
	public String showContacts(@RequestParam(value="name") String name,Model model)
	{
		if (name != null && !name.isEmpty()) {
	        User user = new User();
	        user.setName(name);
	        String userName = user.getName();
	        User user1 = this.uRepo.findByName(userName);
	        model.addAttribute("user", user);
	        List<Contact> contacts = this.cRepo.findContactsByUser(user1.getId());
	        model.addAttribute("contacts", contacts);
	        
	    }
		else {
	    	System.out.println("Name is null");
	    }
		model.addAttribute("title", "Show Contacts");
		
		return "normal/show_contacts";
	}
	
	
	
	//User's Delete Contact Handler:
	@RequestMapping("/delete")
	public String deleteContact(@RequestParam(value="name") String name,@RequestParam("cid") Integer cId, Model model)
	{
	    User user = new User();
	    user.setName(name);
	    model.addAttribute("user", user);
	        
		Contact contact = cRepo.findById(cId).get();
		System.out.println("Contact "+contact.getcId());
		
		cRepo.delete(contact);
		String userName = user.getName();
        User user1 = this.uRepo.findByName(userName);
        List<Contact> contacts = this.cRepo.findContactsByUser(user1.getId());
        model.addAttribute("contacts", contacts);
		
		return "normal/show_contacts";
	}
	
	
	//User's Update Contact Handler:
	@PostMapping("/update")
	public String Update(@RequestParam(value="name") String name,@RequestParam("cid") Integer cId,Model model)
	{
		 User user = new User();
		 user.setName(name);
		 model.addAttribute("user", user);
		        
		 Contact contact = cRepo.findById(cId).get();
		 model.addAttribute("contact",contact);
		 
		 model.addAttribute("title","update Contact");
		 return "normal/update_form";
	}
	
	
	
	// Save Update User's Contact handler:
	@RequestMapping(value="/process-update",method=RequestMethod.POST)
	public String updateProcess(@ModelAttribute Contact contact,@RequestParam(value="name") String name,Model m)
	{
		try {
			User user = new User();
	        user.setName(name);
	        
	        m.addAttribute("user", user);
	         
	        User user1 = uRepo.findByName(name);
	        contact.setUser(user1);
			
			cRepo.save(contact);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Contact Name:"+contact.getSecondName());
		System.out.println("Contact Name:"+contact.getSecondName());
		System.out.println("Contact Id:"+contact.getcId());
		return "normal/updated";
	}
	
	
	//User's Profile Handler:
	@GetMapping("/profile")
	public String profile(@RequestParam(value="name") String name ,Model m)
	{	
		User user = new User();
        user.setName(name);
        m.addAttribute("user", user);
        User user1 = uRepo.findByName(name);
        m.addAttribute("users", user1);
        
		m.addAttribute("title", "Profile Page");
		
		return "normal/profile";
	}
	
	
	// User's Setting:
	@RequestMapping("/setting")
	public String setting(@RequestParam(value="name") String name, Model m)
	{
		User user = new User();
        user.setName(name);
        m.addAttribute("user", user);
        
        m.addAttribute("title","Setting page");
		
		return "normal/setting";
	}
	
	
	
	//User Change password:
	@RequestMapping("/change-password")
	public String changepassword(@RequestParam(value="name") String name, Model m)
	{
		User user = new User();
        user.setName(name);
        m.addAttribute("user", user);
        
        m.addAttribute("title","Change password page");
        
        
		return "normal/change_password";
	}
	
	
	//New Password Page:
	@PostMapping("/new-password")
	public String checkpassword(@ModelAttribute User u  ,@RequestParam(value="name") String name, Model m)
	{
		User user = new User();
        user.setName(name);
        m.addAttribute("user", user);
        
        System.out.println("Email:"+u.getEmail() + " Password: "+u.getPassword());
        String s = uService.checkUsers(u.getEmail(), u.getPassword());
		if(s.equals("exists"))
		{
			String userName=u.getEmail();
			String password=u.getPassword();
			System.out.println("UserName is:"+userName);
			
			User user1= uRepo.findByEmailAndPassword(userName, password);
			System.out.println(user1);
			m.addAttribute("user", user1);
			
			m.addAttribute("title","New password Page");
			return "normal/new_password"; 
		}
		else {
			m.addAttribute("title","Change Password Page");
			return "normal/change_password"; 
		}	
        
 
	}
	
	
	//Updated Change password:
		@PostMapping("/changed-password")
		public String updatedpassword(@RequestParam(value="name") String name,@RequestParam(value="password") String newPassword, Model m)
		{
			User user = new User();
	        user.setName(name);
	        m.addAttribute("user", user);
	        
	        User user1 = uRepo.findByName(name);
	        user1.setPassword(newPassword);
	        uRepo.save(user1);
	        
	        m.addAttribute("title","Updated password page");
	        
	        
			return "normal/changed_password";
		}
		
}
