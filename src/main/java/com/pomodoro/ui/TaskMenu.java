package com.pomodoro.ui;

import com.pomodoro.model.Task;
import com.pomodoro.model.TaskDailyRecord;
import com.pomodoro.model.enums.Priority;
import com.pomodoro.model.enums.TaskStatus;
import com.pomodoro.model.enums.TaskType;
import com.pomodoro.model.enums.TimerMode;
import com.pomodoro.service.TaskService;
import com.pomodoro.util.ConsoleUtil;
import com.pomodoro.util.DateUtil;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 任务管理菜单类，负责任务的增删改查控制台交互
 */
public class TaskMenu {

    /** 任务业务逻辑对象 */
    private TaskService taskService = new TaskService();

    /** 表格各列宽度之和（ID 6 + 标题 18 + 类型 8 + 当日进度 12 + 总进度 20 + 截止时间 22 + 状态 8） */
    private static final int TABLE_WIDTH = 6 + 18 + 8 + 12 + 20 + 22 +8;

    /**
     * 展示任务管理菜单并循环处理用户选择
     */
    public void show(String userId) {
        // 进入菜单先刷新每日记录，保证按日期重复任务的当天记录与逾期状态是最新的
        taskService.refreshDailyRecords(userId);
        while (true) {
            ConsoleUtil.printTitle("任务管理");
            System.out.println("1. 查看任务列表");
            System.out.println("2. 新增任务");
            System.out.println("3. 修改任务");
            System.out.println("4. 删除任务");
            System.out.println("5. 标记完成");
            System.out.println("0. 返回");
            int choice = ConsoleUtil.readInt("请选择：");
            switch (choice) {
                case 1:
                    handleList(userId);
                    pressEnter();
                    break;
                case 2:
                    handleCreate(userId);
                    pressEnter();
                    break;
                case 3:
                    handleUpdate(userId);
                    pressEnter();
                    break;
                case 4:
                    handleDelete(userId);
                    pressEnter();
                    break;
                case 5:
                    handleMarkDone(userId);
                    pressEnter();
                    break;
                case 0:
                    clearConsole();
                    return;
                default:
                    System.out.println("无效的选择，请重新输入。");
                    break;
            }
        }
    }

    /**
     * 展示当前用户的任务列表
     */
    private void handleList(String userId) {
        List<Task> tasks = taskService.listTasks(userId);
        printTaskTable(userId, tasks);
    }

    /**
     * 处理创建任务：按流程读取任务信息并调用任务服务创建
     */
    private void handleCreate(String userId) {
        ConsoleUtil.printTitle("新增任务");
        String title = readNonEmpty("请输入任务标题：");
        String description = readOptional("请输入任务描述（直接回车跳过）：");
        String tags = readOptional("请输入标签，多个标签用逗号分隔（直接回车跳过）：");
        System.out.println("优先级：1. 低  2. 中  3. 高");
        int priorityChoice = readChoice("请选择优先级：", 3);

        Task task = new Task();
        task.setUserId(userId);
        task.setTitle(title);
        task.setDescription(description);
        task.setTags(tags);
        task.setPriority(toPriority(priorityChoice));

        System.out.println("任务类型：1. 按次数  2. 按日期");
        int typeChoice = readChoice("请选择任务类型：", 2);
        if (typeChoice == 1) {
            // 按次数任务：设置目标次数与可选的截止时间
            task.setType(TaskType.REPEAT_COUNT);
            task.setTargetCount(readPositiveInt("请输入目标完成次数："));
            if (ConsoleUtil.readYesNo("是否设置截止时间？(y/n)：")) {
                task.setDueDate(readDateTime("请输入截止时间 (yyyy-MM-dd HH:mm)："));
            }
        } else {
            // 按日期任务：设置开始日期、（连续天数或无限重复）、每日目标次数与每日截止时间
            task.setType(TaskType.REPEAT_DATE);
            task.setStartDate(readDate("请输入开始日期 (yyyy-MM-dd，直接回车默认今天)："));
            if (ConsoleUtil.readYesNo("是否无限重复？(y/n)：")) {
                // 无限重复任务用 repeatDays = -1 标记：每日记录从开始日期一直生成到今天，
                // 总进度不显示"已完成/连续"，只显示已完成天数
                task.setRepeatDays(-1);
            } else {
                task.setRepeatDays(readPositiveInt("请输入连续天数："));
            }
            task.setTargetCount(readPositiveInt("请输入每日目标次数："));
            task.setDailyDeadline(readDailyDeadline());
        }

        System.out.println("计时模式：1. 倒计时  2. 正计时");
        int modeChoice = readChoice("请选择计时模式：", 2);
        task.setTimerMode(modeChoice == 1 ? TimerMode.COUNTDOWN : TimerMode.COUNT_UP);
        if (modeChoice == 1) {
            task.setPomodoroMinutes(readPositiveIntOrDefault("请输入倒计时时长（分钟，直接回车默认 25）：", 25));
        } else {
            // 正计时模式不使用倒计时时长，保留默认值
            task.setPomodoroMinutes(25);
        }

        taskService.createTask(task);
        System.out.println("任务创建成功！");
    }

