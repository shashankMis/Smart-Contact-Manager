package com.MainApp.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.MainApp.Entity.Contact;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer>{
	//Pagination...
	@Query("from Contact as c where c.user.id =:userId")
	public List<Contact> findContactsByUser(@Param("userId") int userId);
}
