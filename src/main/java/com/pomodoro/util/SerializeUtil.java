package com.pomodoro.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 序列化工具类，提供对象与文件之间的序列化读写功能
 */
public class SerializeUtil {

    /** 私有构造方法，防止工具类被实例化 */
    private SerializeUtil() {
    }

    /**
     * 将对象序列化写入指定文件
     */
    public static void writeObject(Object obj, String filePath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(obj);
        }
    }

    /**
     * 从指定文件反序列化读取对象
     */
    public static Object readObject(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return ois.readObject();
        }
    }
}
