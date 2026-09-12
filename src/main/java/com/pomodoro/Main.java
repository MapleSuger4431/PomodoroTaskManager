package com.pomodoro;

import com.pomodoro.util.DBUtil;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        System.out.println("正在尝试连接数据库...");
        try (Connection conn = DBUtil.getConnection()) {
            if (conn != null) {
                System.out.println("✅ 数据库连接成功！");
                System.out.println("数据库产品名称：" + conn.getMetaData().getDatabaseProductName());
                System.out.println("数据库版本：" + conn.getMetaData().getDatabaseProductVersion());
            }
        } catch (Exception e) {
            System.out.println("❌ 数据库连接失败！");
            e.printStackTrace();
        }
    }
}