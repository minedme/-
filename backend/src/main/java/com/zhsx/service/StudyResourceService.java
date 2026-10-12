package com.zhsx.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhsx.entity.StudyResource;
import com.zhsx.mapper.StudyResourceMapper;
import com.zhsx.vo.ResourceVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyResourceService {

    private final StudyResourceMapper resourceMapper;

    public List<ResourceVO> listResources(String keyword, Integer type) {
        LambdaQueryWrapper<StudyResource> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(StudyResource::getTitle, keyword)
                    .or()
                    .like(StudyResource::getCategory, keyword));
        }
        if (type != null) {
            wrapper.eq(StudyResource::getType, type);
        }
        wrapper.orderByDesc(StudyResource::getRating);
        List<StudyResource> list = resourceMapper.selectList(wrapper);
        return list.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    public List<ResourceVO> recommendByGoal(String category) {
        return listResources(category, null);
    }

    private ResourceVO convertToVO(StudyResource resource) {
        ResourceVO vo = new ResourceVO();
        BeanUtils.copyProperties(resource, vo);
        String[] typeNames = {"", "电子书", "视频", "文章", "其他"};
        if (resource.getType() != null && resource.getType() >= 1 && resource.getType() <= 4) {
            vo.setTypeName(typeNames[resource.getType()]);
        } else {
            vo.setTypeName("其他");
        }
        return vo;
    }
}
