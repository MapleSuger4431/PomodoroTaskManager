package com.pomodoro.service;

import com.pomodoro.dao.PomodoroRecordDao;
import com.pomodoro.dao.TaskDao;
import com.pomodoro.model.Task;
import com.pomodoro.model.enums.RecordStatus;

/**
 * 计时业务逻辑类，负责一次番茄钟计时的完整流程（骨架，具体实现待补充）
 */
public class TimerService {

    /** 番茄钟记录数据访问对象 */
    private PomodoroRecordDao recordDao;

    /** 任务数据访问对象 */
    private TaskDao taskDao;

    /**
     * 启动一次番茄钟计时
     */
    public void startSession(Task task) {
        // TODO 执行倒计时/正计时流程，结束后保存番茄钟记录并更新任务与每日记录
        throw new UnsupportedOperationException("待实现");
    }

    /**
     * 获取最近一次计时的有效时长
     */
    public int getLastDuration() {
        // TODO 返回最近一次计时的有效时长（秒）
        throw new UnsupportedOperationException("待实现");
    }

    /**
     * 获取最近一次计时的结束状态
     */
    public RecordStatus getLastStatus() {
        // TODO 返回最近一次计时的结束状态（完成或放弃）
        throw new UnsupportedOperationException("待实现");
    }
}
