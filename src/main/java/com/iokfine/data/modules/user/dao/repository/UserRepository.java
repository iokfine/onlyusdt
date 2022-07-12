package com.iokfine.data.modules.user.dao.repository;

import com.iokfine.data.modules.user.dao.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


/**
 * @author hjx
 * @date 2021/10/18
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

    /**
     * 根据用户名查询
     * @param username 用户名
     * @return /
     */
    User findByUserName(String username);
}
