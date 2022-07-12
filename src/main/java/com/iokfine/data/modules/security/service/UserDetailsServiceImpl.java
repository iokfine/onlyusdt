/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.iokfine.data.modules.security.service;

import com.google.common.collect.Lists;
import com.iokfine.data.modules.security.domain.JwtUserDto;
import com.iokfine.data.modules.security.domain.properties.LoginProperties;
import com.iokfine.data.modules.security.domain.UserDto;
import com.iokfine.data.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Zheng Jie
 * @date 2018-11-22
 */
@RequiredArgsConstructor
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserService userService;
    private final LoginProperties loginProperties;

    /**
     * 用户信息缓存
     *
     * @see {@link UserCacheClean}
     */
    static Map<String, JwtUserDto> userDtoCache = new ConcurrentHashMap<>();

    @Override
    public JwtUserDto loadUserByUsername(String username) {
        boolean searchDb = true;
        JwtUserDto jwtUserDto = null;
        if (loginProperties.isCacheEnable() && userDtoCache.containsKey(username)) {
            jwtUserDto = userDtoCache.get(username);
            // 检查dataScope是否修改
//            List<Long> dataScopes = jwtUserDto.getDataScopes();
//            dataScopes.clear();
//            dataScopes.addAll(dataService.getDeptIds(jwtUserDto.getUser()));
            searchDb = false;
        }
        if (searchDb) {
            UserDto user;
            try {
                user = userService.findByName(username);
            } catch (EntityNotFoundException e) {
                // SpringSecurity会自动转换UsernameNotFoundException为BadCredentialsException
                throw new UsernameNotFoundException("", e);
            }
            if (user == null) {
                throw new UsernameNotFoundException("");
            } else {
                jwtUserDto = new JwtUserDto(user,Lists.newArrayList(),Lists.newArrayList());
                userDtoCache.put(username, jwtUserDto);
            }
        }
        return jwtUserDto;
    }
}
