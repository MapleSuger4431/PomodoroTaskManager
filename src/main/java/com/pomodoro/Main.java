package com.pomodoro;

import com.pomodoro.ui.ConsoleMenu;
import com.pomodoro.util.DBUtil;
import java.sql.Connection;

/**
 * 程序入口
 * 启动前先检测数据库连接，连接成功才进入主菜单
 */
public class Main {
    public static void main(String[] args) {
        // 1. 启动前检测数据库连接
        System.out.println("正在初始化数据库连接...");
        try (Connection conn = DBUtil.getConnection()) {
            System.out.println("✅ 数据库连接成功！");
        } catch (Exception e) {
            System.out.println("❌ 数据库连接失败！请检查 DBUtil.java 的密码和 SQL Server 配置。");
            e.printStackTrace();
            return;
        }

        // 2. 启动控制台菜单
        new ConsoleMenu().start();
    }
}
