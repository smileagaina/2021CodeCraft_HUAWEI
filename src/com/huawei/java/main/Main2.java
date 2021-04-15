package com.huawei.java.main;

/**
 * 程序入口
 */
public class Main2 {

    public static void main(String[] args) {
         long time1 = System.currentTimeMillis();
        Work_a work = new Work_a();
        // 本地的读取
         String where = "2";
         work.read_new(where + ".txt");
        // 本地的处理
         work.process_new();
         long time2 = System.currentTimeMillis();
        // 费用输出
         work.write2();
         System.out.println("运行的总时间 " + (time2 - time1));
        work.test_num();
        // -------------------------

        // 提交的读取
//       work.read_new2_firstKdays();
        // 提交的处理
//       work.process_new2();

    }
}

