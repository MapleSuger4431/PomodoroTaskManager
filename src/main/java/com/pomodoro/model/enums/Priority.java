package com.pomodoro.model.enums;

/**
 * 优先级枚举，用于表示任务的紧急重要程度
 */
public enum Priority {

    /** 低优先级 */
    LOW(0, "低"),

    /** 中优先级 */
    MEDIUM(1, "中"),

    /** 高优先级 */
    HIGH(2, "高");

    /** 优先级对应的编码，与数据库中存储的 int 值一致 */
    private final int code;

    /** 优先级的中文显示名称 */
    private final String label;

    /**
     * 通过编码和显示名称构造优先级枚举值
     */
    Priority(int code, String label) {
        this.code = code;
        this.label = label;
    }

    /**
     * 获取优先级编码
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取优先级的中文显示名称
     */
    public String getLabel() {
        return label;
    }

    /**
     * 根据编码查找对应的优先级，找不到时返回 MEDIUM
     */
    public static Priority fromCode(int code) {
        for (Priority priority : values()) {
            if (priority.code == code) {
                return priority;
            }
        }
        return MEDIUM;
    }
}
