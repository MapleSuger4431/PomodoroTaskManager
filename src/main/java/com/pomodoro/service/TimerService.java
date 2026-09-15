package com.pomodoro.service;

import com.pomodoro.dao.PomodoroRecordDao;
import com.pomodoro.dao.TaskDao;
import com.pomodoro.dao.TaskDailyRecordDao;
import com.pomodoro.dao.impl.PomodoroRecordDaoImpl;
import com.pomodoro.dao.impl.TaskDaoImpl;
import com.pomodoro.dao.impl.TaskDailyRecordDaoImpl;
import com.pomodoro.model.PomodoroRecord;
import com.pomodoro.model.Task;
import com.pomodoro.model.TaskDailyRecord;
import com.pomodoro.model.enums.RecordStatus;
import com.pomodoro.model.enums.TaskType;
import com.pomodoro.model.enums.TimerMode;
import com.pomodoro.util.DateUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 番茄计时业务服务
 * 多线程实现：计时线程 + 输入线程 + 主循环调度
 */
public class TimerService {

    private PomodoroRecordDao recordDao = new PomodoroRecordDaoImpl();
    private TaskDao taskDao = new TaskDaoImpl();
    private TaskDailyRecordDao dailyDao = new TaskDailyRecordDaoImpl();

    private int lastDuration;
    private RecordStatus lastStatus;

    /**
     * 启动一次番茄计时会话
     */
    public void startSession(Task task) {
        System.out.println("========== 番茄计时开始 ==========");
        System.out.println("任务：" + task.getTitle());
        System.out.println("模式：" + (task.getTimerMode() == TimerMode.COUNTDOWN ? "倒计时" : "正计时"));
        System.out.println("时长：" + task.getPomodoroMinutes() + " 分钟");
        System.out.println("按回车可暂停计时");
        System.out.println("==================================");

        // 记录本次会话的真实开始时间，用于番茄记录的 start_time，避免用结束时间倒推造成偏差
        final Date sessionStart = new Date();
        final int totalSeconds = task.getPomodoroMinutes() * 60;
        final AtomicBoolean running = new AtomicBoolean(true);
        final AtomicBoolean paused = new AtomicBoolean(false);
        final AtomicInteger elapsedSeconds = new AtomicInteger(0);
        final BlockingQueue<String> commandQueue = new LinkedBlockingQueue<>();

        // 计时线程：每秒刷新，暂停时不累加
        Thread timerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running.get()) {
                    if (!paused.get()) {
                        int current = elapsedSeconds.get();
                        if (task.getTimerMode() == TimerMode.COUNTDOWN) {
                            int remaining = totalSeconds - current;
                            if (remaining <= 0) {
                                running.set(false);
                                return;
                            }
                            System.out.print("\r剩余时间: " + formatTime(remaining) + "    ");
                        } else {
                            System.out.print("\r已用时间: " + formatTime(current) + "    ");
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                        elapsedSeconds.incrementAndGet();
                    } else {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                }
            }
        });

        // 输入守护线程：非阻塞轮询标准输入，不再新建 Scanner。
        // 计时结束后线程随 running 标志自然退出，不会残留阻塞在输入流上抢走菜单输入
        Thread inputThread = new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                while (running.get()) {
                    try {
                        if (System.in.available() > 0) {
                            String line = reader.readLine();
                            if (line != null) {
                                commandQueue.offer(line.trim());
                            }
                        } else {
                            Thread.sleep(100);
                        }
                    } catch (Exception e) {
                        // 读取输入异常时结束输入线程，不影响计时主流程
                        return;
                    }
                }
            }
        });
        inputThread.setDaemon(true);

        timerThread.start();
        inputThread.start();

        // 主循环：轮询计时结束 + 用户命令
        while (running.get()) {
            // 倒计时自然结束
            if (task.getTimerMode() == TimerMode.COUNTDOWN
                    && elapsedSeconds.get() >= totalSeconds) {
                running.set(false);
                onFinish(task, totalSeconds, sessionStart);
                return;
            }

            String cmd = commandQueue.poll();
            if (cmd != null) {
                if (!paused.get()) {
                    // 运行中：任意输入触发暂停
                    paused.set(true);
                    System.out.println("\n已暂停，输入 p 继续，a 放弃，f 完成");
                } else {
                    // 暂停中：处理命令
                    if ("p".equalsIgnoreCase(cmd)) {
                        paused.set(false);
                        System.out.print("继续计时");
                    } else if ("a".equalsIgnoreCase(cmd)) {
                        running.set(false);
                        onAbandon(task, elapsedSeconds.get(), sessionStart);
                        return;
                    } else if ("f".equalsIgnoreCase(cmd)) {
                        running.set(false);
                        onFinish(task, elapsedSeconds.get(), sessionStart);
                        return;
                    } else {
                        System.out.println("无效命令，输入 p 继续，a 放弃，f 完成");
                    }
                }
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    /**
     * 计时完成回调
     */
    private void onFinish(Task task, int duration, Date sessionStart) {
        Date now = new Date();
        PomodoroRecord record = new PomodoroRecord();
        record.setUserId(task.getUserId());
        record.setTaskId(task.getId());
        record.setStartTime(sessionStart);
        record.setEndTime(now);
        record.setDuration(duration);
        record.setStatus(RecordStatus.FINISHED);
        recordDao.insert(record);

        // 更新任务总完成次数
        task.setCompletedCount(task.getCompletedCount() + 1);
        taskDao.updateCompletedCount(task.getId(), task.getCompletedCount());

        // 按日期重复任务：更新今日每日记录
        if (task.getType() == TaskType.REPEAT_DATE) {
            Date today = new Date();
            TaskDailyRecord dailyRecord = dailyDao.findByTaskAndDate(task.getId(), today);
            if (dailyRecord != null) {
                dailyDao.updateCompletedCount(dailyRecord.getId(), dailyRecord.getCompletedCount() + 1);
            }
        }

        lastDuration = duration;
        lastStatus = RecordStatus.FINISHED;

        System.out.println("\n========== 计时结束 ==========");
        System.out.println("任务: " + task.getTitle());
        System.out.println("本次专注: " + DateUtil.formatDuration(duration));
        System.out.println("已自动记为完成");
        System.out.println("================================");
    }

    /**
     * 放弃计时回调
     */
    private void onAbandon(Task task, int duration, Date sessionStart) {
        Date now = new Date();
        PomodoroRecord record = new PomodoroRecord();
        record.setUserId(task.getUserId());
        record.setTaskId(task.getId());
        record.setStartTime(sessionStart);
        record.setEndTime(now);
        record.setDuration(duration);
        record.setStatus(RecordStatus.ABANDONED);
        recordDao.insert(record);

        lastDuration = duration;
        lastStatus = RecordStatus.ABANDONED;

        System.out.println("\n已放弃，本次专注时长: " + DateUtil.formatDuration(duration));
    }

    public int getLastDuration() {
        return lastDuration;
    }

    public RecordStatus getLastStatus() {
        return lastStatus;
    }

    /**
     * 秒数格式化为 mm:ss
     */
    private String formatTime(int seconds) {
        int min = seconds / 60;
        int sec = seconds % 60;
        return String.format("%02d:%02d", min, sec);
    }
}
