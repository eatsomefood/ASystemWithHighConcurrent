package com.star.highconcurrent.esdao;


import com.star.highconcurrent.model.dto.QuestionEsDTO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * 题目 ES 操作
 *
*/
public interface QuestionEsDao extends ElasticsearchRepository<QuestionEsDTO, Long> {

    List<QuestionEsDTO> findByUserId(Long userId);
}