    /**
     * 处理修改任务：可修改新增任务时填写的全部信息（标题、描述、标签、优先级、任务类型、
     * 类型相关字段、计时模式与时长），直接回车表示保持原值，输入 0 表示清除可选字段
     */
    private void handleUpdate(String userId) {
        List<Task> tasks = taskService.listTasks(userId);
        if (tasks.isEmpty()) {
            System.out.println("暂无任务，无法修改。");
            return;
        }
        printTaskTable(userId, tasks);
        // 支持按 ID 或标题两种方式定位任务
        Task task = selectTask(userId, "请输入要修改的任务 ID 或标题（直接回车返回）：");
        if (task == null) {
            return;
        }
        updateTaskFields(task);
        taskService.updateTask(task);
        System.out.println("修改成功！");
    }

    /**
     * 逐个提示修改新增任务时的全部字段：直接回车表示保持原值，输入内容则更新
     */
    private void updateTaskFields(Task task) {
        // 1. 基本信息：标题、描述、标签
        String title = ConsoleUtil.readLine("新标题（直接回车保持不变）：");
        if (!title.isEmpty()) {
            task.setTitle(title);
        }
        String description = ConsoleUtil.readLine("新描述（直接回车保持不变）：");
        if (!description.isEmpty()) {
            task.setDescription(description);
        }
        String tags = ConsoleUtil.readLine("新标签（直接回车保持不变）：");
        if (!tags.isEmpty()) {
            task.setTags(tags);
        }
        // 2. 优先级 1低 2中 3高
        updatePriority(task);
        // 3. 任务类型 1按次数 2按日期（允许修改，改为按日期后由业务层自动刷新每日记录）
        while (true) {
            String typeLine = ConsoleUtil.readLine("任务类型：1. 按次数  2. 按日期（直接回车保持不变）：");
            if (typeLine.isEmpty()) {
                break;
            }
            try {
                int typeChoice = Integer.parseInt(typeLine);
                if (typeChoice == 1) {
                    task.setType(TaskType.REPEAT_COUNT);
                    break;
                }
                if (typeChoice == 2) {
                    task.setType(TaskType.REPEAT_DATE);
                    break;
                }
            } catch (NumberFormatException e) {
                // 输入不是数字时继续循环重新读取
            }
            System.out.println("输入无效，请输入 1 或 2。");
        }
        // 4. 类型相关的字段
        if (task.getType() == TaskType.REPEAT_COUNT) {
            // 按次数任务：目标完成次数与截止时间
            int target = readPositiveIntOrKeep("目标完成次数（直接回车保持不变）：");
            if (target != -1) {
                task.setTargetCount(target);
            }
            while (true) {
                String dueLine = ConsoleUtil.readLine("截止时间 (yyyy-MM-dd HH:mm，直接回车保持不变，输入 0 清除)：");
                if (dueLine.isEmpty()) {
                    break;
                }
                if (dueLine.trim().equals("0")) {
                    task.setDueDate(null);
                    break;
                }
                try {
                    task.setDueDate(DateUtil.parse(dueLine, "yyyy-MM-dd HH:mm"));
                    break;
                } catch (ParseException e) {
                    System.out.println("时间格式无效，请按 yyyy-MM-dd HH:mm 输入。");
                }
            }
        } else {
            // 按日期任务：开始日期、无限/连续天数、每日目标次数、每日截止时间
            while (true) {
                String startLine = ConsoleUtil.readLine("开始日期 (yyyy-MM-dd，直接回车保持不变，输入 0 设为今天)：");
                if (startLine.isEmpty()) {
                    break;
                }
                if (startLine.trim().equals("0")) {
                    task.setStartDate(new Date());
                    break;
                }
                try {
                    task.setStartDate(DateUtil.parse(startLine, "yyyy-MM-dd"));
                    break;
                } catch (ParseException e) {
                    System.out.println("日期格式无效，请按 yyyy-MM-dd 输入。");
                }
            }
            while (true) {
                String infiniteLine = ConsoleUtil.readLine("无限重复（y 无限 / n 有限，直接回车保持不变）：");
                if (infiniteLine.isEmpty()) {
                    break;
                }
                String lower = infiniteLine.trim().toLowerCase(Locale.ROOT);
                if ("y".equals(lower) || "yes".equals(lower)) {
                    task.setRepeatDays(-1);
                    break;
                }
                if ("n".equals(lower) || "no".equals(lower)) {
                    task.setRepeatDays(readPositiveInt("请输入连续天数："));
                    break;
                }
                System.out.println("输入无效，请输入 y 或 n。");
            }
            int target = readPositiveIntOrKeep("每日目标次数（直接回车保持不变）：");
            if (target != -1) {
                task.setTargetCount(target);
            }
            String deadline = readDailyDeadlineOrKeep("每日截止时间 HH:mm（直接回车保持不变）：");
            if (deadline != null) {
                task.setDailyDeadline(deadline);
            }
        }
        // 5. 计时模式与倒计时时长
        while (true) {
            String modeLine = ConsoleUtil.readLine("计时模式：1. 倒计时  2. 正计时（直接回车保持不变）：");
            if (modeLine.isEmpty()) {
                break;
            }
            try {
                int modeChoice = Integer.parseInt(modeLine);
                if (modeChoice == 1) {
                    task.setTimerMode(TimerMode.COUNTDOWN);
                    break;
                }
                if (modeChoice == 2) {
                    task.setTimerMode(TimerMode.COUNT_UP);
                    break;
                }
            } catch (NumberFormatException e) {
                // 输入不是数字时继续循环重新读取
            }
            System.out.println("输入无效，请输入 1 或 2。");
        }
        if (task.getTimerMode() == TimerMode.COUNTDOWN) {
            int minutes = readPositiveIntOrKeep("倒计时时长（分钟，直接回车保持不变）：");
            if (minutes != -1) {
                task.setPomodoroMinutes(minutes);
            }
        }
    }

