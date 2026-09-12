package com.pomodoro.ui;

import com.pomodoro.service.BackupService;
import com.pomodoro.util.ConsoleUtil;

/**
 * 备份恢复菜单类，负责数据备份与恢复的交互
 */
public class BackupMenu {

    /** 备份业务逻辑对象 */
    private BackupService backupService = new BackupService();

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
}
