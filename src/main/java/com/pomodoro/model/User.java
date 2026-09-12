package com.pomodoro.model;

import com.pomodoro.util.DateUtil;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户实体类，封装系统用户的账号信息
 */
public class User implements Serializable {

    /** 序列化版本号 */
    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private String id;

    /** 用户名 */
    private String username;

    /** 密码（存储经哈希处理后的值） */
    private String password;

    /** 账号创建时间 */
    private Date createTime;

    /**
     * 无参构造方法
     */
    public User() {
    }

    /**
     * 全参构造方法
     */
    public User(String id, String username, String password, Date createTime) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.createTime = createTime;
    }

    /**
     * 获取用户ID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置用户ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取账号创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置账号创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 返回用户对象的字符串表示形式
     */
    @Override
    public String toString() {
        return "User{id='" + id + "', username='" + username
                + "', createTime=" + DateUtil.formatDateTime(createTime) + "}";
    }
}
