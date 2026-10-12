package com.zhsx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhsx.entity.StudyReminder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface StudyReminderMapper extends BaseMapper<StudyReminder> {

    @Select("SELECT * FROM study_reminder WHERE is_sent = 0 AND remind_time <= #{now} AND deleted = 0")
    List<StudyReminder> selectPendingReminders(LocalDateTime now);
}
