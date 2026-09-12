package com.pomodoro.util;

import java.util.Locale;
import java.util.Scanner;

/**
 * 控制台工具类，封装控制台输入读取与格式化输出
 */
public class ConsoleUtil {

    /** 全局唯一的控制台扫描器 */
    private static final Scanner SCANNER = new Scanner(System.in);

    /** 控制台分隔线 */
    private static final String DIVIDER = "----------------------------------------";

    /** 私有构造方法，防止工具类被实例化 */
    private ConsoleUtil() {
    }

    /**
     * 读取一行文本输入并去除首尾空白
     */
    public static String readLine(String prompt) {
        System.out.print(prompt);
        return SCANNER.nextLine().trim();
    }

    /**
     * 循环读取直到用户输入合法的整数
     */
    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = SCANNER.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("输入无效，请输入一个整数。");
            }
        }
    }

    /**
     * 读取整数，输入为空或不合法时返回默认值
     */
    public static int readIntOrDefault(String prompt, int def) {
        System.out.print(prompt);
        String line = SCANNER.nextLine().trim();
        if (line.isEmpty()) {
            return def;
        }
        try {
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    /**
     * 循环读取直到用户输入 y/n 形式的确认结果
     */
    public static boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = SCANNER.nextLine().trim().toLowerCase(Locale.ROOT);
            if ("y".equals(line) || "yes".equals(line)) {
                return true;
            }
            if ("n".equals(line) || "no".equals(line)) {
                return false;
            }
            System.out.println("输入无效，请输入 y 或 n。");
        }
    }

    /**
     * 打印一条分隔线
     */
    public static void printDivider() {
        System.out.println(DIVIDER);
    }

    /**
     * 打印带装饰的标题行
     */
    public static void printTitle(String title) {
        System.out.println();
        System.out.println("========== " + title + " ==========");
    }

    /**
     * 提示用户按回车键继续并等待输入
     */
    public static void pressEnterToContinue() {
        System.out.print("按回车键继续...");
        SCANNER.nextLine();
    }

    /**
     * 当前线程休眠指定毫秒数
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
