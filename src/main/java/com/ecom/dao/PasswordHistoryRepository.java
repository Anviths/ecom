package com.ecom.dao;

import com.ecom.entity.PasswordEntityHistory;
import com.ecom.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PasswordHistoryRepository extends JpaRepository<PasswordEntityHistory ,Long> {

    List<PasswordEntityHistory> findTop5ByUserOrderByCreatedAtDesc(User user);

}
