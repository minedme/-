package com.zhsx.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhsx.common.BusinessException;
import com.zhsx.dto.UserLoginDTO;
import com.zhsx.dto.UserRegisterDTO;
import com.zhsx.entity.User;
import com.zhsx.mapper.UserMapper;
import com.zhsx.security.JwtTokenProvider;
import com.zhsx.vo.LoginVO;
import com.zhsx.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public void register(UserRegisterDTO dto) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }
        if (dto.getEmail() != null) {
            wrapper.clear();
            wrapper.eq(User::getEmail, dto.getEmail());
            if (userMapper.selectCount(wrapper) > 0) {
                throw new BusinessException("邮箱已被注册");
            }
        }
        if (dto.getPhone() != null) {
            wrapper.clear();
            wrapper.eq(User::getPhone, dto.getPhone());
            if (userMapper.selectCount(wrapper) > 0) {
                throw new BusinessException("手机号已被注册");
            }
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setStatus(1);
        userMapper.insert(user);
    }

    public LoginVO login(UserLoginDTO dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getAccount(), dto.getPassword())
        );

        User user = userMapper.selectById(Long.valueOf(authentication.getName()));
        if (user == null || user.getStatus() != 1) {
            throw new BusinessException("用户不存在或已被禁用");
        }

        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername());

        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setTokenType("Bearer");
        vo.setUser(convertToVO(user));
        return vo;
    }

    public UserVO getProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return convertToVO(user);
    }

    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
