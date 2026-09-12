package com.pomodoro.model.enums;

/**
 * 任务状态枚举，表示任务当前所处的状态
 */
public enum TaskStatus {

    /** 待完成 */
    TODO(0),

    /** 已完成 */
    DONE(1),

    /** 已逾期 */
    OVERDUE(2);

    /** 任务状态对应的编码，与数据库中存储的 int 值一致 */
    private final int code;

    /**
     * 通过编码构造任务状态枚举值
     */
    TaskStatus(int code) {
        this.code = code;
    }

    /**
     * 获取任务状态编码
     */
    public int getCode() {
        return code;
    }

    /**
     * 根据编码查找对应的任务状态，找不到时返回 TODO
     */
    public static TaskStatus fromCode(int code) {
        for (TaskStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return TODO;
    }
}