    /**
     * 读取新的优先级并写回任务，直接回车保持原值
     */
    private void updatePriority(Task task) {
        while (true) {
            String line = ConsoleUtil.readLine("新优先级：1. 低  2. 中  3. 高（直接回车保持不变）：");
            if (line.isEmpty()) {
                return;
            }
            try {
                int choice = Integer.parseInt(line);
                if (choice >= 1 && choice <= 3) {
                    task.setPriority(toPriority(choice));
                    return;
                }
            } catch (NumberFormatException e) {
                // 输入不是数字时继续循环重新读取
            }
            System.out.println("输入无效，请输入 1-3。");
        }
    }

    /**
     * 处理删除任务：输入任务 ID 或标题后逻辑删除
     */
    private void handleDelete(String userId) {
        // 支持按 ID 或标题两种方式定位任务
        Task task = selectTask(userId, "请输入要删除的任务 ID 或标题（直接回车返回）：");
        if (task == null) {
            return;
        }
        taskService.deleteTask(task.getId());
        System.out.println("删除成功！");
    }

    /**
     * 处理标记完成：输入任务 ID 或标题后将任务标记为已完成
     */
    private void handleMarkDone(String userId) {
        // 支持按 ID 或标题两种方式定位任务
        Task task = selectTask(userId, "请输入要标记完成的任务 ID 或标题（直接回车返回）：");
        if (task == null) {
            return;
        }
        if (task.getStatus() == TaskStatus.DONE) {
            System.out.println("该任务已是完成状态。");
            return;
        }
        taskService.markDone(task.getId());
        System.out.println("标记完成成功！");
    }

