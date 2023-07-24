package com.simple.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 测试用
 */
@TableName(value ="user")
@Data
public class BaseStat implements Serializable {
    @TableField(value = "id")
    private String id;
    private String name;

}
