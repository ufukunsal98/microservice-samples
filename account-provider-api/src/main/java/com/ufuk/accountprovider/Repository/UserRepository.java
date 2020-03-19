package com.ufuk.accountprovider.Repository;

import com.ufuk.accountprovider.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<Users, String> {

    List<Users> findByUsername(String username);

}