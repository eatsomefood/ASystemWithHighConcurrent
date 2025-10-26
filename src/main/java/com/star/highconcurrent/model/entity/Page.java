package com.star.highconcurrent.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Page {

    /**
     * 页数
     */
    private int pageNum;

    /**
     * 单页展示行数
     */
    private int pageSize;

}
