package com.zhsx.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhsx.entity.User;
import com.zhsx.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
        User user;
        try {
            Long id = Long.parseLong(account);
            user = userMapper.selectById(id);
        } catch (NumberFormatException e) {
            user = null;
        }
        if (user == null) {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getUsername, account)
                    .or(w -> w.eq(User::getEmail, account))
                    .or(w -> w.eq(User::getPhone, account));
            user = userMapper.selectOne(wrapper);
        }
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return new org.springframework.security.core.userdetails.User(
                String.valueOf(user.getId()),
                user.getPassword(),
                user.getStatus() == 1,
                true, true, true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
