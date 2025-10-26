package com.star.highconcurrent.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LikeRecordDto {

    private Long userId;

    private Integer targetType;

    private Long targetId;

    private Integer status;

}
