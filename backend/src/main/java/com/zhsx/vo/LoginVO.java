package com.zhsx.vo;

import lombok.Data;

@Data
public class LoginVO {
    private String token;
    private String tokenType;
    private UserVO user;
}
