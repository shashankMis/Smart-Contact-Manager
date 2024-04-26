package com.MainApp.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.MainApp.Entity.User;
import com.MainApp.Repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository uRepo;
	
	public String checkUsers(String email,String password)
	{
		User u= uRepo.findByEmailAndPassword(email,password);  
		if(u!=null)
		{
			return "exists";
		}
		else {
			return "not exists"; 
		}
	}
	
}
