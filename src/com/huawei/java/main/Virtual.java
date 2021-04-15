package com.huawei.java.main;

//一个整数 M(1≤M≤1000)，表示售卖的虚拟机类型数量
public class Virtual {
    // 型号,唯一标识，虚拟机型号长度不超过 20，仅由数字，大小写英文字符和'.'构成
    public String name;
    // CPU核数,大小不超过 1024
    public long cpu;
    // 内存大小,大小不超过 1024
    public long memory;
    //是否双节点部署,0表示单节点，1表示双节点
    public int isBi;

    //在哪个节点上部署,0表示左节点，1表示右节点，2表示双节点
    public int node;

    // 构造函数
    public Virtual(String[] str) {
        this.name = str[0];
        this.cpu = Long.parseLong(str[1]);
        this.memory = Long.parseLong(str[2]);
        this.isBi = Integer.parseInt(str[3]);
    }

    @Override
    public String toString() {
        return "Virtual{" +
                "name='" + name + '\'' +
                ", cpu=" + cpu +
                ", memory=" + memory +
                ", isBi=" + isBi +
                ", node=" + node +
                '}';
    }
}
