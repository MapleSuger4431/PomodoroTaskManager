package com.pomodoro.model.enums;

/**
 * 任务类型枚举，区分按次数打卡的任务与按日期重复的任务
 */
public enum TaskType {

    /** 按次数重复的任务 */
    REPEAT_COUNT(0),

    /** 按日期重复的任务 */
    REPEAT_DATE(1);

    /** 任务类型对应的编码，与数据库中存储的 int 值一致 */
    private final int code;

    /**
     * 通过编码构造任务类型枚举值
     */
    TaskType(int code) {
        this.code = code;
    }

    /**
     * 获取任务类型编码
     */
    public int getCode() {
        return code;
    }

    /**
     * 根据编码查找对应的任务类型，找不到时返回 REPEAT_COUNT
     */
    public static TaskType fromCode(int code) {
        for (TaskType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return REPEAT_COUNT;
    }
}
