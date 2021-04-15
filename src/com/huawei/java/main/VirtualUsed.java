package com.huawei.java.main;

//一个整数 M(1≤M≤1000)，表示售卖的虚拟机类型数量
public class VirtualUsed {
    //型号,唯一标识，虚拟机型号长度不超过 20，仅由数字，大小写英文字符和'.'构成
    public String name;
    //虚拟机编号
    public String no;
    //所在服务器编号
    public ServerBought serverBought;
    //CPU核数,大小不超过 1024
    public long cpusize;
    //内存大小,大小不超过 1024
    public long memsize;
    //是否双节点部署,0表示单节点，1表示双节点
    public int isBi;
    //在哪个节点上部署,0表示左节点，1表示右节点，2表示双节点
    public int node;

    /**
     * 无编号构造器
     * @param strs
     */

    public VirtualUsed(String[] strs) {
        this.name = strs[0];
        this.cpusize = Integer.parseInt(strs[1]);
        this.memsize = Integer.parseInt(strs[2]);
        this.isBi = Integer.parseInt(strs[3]);
    }

    public VirtualUsed(String no, String[] strs) {
        this.no = no;
        this.name = strs[0];
        this.cpusize = Integer.parseInt(strs[1]);
        this.memsize = Integer.parseInt(strs[2]);
        this.isBi = Integer.parseInt(strs[3]);
    }

    public VirtualUsed(String no, Virtual virtual) {
        this.no = no;
        this.name = virtual.name;
        this.cpusize = virtual.cpu;
        this.memsize = virtual.memory;
        this.isBi = virtual.isBi;
    }


}
