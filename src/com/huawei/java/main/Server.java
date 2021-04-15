package com.huawei.java.main;

// N(1≤N≤100)，表示可以采购的服务器类型数量
public class Server {
    // 型号,唯一标识，长度不超过 20，仅由数字和大小写英文字符构成
    public String name;
    // CPU核数
    public long cpu;
    // 内存大小
    public long memory;
    // 硬件成本
    public long oricost;
    // 每日能耗成本
    public long dailycost;

    public Server(String[] str) {
        this.name = str[0];
        this.cpu = Long.parseLong(str[1]);
        this.memory = Long.parseLong(str[2]);
        this.oricost = Long.parseLong(str[3]);
        this.dailycost = Long.parseLong(str[4]);
    }

    @Override
    public String toString() {
        return "Server{" +
                "name='" + name + '\'' +
                ", cpu=" + cpu +
                ", memory=" + memory +
                ", oricost=" + oricost +
                ", dailycost=" + dailycost +
                '}';
    }
}
