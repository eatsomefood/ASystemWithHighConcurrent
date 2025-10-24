package com.star.highconcurrent.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Login implements Serializable {

    private static final long serialVersionUID = 1L;

    private String email;

    private String password;

}
