package com.synchrony.myapp.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.synchrony.myapp.dao.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{

	User findByUserName(String userName);
	
}
