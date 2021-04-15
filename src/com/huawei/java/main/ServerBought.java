package com.huawei.java.main;

import java.util.ArrayList;

// N(1≤N≤100)，表示可以采购的服务器类型数量
public class ServerBought {
    public String name;
    //服务器编号
    public int no;
    // CPU核数
    public long cpu;
    // 内存
    public long memory;
    //左节点CPU核数,大小不超过 1024
    public long cpusizel;
    //左节点内存大小,大小不超过 1024
    public long memsizel;
    //右节点CPU核数,大小不超过 1024
    public long cpusizer;
    //右节点内存大小,大小不超过 1024
    public long memsizer;
    //硬件成本,不超过 5×10^5
    public long oricost;
    //每日能耗成本,不超过 5000
    public long dailycost;
    // 服务器上负载的虚拟机列表
    public ArrayList<VirtualUsed> virtualList;
    //是否可以迁移,0表示可以,1表示不可以
    public int can;

    public ServerBought(int no, Server server) {
        this.name = server.name;
        this.no = no;
        this.cpu = server.cpu;
        this.memory = server.memory;
        // 除以2，向下转化
        this.cpusizel = server.cpu/2;
        this.memsizel = server.memory/2;
        this.cpusizer = server.cpu/2;
        this.memsizer = server.memory/2;
        this.oricost = server.oricost;
        this.dailycost = server.dailycost;
        virtualList = new ArrayList<>();
        this.can = 0;
    }

    // 从一个serverBought构造另一个serverBought
    public ServerBought(ServerBought serverBought) {
        this.name = serverBought.name;
        this.no = serverBought.no;
        this.cpu = serverBought.cpu;
        this.memory = serverBought.memory;
        // 除以2，向下转化
        this.cpusizel = serverBought.cpusizel;
        this.memsizel = serverBought.memsizel;
        this.cpusizer = serverBought.cpusizer;
        this.memsizer = serverBought.memsizer;
        this.oricost = serverBought.oricost;
        this.dailycost = serverBought.dailycost;

        this.virtualList = new ArrayList<>(serverBought.virtualList);
        this.can = 0;

    }

    /**
     * 无编号
     * @param infos
     */
    public ServerBought(String[] infos) {
        this.name = infos[0];
        this.cpu = Integer.parseInt(infos[1]);
        this.memory = Integer.parseInt(infos[2]);
        this.cpusizel = Integer.parseInt(infos[1])/2;
        this.memsizel = Integer.parseInt(infos[2])/2;
        this.cpusizer = Integer.parseInt(infos[1])/2;
        this.memsizer = Integer.parseInt(infos[2])/2;
        this.oricost = Integer.parseInt(infos[3]);
        this.dailycost = Integer.parseInt(infos[4]);
    }

    public ServerBought(int no, String[] infos) {
        this.name = infos[0];
        this.no = no;
        this.cpu = Integer.parseInt(infos[1]);
        this.memory = Integer.parseInt(infos[2]);
        this.cpusizel = Integer.parseInt(infos[1])/2;
        this.memsizel = Integer.parseInt(infos[2])/2;
        this.cpusizer = Integer.parseInt(infos[1])/2;
        this.memsizer = Integer.parseInt(infos[2])/2;
        this.oricost = Integer.parseInt(infos[3]);
        this.dailycost = Integer.parseInt(infos[4]);
    }

    @Override
    public String toString() {
        return "ServerBought{" +
                "name='" + name + '\'' +
                ", no=" + no +
                ", cpu=" + cpu +
                ", memory=" + memory +
                ", cpusizel=" + cpusizel +
                ", memsizel=" + memsizel +
                ", cpusizer=" + cpusizer +
                ", memsizer=" + memsizer +
                ", oricost=" + oricost +
                ", dailycost=" + dailycost +
                ", virtualList=" + virtualList +
                '}';
    }
}
