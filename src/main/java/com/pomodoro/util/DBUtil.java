package com.pomodoro.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 数据库连接工具类
 * 负责获取 SQL Server 数据库连接以及释放资源
 */
public class DBUtil {

    // 数据库连接 URL
    // databaseName 必须和你 SSMS 里建的库名完全一致
    private static final String URL =
            "jdbc:sqlserver://localhost:1433;"
                    + "databaseName=PomodoroDB;"
                    + "encrypt=false;trustServerCertificate=true";

    // 数据库用户名
    private static final String USER = "sa";

    // 数据库密码
    // TODO: 确保这里的密码和你 SQL Server 里 sa 账号的密码一致
    private static final String PASSWORD = "123456";

    /**
     * 获取数据库连接
     *
     * @return Connection 对象
     * @throws SQLException 连接失败时抛出异常
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * 释放数据库资源
     * 按照 ResultSet -> Statement -> Connection 的顺序关闭
     *
     * @param conn 数据库连接
     * @param stmt 执行 SQL 的 Statement
     * @param rs   查询结果集
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 主方法：用于单独测试数据库连接是否成功
     * 如果控制台输出“✅ 数据库连接成功！”，说明配置完全正确
     */
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