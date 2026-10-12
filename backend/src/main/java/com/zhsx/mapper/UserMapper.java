package com.zhsx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhsx.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
