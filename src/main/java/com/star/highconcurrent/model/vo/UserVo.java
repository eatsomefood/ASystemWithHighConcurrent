package com.star.highconcurrent.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;

    private String userName;

    private String nickName;

    private String avatar;

}
