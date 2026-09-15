package com.pomodoro.ui;

import com.pomodoro.service.BackupService;
import com.pomodoro.util.ConsoleUtil;

import java.io.File;

/**
 * 备份恢复菜单类，负责数据备份与恢复的交互
 */
public class BackupMenu {

    /** 备份业务逻辑对象 */
    private BackupService backupService = new BackupService();

    /** 清屏时输出的空行数量，用于把历史输出滚出可见区域 */
    private static final int CLEAR_BLANK_LINES = 50;

    /**
     * 展示备份恢复菜单
     */
    public void show(String userId) {
        while (true) {
            System.out.println();
            System.out.println("========== 备份恢复 ==========");
            System.out.println("1. 备份数据");
            System.out.println("2. 恢复数据");
            System.out.println("0. 返回");
            System.out.println("==============================");
            int choice = ConsoleUtil.readInt("请选择：");
            if (choice == 1) {
                handleBackup();
            } else if (choice == 2) {
                handleRestore();
            } else if (choice == 0) {
                // 返回前清屏，把本次菜单与操作的全部历史输出清除
                clearScreen();
                return;
            } else {
                System.out.println("无效选项，请重新输入。");
            }
        }
    }

    /**
     * 处理数据备份：读取备份文件路径（回车使用默认路径）并执行备份
     * 备份内容为全部用户数据，任何用户登录后都可以使用同一份备份文件
     */
    private void handleBackup() {
        // 默认备份文件路径：项目运行目录下的 backup_all.ser，所有用户共用
        String defaultPath = "backup_all.ser";
        String path = ConsoleUtil.readLine("请输入备份文件路径（直接回车使用默认 " + defaultPath + "，支持任意自定义路径）：");
        if (path.isEmpty()) {
            path = defaultPath;
        }
        backupService.backup(path);
        ConsoleUtil.pressEnterToContinue();
    }

    /**
     * 处理数据恢复：读取恢复文件路径（回车使用默认路径），二次确认后执行恢复
     * 恢复的是全库数据，任何用户登录后都可以执行恢复
     */
    private void handleRestore() {
        // 默认恢复文件路径：与备份默认路径一致，即项目运行目录下的 backup_all.ser
        String defaultPath = "backup_all.ser";
        String path = ConsoleUtil.readLine("请输入备份文件路径（直接回车使用默认 " + defaultPath + "，支持任意自定义路径）：");
        if (path.isEmpty()) {
            path = defaultPath;
        }
        // 恢复前校验备份文件是否存在，避免确认后才失败
        if (!new File(path).exists()) {
            System.out.println("备份文件不存在：" + new File(path).getAbsolutePath());
            System.out.println("请检查路径后重试。");
            ConsoleUtil.pressEnterToContinue();
            return;
        }
        // 恢复会清空并覆盖当前全部数据，必须二次确认
        boolean confirmed = ConsoleUtil.readYesNo("警告：恢复会覆盖当前所有数据，确认？(y/n)");
        if (!confirmed) {
            System.out.println("已取消恢复操作。");
            return;
        }
        backupService.restore(path);
        ConsoleUtil.pressEnterToContinue();
    }

    /**
     * 清除控制台历史输出：输出若干空行，把之前的菜单与操作记录滚出可见区域
     * 说明：不调用 cmd /c cls，也不依赖 ANSI 转义序列，
     * 保证在 IDEA 控制台、cmd、PowerShell 下都能正常清屏，不产生乱码
     */
    private void clearScreen() {
        for (int i = 0; i < CLEAR_BLANK_LINES; i++) {
            System.out.println();
        }
        // 立即刷新输出缓冲区，保证清屏效果在返回上一级菜单前生效
        System.out.flush();
    }
}
