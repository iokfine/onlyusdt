package com.iokfine.data.modules.user.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.iokfine.data.exception.CommonException;
import com.iokfine.data.modules.security.domain.AuthUserDto;
import com.iokfine.data.modules.user.dao.model.User;
import com.iokfine.data.modules.user.dao.repository.UserRepository;
import com.iokfine.data.modules.security.domain.UserDto;
import com.iokfine.data.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author hjx
 * @date 2021/10/22
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public UserDto findById(long id) {
        return null;
    }

    @Override
    public UserDto findByName(String userName) {
        User user = userRepository.findByUserName(userName);
        if (user == null) {
            return null;
        } else {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(user,userDto);
            return userDto;
        }
    }

    @Override
    public void addUser(AuthUserDto authUser) {
        User target = new User();
        target.setUserName(authUser.getUsername());
        target.setCreateTime(new Date());
        target.setUpdateTime(new Date());
        target.setMail(authUser.getUsername());
        String salt = RandomUtil.randomString(6);
        target.setSecret(salt);
        target.setPassword(passwordEncoder.encode(authUser.getPassword()+salt));
        userRepository.save(target);
    }
}
