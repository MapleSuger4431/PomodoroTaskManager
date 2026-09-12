package com.pomodoro.model.enums;

/**
 * 番茄钟记录状态枚举，表示一次番茄钟计时的最终结果
 */
public enum RecordStatus {

    /** 正常完成 */
    FINISHED(0),

    /** 中途放弃 */
    ABANDONED(1);

    /** 记录状态对应的编码，与数据库中存储的 int 值一致 */
    private final int code;

    /**
     * 通过编码构造记录状态枚举值
     */
    RecordStatus(int code) {
        this.code = code;
    }

    /**
     * 获取记录状态编码
     */
    public int getCode() {
        return code;
    }

    /**
     * 根据编码查找对应的记录状态，找不到时返回 FINISHED
     */
    public static RecordStatus fromCode(int code) {
        for (RecordStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return FINISHED;
    }
}