    /**
     * 按 ID 或标题选择任务：单个匹配直接返回，多个匹配时列出候选让用户按序号选择，
     * 未匹配或用户取消时返回 null
     */
    private Task selectTask(String userId, String prompt) {
        String input = ConsoleUtil.readLine(prompt);
        if (input.isEmpty()) {
            return null;
        }
        List<Task> matched = taskService.searchTasks(userId, input);
        if (matched.isEmpty()) {
            System.out.println("未找到该任务。");
            return null;
        }
        if (matched.size() == 1) {
            return matched.get(0);
        }
        // 输入命中多个任务（同标题或标题包含该关键词）时，列出候选由用户选择
        System.out.println("找到多个匹配的任务：");
        for (int i = 0; i < matched.size(); i++) {
            Task task = matched.get(i);
            System.out.println((i + 1) + ". " + pad(task.getId(), 6) + pad(task.getTitle(), 18)
                    + pad(task.getType() == TaskType.REPEAT_DATE ? "按日期" : "按次数", 8)
                    + statusLabel(task.getStatus()));
        }
        while (true) {
            int choice = ConsoleUtil.readInt("请输入序号选择（0 返回）：");
            if (choice == 0) {
                return null;
            }
            if (choice >= 1 && choice <= matched.size()) {
                return matched.get(choice - 1);
            }
            System.out.println("输入无效，请输入 0-" + matched.size() + "。");
        }
    }

    /**
     * 以表格形式打印任务列表：ID、标题、类型、当日进度、总进度、截止时间、状态
     */
    private void printTaskTable(String userId, List<Task> tasks) {
        if (tasks.isEmpty()) {
            System.out.println("暂无任务。");
            return;
        }
        System.out.println(pad("ID", 6) + pad("标题", 18) + pad("类型", 8)
                + pad("当日进度", 12) + pad("总进度", 20) + pad("截止时间", 22) + pad("状态", 8));

        // 通用分隔线只有 40 个字符，比任务表格窄，必须打印与表格等宽的分隔线才不显得错位

        printTableDivider();

        Date today = new Date();
        // 每日记录按需只加载一次，用于计算按日期任务的当日进度与已完成天数
        List<TaskDailyRecord> dailyRecords = null;
        for (Task task : tasks) {
            String todayProgress;
            String totalProgress;
            String deadlineText;
            if (task.getType() == TaskType.REPEAT_DATE) {
                // 按日期任务：当日进度显示 今日完成/每日目标，总进度显示 已完成天数/连续天数
                if (dailyRecords == null) {
                    dailyRecords = taskService.listDailyRecords(userId);
                }
                TaskDailyRecord todayRecord = taskService.getDailyRecord(task.getId(), today);
                int todayDone = todayRecord != null ? todayRecord.getCompletedCount() : 0;
                todayProgress = todayDone + "/" + task.getTargetCount();
                int completedDays = countCompletedDays(task, dailyRecords);
                if (task.getRepeatDays() == -1) {
                    // 无限重复任务没有总天数，只显示已完成天数
                    totalProgress = "已完成 " + completedDays + " 天";
                } else {
                    totalProgress = completedDays + "/" + task.getRepeatDays();
                }
                String deadline = task.getDailyDeadline() != null ? task.getDailyDeadline() : "23:59";
                deadlineText = "每日 " + deadline;
            } else {
                // 按次数任务：当日进度无意义显示 "-"，总进度显示 已完成次数/目标次数
                todayProgress = "-";
                totalProgress = task.getCompletedCount() + "/" + task.getTargetCount();
                deadlineText = task.getDueDate() != null ? DateUtil.formatDateTime(task.getDueDate()) : "无";
            }
            System.out.println(pad(task.getId(), 6) + pad(task.getTitle(), 18)
                    + pad(task.getType() == TaskType.REPEAT_DATE ? "按日期" : "按次数", 8)
                    + pad(todayProgress, 12) + pad(totalProgress, 20)
                    + pad(deadlineText, 22) + pad(statusLabel(task.getStatus()), 8));
        }
    }

