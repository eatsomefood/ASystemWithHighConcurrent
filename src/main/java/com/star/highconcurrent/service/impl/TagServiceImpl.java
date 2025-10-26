package com.star.highconcurrent.service.impl;

import com.star.highconcurrent.model.entity.Tag;
import com.star.highconcurrent.mapper.TagMapper;
import com.star.highconcurrent.service.TagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 标签表 服务实现类
 * </p>
 *
 * @author star
 * @since 2025-10-26
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

}
