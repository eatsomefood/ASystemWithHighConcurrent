package com.star.highconcurrent.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LikeRecordDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;

    private Integer targetType;

    private Long targetId;

    private Integer status;

}