    /**
     * 打印与表格等宽的分隔线，保证分隔线完整覆盖到最后一列
     */
    private static void printTableDivider() {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < TABLE_WIDTH; i++) {
            line.append('-');
        }
        System.out.println(line);
    }

    /**
     * 清空终端中已经输出的内容：先真正删除屏幕上的全部信息，
     * 再由上一级菜单（ConsoleMenu）打印主菜单，避免历史输出堆积显得臃肿。
     * 使用 ANSI 清屏转义序列：\033[H 把光标移回左上角，\033[2J 清除整个屏幕，
     * cmd / PowerShell / Windows Terminal / IDEA 运行控制台均支持
     */
    private static void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * 提示按回车键继续：先刷新输出缓冲区让提示立刻显示（避免提示迟迟不出现造成卡顿），
     * 再用控制台工具一次读取完整一行的回车，避免把回车输入遗留到下一个输入提示中
     */
    private static void pressEnter() {
        System.out.print("按回车键继续...");
        System.out.flush();
        ConsoleUtil.readLine("");
    }

    /**
     * 统计按日期任务已完成的天数：当日完成次数达到目标次数即算完成一天
     */
    private int countCompletedDays(Task task, List<TaskDailyRecord> records) {
        int days = 0;
        for (TaskDailyRecord record : records) {
            if (record.getTaskId().equals(task.getId())
                    && record.getCompletedCount() >= record.getTargetCount()) {
                days++;
            }
        }
        return days;
    }

    /**
     * 将任务状态转换为中文显示文本
     */
    private String statusLabel(TaskStatus status) {
        if (status == TaskStatus.DONE) {
            return "已完成";
        }
        if (status == TaskStatus.OVERDUE) {
            return "未完成";
        }
        return "进行中";
    }

    /**
     * 读取非空文本，输入为空时反复提示
     */
    private String readNonEmpty(String prompt) {
        while (true) {
            String line = ConsoleUtil.readLine(prompt);
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println("输入不能为空，请重新输入。");
        }
    }

    /**
     * 读取可选文本，直接回车时返回 null
     */
    private String readOptional(String prompt) {
        String line = ConsoleUtil.readLine(prompt);
        return line.isEmpty() ? null : line;
    }

    /**
     * 读取 1 到 max 之间的整数选项，超出范围时反复提示
     */
    private int readChoice(String prompt, int max) {
        while (true) {
            int value = ConsoleUtil.readInt(prompt);
            if (value >= 1 && value <= max) {
                return value;
            }
            System.out.println("输入无效，请输入 1-" + max + "。");
        }
    }

    /**
     * 读取大于 0 的整数，非法时反复提示
     */
    private int readPositiveInt(String prompt) {
        while (true) {
            int value = ConsoleUtil.readInt(prompt);
            if (value >= 1) {
                return value;
            }
            System.out.println("输入无效，请输入大于 0 的整数。");
        }
    }

    /**
     * 读取大于 0 的整数，直接回车时返回默认值
     */
    private int readPositiveIntOrDefault(String prompt, int defaultValue) {
        while (true) {
            String line = ConsoleUtil.readLine(prompt);
            if (line.isEmpty()) {
                return defaultValue;
            }
            try {
                int value = Integer.parseInt(line);
                if (value >= 1) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // 输入不是数字时继续循环重新读取
            }
            System.out.println("输入无效，请输入大于 0 的整数。");
        }
    }

    /**
     * 读取大于 0 的整数，直接回车时返回 -1 表示保持不变
     */
    private int readPositiveIntOrKeep(String prompt) {
        while (true) {
            String line = ConsoleUtil.readLine(prompt);
            if (line.isEmpty()) {
                return -1;
            }
            try {
                int value = Integer.parseInt(line);
                if (value >= 1) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // 输入不是数字时继续循环重新读取
            }
            System.out.println("输入无效，请输入大于 0 的整数，直接回车保持不变。");
        }
    }

    /**
     * 读取 yyyy-MM-dd 日期，直接回车时返回 null（由业务层默认为今天）
     */
    private Date readDate(String prompt) {
        while (true) {
            String line = ConsoleUtil.readLine(prompt);
            if (line.isEmpty()) {
                return null;
            }
            try {
                return DateUtil.parse(line, "yyyy-MM-dd");
            } catch (ParseException e) {
                System.out.println("日期格式无效，请按 yyyy-MM-dd 输入。");
            }
        }
    }

    /**
     * 读取 yyyy-MM-dd HH:mm 时间，直接回车时返回 null
     */
    private Date readDateTime(String prompt) {
        while (true) {
            String line = ConsoleUtil.readLine(prompt);
            if (line.isEmpty()) {
                return null;
            }
            try {
                return DateUtil.parse(line, "yyyy-MM-dd HH:mm");
            } catch (ParseException e) {
                System.out.println("时间格式无效，请按 yyyy-MM-dd HH:mm 输入。");
            }
        }
    }

    /**
     * 读取每日截止时间 HH:mm，直接回车时默认 23:59
     */
    private String readDailyDeadline() {
        while (true) {
            String line = ConsoleUtil.readLine("请输入每日截止时间 HH:mm（直接回车默认 23:59）：");
            if (line.isEmpty()) {
                return "23:59";
            }
            if (line.matches("([01]?\\d|2[0-3]):[0-5]\\d")) {
                return line;
            }
            System.out.println("时间格式无效，请按 HH:mm 输入，例如 22:30。");
        }
    }

    /**
     * 读取每日截止时间 HH:mm，直接回车时返回 null 表示保持不变
     */
    private String readDailyDeadlineOrKeep(String prompt) {
        while (true) {
            String line = ConsoleUtil.readLine(prompt);
            if (line.isEmpty()) {
                return null;
            }
            if (line.matches("([01]?\\d|2[0-3]):[0-5]\\d")) {
                return line;
            }
            System.out.println("时间格式无效，请按 HH:mm 输入，例如 22:30。");
        }
    }

    /**
     * 将 1/2/3 的菜单选择转换为优先级枚举
     */
    private Priority toPriority(int choice) {
        if (choice == 1) {
            return Priority.LOW;
        }
        if (choice == 2) {
            return Priority.MEDIUM;
        }
        return Priority.HIGH;
    }

    /**
     * 按显示宽度右侧补空格用于表格对齐，中文字符按 2 个宽度计算，超宽时截断
     */
    private static String pad(String text, int width) {
        if (text == null) {
            text = "";
        }
        StringBuilder sb = new StringBuilder();
        int used = 0;
        for (int i = 0; i < text.length() && used < width; i++) {
            char c = text.charAt(i);
            int charWidth = c > 255 ? 2 : 1;
            if (used + charWidth > width) {
                break;
            }
            sb.append(c);
            used += charWidth;
        }
        while (used < width) {
            sb.append(' ');
            used++;
        }
        return sb.toString();
    }
}
