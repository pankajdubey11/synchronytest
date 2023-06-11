package com.synchrony.myapp.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.synchrony.myapp.dao.model.UserImage;

@Repository
public interface UserImageRepository extends JpaRepository<UserImage, Integer>{

	Integer deleteByUserIdAndImgurImageDeleteHash(Integer userId, String imgurImageDeleteHash);
	
	List<UserImage> findByUserId(Integer userId);
	
}
