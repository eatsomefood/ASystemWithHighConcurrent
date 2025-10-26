package com.star.highconcurrent.service.impl;

import com.star.highconcurrent.model.entity.Notification;
import com.star.highconcurrent.mapper.NotificationMapper;
import com.star.highconcurrent.service.NotificationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 通知表 服务实现类
 * </p>
 *
 * @author star
 * @since 2025-10-26
 */
@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

}
