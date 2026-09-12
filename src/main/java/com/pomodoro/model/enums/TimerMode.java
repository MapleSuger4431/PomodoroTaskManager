package com.pomodoro.model.enums;

/**
 * 计时模式枚举，区分倒计时与正计时两种番茄钟计时方式
 */
public enum TimerMode {

    /** 倒计时模式 */
    COUNTDOWN(0),

    /** 正计时模式 */
    COUNT_UP(1);

    /** 计时模式对应的编码，与数据库中存储的 int 值一致 */
    private final int code;

    /**
     * 通过编码构造计时模式枚举值
     */
    TimerMode(int code) {
        this.code = code;
    }

    /**
     * 获取计时模式编码
     */
    public int getCode() {
        return code;
    }

    /**
     * 根据编码查找对应的计时模式，找不到时返回 COUNTDOWN
     */
    public static TimerMode fromCode(int code) {
        for (TimerMode mode : values()) {
            if (mode.code == code) {
                return mode;
            }
        }
        return COUNTDOWN;
    }
}
