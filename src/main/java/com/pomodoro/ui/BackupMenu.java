package com.pomodoro.ui;

import com.pomodoro.service.BackupService;
import com.pomodoro.util.ConsoleUtil;

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
                handleBackup(userId);
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
     */
    private void handleBackup(String userId) {
        // 默认备份文件路径：项目运行目录下的 backup_<userId>.ser
        String defaultPath = "backup_" + userId + ".ser";
        String path = ConsoleUtil.readLine("请输入备份文件路径（直接回车使用默认 " + defaultPath + "）：");
        if (path.isEmpty()) {
            path = defaultPath;
        }
        backupService.backup(userId, path);
        ConsoleUtil.pressEnterToContinue();
    }

    /**
     * 处理数据恢复：读取备份文件路径，二次确认后执行恢复
     */
    private void handleRestore() {
        String path = ConsoleUtil.readLine("请输入备份文件路径：");
        if (path.isEmpty()) {
            System.out.println("文件路径不能为空。");
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
