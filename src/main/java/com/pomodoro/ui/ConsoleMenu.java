package com.pomodoro.ui;

import com.pomodoro.service.UserService;
import com.pomodoro.util.ConsoleUtil;

/**
 * 控制台主菜单类，负责登录菜单与主菜单的循环展示与分发
 */
public class ConsoleMenu {

    /** 用户业务逻辑对象 */
    private UserService userService = new UserService();

    /**
     * 启动控制台菜单主循环，未登录展示登录菜单，已登录展示主菜单
     */
    public void start() {
        while (true) {
            if (userService.isLoggedIn()) {
                showMainMenu();
            } else {
                showLoginMenu();
            }
        }
    }

    /**
     * 展示登录与注册菜单并处理用户选择
     */
    private void showLoginMenu() {
        System.out.println();
        System.out.println("========== 番茄任务管理系统 ==========");
        System.out.println("1. 注册");
        System.out.println("2. 登录");
        System.out.println("0. 退出");
        int choice = ConsoleUtil.readInt("请选择：");
        switch (choice) {
            case 1:
                handleRegister();
                break;
            case 2:
                handleLogin();
                break;
            case 0:
                System.out.println("感谢使用，再见！");
                System.exit(0);
                break;
            default:
                System.out.println("无效的选择，请重新输入。");
                break;
        }
    }

    /**
     * 展示主菜单并按选择路由到各功能子菜单
     */
    private void showMainMenu() {
        String userId = userService.getCurrentUser().getId();
        System.out.println();
        System.out.println("========== 欢迎，" + userService.getCurrentUser().getUsername() + " ==========");
        System.out.println("1. 任务管理");
        System.out.println("2. 番茄计时");
        System.out.println("3. 记录查询");
        System.out.println("4. 统计");
        System.out.println("5. 备份/恢复");
        System.out.println("0. 退出登录");
        int choice = ConsoleUtil.readInt("请选择：");
        switch (choice) {
            case 1:
                new TaskMenu().show(userId);
                break;
            case 2:
                new TimerMenu().show(userId);
                break;
            case 3:
                new RecordMenu().show(userId);
                break;
            case 4:
                new StatsMenu().show(userId);
                break;
            case 5:
                new BackupMenu().show(userId);
                break;
            case 0:
                userService.logout();
                // 先清屏，再在干净的屏幕上打印提示与新的登录菜单
                clearScreen();
                System.out.println("已退出登录。");
                break;
            default:
                System.out.println("无效的选择，请重新输入。");
                break;
        }
    }

    /**
     * 处理用户注册，读取用户名、密码与确认密码并调用注册服务
     */
    private void handleRegister() {
        String username = ConsoleUtil.readLine("请输入用户名：");
        String password = ConsoleUtil.readLine("请输入密码：");
        String confirmPassword = ConsoleUtil.readLine("请再次输入密码：");
        // 两次输入的密码必须一致
        if (!password.equals(confirmPassword)) {
            System.out.println("两次密码不一致");
            return;
        }
        try {
            userService.register(username, password);
            System.out.println("注册成功");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * 处理用户登录，读取用户名与密码并调用登录服务
     */
    private void handleLogin() {
        String username = ConsoleUtil.readLine("请输入用户名：");
        String password = ConsoleUtil.readLine("请输入密码：");
        try {
            userService.login(username, password);
            System.out.println("登录成功");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * 清空控制台屏幕，便于在干净的屏幕上展示新的菜单
     */
    private void clearScreen() {
        // 输出空行把旧内容顶出可见区域：与 TaskMenu、BackupMenu 的做法一致，
        // 不使用 ANSI 转义序列与系统清屏命令，保证 IDEA 控制台、cmd、PowerShell 下都不产生乱码
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
}
