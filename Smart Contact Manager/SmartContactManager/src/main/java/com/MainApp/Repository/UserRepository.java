package com.MainApp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.MainApp.Entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{
	
	User findByEmailAndPassword(String email,String password);
	
	User findByName(String name);
}
 