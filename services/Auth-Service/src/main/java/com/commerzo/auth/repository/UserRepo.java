package com.commerzo.auth.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.commerzo.auth.model.Users;


@Repository
public interface UserRepo extends JpaRepository<Users,Long>{
	Users findByUsername(String username);

	Optional<Users> findById(Long id);

	boolean existsById(Long id);

	void deleteById(Long id);

	List<Users> getUsersByUsername(String username);
}
	