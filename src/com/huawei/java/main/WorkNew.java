package com.huawei.java.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

public class WorkNew {
    // 服务器信息映射
    private static Map<String, Server> ServerMaps = new HashMap<>();
    // 虚拟机信息映射
    private static Map<String, Virtual> VirtualMaps = new HashMap<>();
    //    static Comparator cmp = new Comparator<Server>() {
//        @Override
//        public int compare(Server o1, Server o2) {
//            return (int)(1000*(o2.cpu*0.75 + o2.memory*0.23 + o2.oricost*0.02) - 1000*(o1.cpu*0.75 + o1.memory*0.23 + o1.oricost*0.02));
//        }
//
//    };
//    static Comparator<Server> cmp = Comparator.comparingDouble(s -> s.oricost*1.0/(s.cpu + s.memory));

    static Comparator<Server> cmp = Comparator.comparingDouble(s -> s.oricost);
//    static Comparator<Server> cmp2 = Comparator.comparingDouble(s -> 1.0/s.oricost);
    private static List<Server> serverList = new ArrayList<>(); // 优先队列，服务器的价格进行一个排序

    private static PriorityQueue<Server> serverList1 = new PriorityQueue<>(cmp); // 优先队列，服务器的价格进行一个排序
    private static PriorityQueue<Server> serverList2 = new PriorityQueue<>(cmp); // 优先队列，服务器的价格进行一个排序
    private static PriorityQueue<Server> serverList3 = new PriorityQueue<>(cmp); // 优先队列，服务器的价格进行一个排序

    private static PriorityQueue<Virtual> virtualListCpuNeed = new PriorityQueue<>(Comparator.comparingLong(v -> v.cpu)); //优先队列，表示的是按虚拟机cpu的需求量排序
    private static PriorityQueue<Virtual> virtualListMemoryNeed = new PriorityQueue<>(Comparator.comparingLong(v -> v.memory)); //优先队列，表示的是按虚拟机内存的需求量排序

    // 所有的请求列表
    private static List<List<Request>> requestLists = new ArrayList<>();

    private static List<ServerBought> curServerBoughtList = new ArrayList<>();
    static Comparator<ServerBought> cmpCur = new Comparator<ServerBought>() {
        @Override
        public int compare(ServerBought o1, ServerBought o2) {
            return (int)(Math.max(o1.cpusizel+o1.memsizel,o1.cpusizer+o1.memsizer)-Math.max(o2.cpusizel+o2.memsizel,o2.cpusizer+o2.memsizer));
        }
    };
    static Comparator<ServerBought> cmpCurBi = new Comparator<ServerBought>() {
        @Override
        public int compare(ServerBought o1, ServerBought o2) {
            return (int)((o1.cpusizel + o1.cpusizer + o1.memsizel + o1.memsizer)-(o2.cpusizel + o2.cpusizer + o2.memsizel + o2.memsizer));
        }
//        public int compare(ServerBought o1, ServerBought o2) {
//            return (int)(Math.min(o1.cpusizel,o1.cpusizer)+Math.min(o1.memsizel,o1.memsizer))- (int)(Math.min(o2.cpusizel,o2.cpusizer)+Math.min(o2.memsizel,o2.memsizer));
//        }
    };

//    private static PriorityQueue<ServerBought> curServerBoughtList = new PriorityQueue(new Comparator<ServerBought>() {
//        public int compare(ServerBought o1, ServerBought o2) {
//            return (int)((o1.cpusizel + o1.cpusizer + o1.memsizel + o1.memsizer)-(o2.cpusizel  + o2.cpusizer + o2.memsizel + o2.memsizer));
//        }
//    });

    //    private static List<ServerBought> curServerBoughtList = new ArrayList<>();

    /**
     * 排序比较
     */
    static Comparator cmpwlh = new Comparator<Request>() {
        @Override
        public int compare(Request o1, Request o2) {
            if(!o1.operation.equals(o2.operation)){
                return o1.operation.compareTo(o2.operation);
            }else{
                if(o1.virtualName!=null&&o2.virtualName!=null&&!o1.virtualName.equals(o2.virtualName)){
                    Virtual virtual1 = VirtualMaps.get(o1.virtualName) ;
                    Virtual virtual2 = VirtualMaps.get( o2.virtualName);
                    if(virtual1.isBi!=virtual2.isBi){
                        return virtual2.isBi-virtual1.isBi;
                    }else{
                        return (int)(virtual2.cpu+virtual2.memory)-(int)(virtual1.cpu+virtual1.memory);
                    }
                }
                else{
                    return o1.rank-o2.rank;
                }
            }
        }
    };
    // 已存在的虚拟机的列表
    private static Map<String, VirtualUsed> curVirtualUsedMap = new HashMap<>();
    // 已有的购买的服务器列表数量
    private static int seqNumber = 0;
    // 文件路径
    private static final String filePath = "src/com/huawei/resource/test_data.txt";
//    private static final String filePath = "src/com/huawei/resource/training-2.txt";
    // 总费用
    private static long resFeeAll = 0;
    // 服务器购买成本
    private static long serverFee = 0;

    public static int buyNum = 0;
    // 服务器运行成本
    private static long powerFee = 0;
    //保存所有的输出信息
    private static List<String> logInfo = new ArrayList<>();
    //保存添加服务器日志
    private static Map<String,List<ServerBought>> addInfo = new HashMap<>();
    //保存部署信息
    private static List<PushInfo> putInfo = new ArrayList<>();
    //保存迁移信息
    private static List<MigrationInfo> migrateInfo = new ArrayList<>();

    public static int migrateNum = 0;
    /**
     * 读取文件
     */
    public static void read() {
        try  {
            String encoding = "utf-8" ;
            File file= new  File(filePath);
            InputStreamReader read =  new  InputStreamReader(new FileInputStream(file),encoding); //考虑到编码格式
            BufferedReader bufferedReader =  new  BufferedReader(read);
            int serverNum = Integer.parseInt(bufferedReader.readLine());
            //获取可用服务器列表
            for(int i=0;i<serverNum;i++){
                String t = bufferedReader.readLine();
                String[] strs = t.substring(1,t.length()-1).split(", ");
                ServerMaps.put(strs[0], new Server(strs));
                Server server = new Server(strs);
                double r = server.cpu*1.0/server.memory;
//                if(r >12/5 || r <5/12)
//                    continue;
                serverList.add(server);
                int ratio = (int)(server.cpu*1.0/server.memory + 0.5);
                if (ratio > 2) {
                    serverList1.add(server);
                } else if (ratio < 0.5){
                    serverList2.add(server);
                } else {
                    serverList3.add(server);
                }
            }
            int virtualNum = Integer.parseInt(bufferedReader.readLine());
            //获取可用虚拟机列表
            for (int i = 0; i < virtualNum; i++) {
                String t = bufferedReader.readLine();
                String[] strs = t.substring(1,t.length()-1).split(", ");
                VirtualMaps.put(strs[0], new Virtual(strs));
                virtualListCpuNeed.add(new Virtual(strs));
                virtualListMemoryNeed.add(new Virtual(strs));
            }
            int requestDays = Integer.parseInt(bufferedReader.readLine());
            //获取T天的用户请求序列
            for (int i = 0; i < requestDays; i++) {
                //每天的请求数
                int requestNumEveryDay = Integer.valueOf(bufferedReader.readLine());
                List<Request> virs = new ArrayList<Request>();
                for (int j = 0; j < requestNumEveryDay; j++) {
                    String t = bufferedReader.readLine();
                    String[] strs = t.substring(1, t.length() - 1).split(", ");
                    virs.add(new Request(strs,j));
                }
                //  暂时先一次性读入存储再处理。
                requestLists.add(virs);
            }
            read.close();
        } catch  (Exception e) {
            System.out.println( "读取文件内容出错" );
            e.printStackTrace();
        }
    }

    /**
     * 读取标准输入
     */
    public static void read2() {
        Scanner sc = new Scanner(System.in);
        int serverNum = Integer.parseInt(sc.nextLine());
        //获取可用服务器列表
        for(int i=0;i<serverNum;i++){
            String t = sc.nextLine();
            String[] strs = t.substring(1,t.length()-1).split(", ");
            ServerMaps.put(strs[0], new Server(strs));
            Server server = new Server(strs);
            double r = server.cpu*1.0/server.memory;
            if(r >12/5 || r <5/12)
                continue;
            serverList.add(server);
            int ratio = (int)(server.cpu*1.0/server.memory + 0.5);
            if (ratio > 1) {
                serverList1.add(server);
            } else if (ratio < 1){
                serverList2.add(server);
            } else {
                serverList3.add(server);
            }
        }
        int virtualNum = Integer.parseInt(sc.nextLine());
        //获取可用虚拟机列表
        for (int i = 0; i < virtualNum; i++) {
            String t = sc.nextLine();
            String[] strs = t.substring(1,t.length()-1).split(", ");
            VirtualMaps.put(strs[0], new Virtual(strs));
            virtualListCpuNeed.add(new Virtual(strs));
            virtualListMemoryNeed.add(new Virtual(strs));
        }
        int requestDays = Integer.parseInt(sc.nextLine());
        //获取T天的用户请求序列
        for (int i = 0; i < requestDays; i++) {
            //每天的请求数
            int requestNumEveryDay = Integer.valueOf(sc.nextLine());
            List<Request> virs = new ArrayList<>();
            for (int j = 0; j < requestNumEveryDay; j++) {
                String t = sc.nextLine();
                String[] strs = t.substring(1, t.length() - 1).split(", ");
                virs.add(new Request(strs,j));
            }
            //  暂时先一次性读入存储再处理。
            requestLists.add(virs);
        }
    }


    /**
     * 策略1：分配策略
     * 程序主处理入口:
     */
    public static void process(int z) {
        //对服务器进行排序
//        Collections.sort(serverList, cmp);
        // 处理每一天的请求
        for (int k=0;k<requestLists.size();k++) {
            // 迁移策略
            migrate5();
            List<Request> list = requestLists.get(k);
            //对每天要执行的请求进行排序
            if(k<requestLists.size()-z){
                Collections.sort(list,cmpwlh);
            }
            int serNumUntilNow = seqNumber;
            for (Request request : list) {
                if ("add".equals(request.operation)) {
                    // 获取当前Virtual的规格
                    Virtual curVirtual = VirtualMaps.get(request.virtualName);
                    List<Server> serverListSelected = switchServer(curVirtual);
                    // 1. 先判断能不能放,如果可以放的话在addMatch的时候已经完成部署
                    boolean canStore = addMatch(curVirtual, request);
                    if (!canStore) {
                        // 1。扩容策略
                        expansion(curVirtual, request, serverListSelected);
                    }
                } else {
                    // 2。删除策略
                    delVirtual(request.virtualId);
                }
            }
            // TODO 测试
//            test();
            //把每天的操作信息都放入到日志中，最后统一输出
            writeToLog(serNumUntilNow);
            calculate();
//            System.out.println(k);
        }
    }

    /**
     * 4。分配策略：判断在已有的运行的服务器列表中是否可以放下,如果可以放下就直接完成部署
     * @param curVirtual 当前要操作新增虚拟机
     * @return
     */
    public static boolean addMatch(Virtual curVirtual,Request request) {
//        if(curServerBoughtList.isEmpty()||curServerBoughtListSingle.isEmpty()){
//            return false;
//        }
        if(curServerBoughtList.isEmpty()){
            return false;
        }
        boolean flag = false;

        // 获取当前存量服务器具的堆顶
        // 可以遍历一下，用时间换成本
        try {
            if(curVirtual.isBi==1){
                Collections.sort(curServerBoughtList, cmpCurBi);
                for(ServerBought topServerBought:curServerBoughtList){
                    if(topServerBought.cpusizel>=curVirtual.cpu/2&&topServerBought.cpusizer>=curVirtual.cpu/2
                            &&topServerBought.memsizel>=curVirtual.memory/2&&topServerBought.memsizer>=curVirtual.memory/2){
                        // 添加虚拟机和服务器的映射关系(也就是完成部署)
                        VirtualUsed virtualUsed = new VirtualUsed(request.virtualId,curVirtual);
                        virtualUsed.serverBought = topServerBought;
                        virtualUsed.node = 2;
                        curVirtualUsedMap.put(request.virtualId,virtualUsed);
                        // 消耗服务器资源
                        consumeResource(topServerBought,curVirtual,virtualUsed,2);
                        // 输出部署信息
                        putInfo.add(new PushInfo(topServerBought,2,request.rank));
                        //资源重组
                        reSort(topServerBought);
                        flag = true;
                        break;
                    }
                }
            }else{
                Collections.sort(curServerBoughtList, cmpCur);
                for(ServerBought topServerBought:curServerBoughtList){
//                    /* owen added */
                    // 对于单节点虚拟机, 先选择两个节点负载低(CPU + RAM)的节点存放, 若放不下, 放入另一个节点
                    if((topServerBought.cpusizel + topServerBought.memsizel) > (topServerBought.cpusizer + topServerBought.memsizer)){
                        // 判断左节点是否能存放
                        if((topServerBought.cpusizel>=curVirtual.cpu&&topServerBought.memsizel>=curVirtual.memory)){
                            // 添加虚拟机和服务器的映射关系(也就是完成部署)
                            VirtualUsed virtualUsed = new VirtualUsed(request.virtualId,curVirtual);
                            virtualUsed.serverBought = topServerBought;
                            //单节点放在左边
                            virtualUsed.node = 0;
                            curVirtualUsedMap.put(request.virtualId,virtualUsed);
                            // 消耗服务器资源
                            consumeResource(topServerBought,curVirtual,virtualUsed,0);
                            // 输出部署信息
                            putInfo.add(new PushInfo(topServerBought,0,request.rank));
                            //资源重组
                            reSort(topServerBought);
                            flag = true;
                            break;
                        }
                        // 若左节点不能存放, 判断右节点能否存放
                        else if((topServerBought.cpusizer>=curVirtual.cpu&&topServerBought.memsizer>=curVirtual.memory)){
                            VirtualUsed virtualUsed = new VirtualUsed(request.virtualId,curVirtual);
                            virtualUsed.serverBought = topServerBought;
                            //单节点放在右边
                            virtualUsed.node = 1;
                            curVirtualUsedMap.put(request.virtualId,virtualUsed);
                            // 消耗服务器资源
                            consumeResource(topServerBought,curVirtual,virtualUsed,1);
                            // 输出部署信息
                            putInfo.add(new PushInfo(topServerBought,1,request.rank));
                            //资源重组
                            reSort(topServerBought);
                            flag = true;
                            break;
                        }
                    }
                    // 先尝试放入右节点
                    else{
                        if((topServerBought.cpusizer>=curVirtual.cpu&&topServerBought.memsizer>=curVirtual.memory)){
                            VirtualUsed virtualUsed = new VirtualUsed(request.virtualId,curVirtual);
                            virtualUsed.serverBought = topServerBought;
                            //单节点放在右边
                            virtualUsed.node = 1;
                            curVirtualUsedMap.put(request.virtualId,virtualUsed);
                            // 消耗服务器资源
                            consumeResource(topServerBought,curVirtual,virtualUsed,1);
                            // 输出部署信息
                            putInfo.add(new PushInfo(topServerBought,1,request.rank));
                            //资源重组
                            reSort(topServerBought);
                            flag = true;
                            break;
                        }else if((topServerBought.cpusizel>=curVirtual.cpu&&topServerBought.memsizel>=curVirtual.memory)){
                            //添加虚拟机和服务器的映射关系(也就是完成部署)
                            VirtualUsed virtualUsed = new VirtualUsed(request.virtualId,curVirtual);
                            virtualUsed.serverBought = topServerBought;
                            //单节点放在左边
                            virtualUsed.node = 0;
                            curVirtualUsedMap.put(request.virtualId,virtualUsed);
                            // 消耗服务器资源
                            consumeResource(topServerBought,curVirtual,virtualUsed,0);
                            // 输出部署信息
                            putInfo.add(new PushInfo(topServerBought,0,request.rank));
                            //资源重组
                            reSort(topServerBought);
                            flag = true;
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 3。删除策略。从服务器上删除虚拟机
     */
    public static void delVirtual(String virtualNo){
        VirtualUsed virtualUsed = curVirtualUsedMap.get(virtualNo);
        ServerBought serverBought = virtualUsed.serverBought;
        if(virtualUsed.node==2){
            serverBought.cpusizel+= virtualUsed.cpusize/2;
            serverBought.cpusizer+= virtualUsed.cpusize/2;
            serverBought.memsizel+= virtualUsed.memsize/2;
            serverBought.memsizer+= virtualUsed.memsize/2;
        }else if(virtualUsed.node==1){
            serverBought.cpusizer+= virtualUsed.cpusize;
            serverBought.memsizer+= virtualUsed.memsize;
        }else {
            serverBought.cpusizel+=virtualUsed.cpusize;
            serverBought.memsizel+=virtualUsed.memsize;
        }
        serverBought.virtualList.remove(virtualUsed);
        curVirtualUsedMap.remove(virtualNo);
        // 删除后重组
        reSort(serverBought);
    }

    /**
     * 选择合适的服务器列表进行遍历
     */
    public static List<Server> switchServer(Virtual curVirtual){
//        int ratio = (int)(curVirtual.cpu*1.0/curVirtual.memory + 0.5);
//        if (ratio > 1) {
//            return serverList1;
//        } else if (ratio < 1){
//            return serverList2;
//        } else {
//            return serverList3;
//        }
        return serverList;
    }


    /**
     * 消耗服务器资源
     * serverBought表示被消耗的服务器实例，virtual表示要部署的虚拟的规格，node表示怎么消耗(0表示部署在左节点，1表示在右节点，2表示双节点部署)
     */
    public static void consumeResource(ServerBought serverBought,Virtual virtual,VirtualUsed virtualUsed,int node) {
        if(node==2){
            serverBought.cpusizel-=virtual.cpu/2;
            serverBought.cpusizer-=virtual.cpu/2;
            serverBought.memsizel-=virtual.memory/2;
            serverBought.memsizer-=virtual.memory/2;
        }else if(node==1){
            serverBought.cpusizer-=virtual.cpu;
            serverBought.memsizer-=virtual.memory;
        }else{
            serverBought.cpusizel-=virtual.cpu;
            serverBought.memsizel-=virtual.memory;
        }
        serverBought.virtualList.add(virtualUsed);
    }

    /**
     * 资源重组(对服务器列表中的服务器进行重新排序)
     */
    public static void reSort(ServerBought serverBought){
//        curServerBoughtList.remove(serverBought);
//        curServerBoughtListSingle.remove(serverBought);
//        curServerBoughtList.add(serverBought);
//        curServerBoughtListSingle.add(serverBought);
    }

    /**
     * 策略1：扩容策略
     * 该怎么购买
     */
    public static void expansion(Virtual curVirtual, Request request, List<Server> serverListSelected) {
        if(curVirtual.isBi==1){
            // *** 堆顶找不到，然后找下一个，所以需要循环，直接取堆顶不行
            for (Server server : serverListSelected) {
                if (server.cpu >= curVirtual.cpu && server.memory >= curVirtual.memory) {
                    // 新建一个服务器
                    ServerBought serverBought = new ServerBought(seqNumber, server);
                    seqNumber++;
                    // 添加虚拟机和服务器的映射关系(也就是完成部署)
                    VirtualUsed virtualUsed = new VirtualUsed(request.virtualId, curVirtual);
                    virtualUsed.serverBought = serverBought;
                    virtualUsed.node = 2;
                    curVirtualUsedMap.put(request.virtualId, virtualUsed);
                    // 消耗服务器资源
                    consumeResource(serverBought, curVirtual, virtualUsed, 2);
                    // 已购买列表添加一个服务器
                    curServerBoughtList.add(serverBought);
                    // 计算成本1：购买成本
                    serverFee += server.oricost;
                    buyNum++;
                    //添加每日的购买信息
                    if (addInfo.get(serverBought.name) == null) {
                        addInfo.put(serverBought.name, new ArrayList<ServerBought>());
                    }
                    addInfo.get(serverBought.name).add(serverBought);
                    // 输出部署信息
                    putInfo.add(new PushInfo(serverBought, 2,request.rank));
                    break;
                }
            }
        }else{
            // *** 堆顶找不到，然后找下一个，所以需要循环，直接取堆顶不行
            for (Server server : serverListSelected) {
                if (server.cpu / 2 >= curVirtual.cpu && server.memory / 2 >= curVirtual.memory) {
                    double random = Math.random();
                    // 新建一个服务器
                    ServerBought serverBought = new ServerBought(seqNumber, server);
                    seqNumber++;
                    // 添加虚拟机和服务器的映射关系(也就是完成部署)
                    VirtualUsed virtualUsed = new VirtualUsed(request.virtualId, curVirtual);
                    virtualUsed.serverBought = serverBought;
                    //单节点先统一放在右边
                    virtualUsed.node = 1;
                    curVirtualUsedMap.put(request.virtualId, virtualUsed);
                    // 消耗服务器资源
                    consumeResource(serverBought, curVirtual, virtualUsed, 1);
                    //已购买列表添加一个服务器
                    curServerBoughtList.add(serverBought);
                    // 计算成本1：购买成本
                    serverFee += server.oricost;
                    buyNum++;
                    //添加每日的购买信息
                    if (addInfo.get(serverBought.name) == null) {
                        addInfo.put(serverBought.name, new ArrayList<ServerBought>());
                    }
                    addInfo.get(serverBought.name).add(serverBought);
                    // 输出部署信息
                    putInfo.add(new PushInfo(serverBought, 1,request.rank));
                    break;
                }
            }
        }
    }

    // TODO 按梯度下降，前result结果0-0.1,0.1-0.2,0.2-0.3;总体剩余比例大的在前面，同样比例梯度，空闲空间大的在前面
    static Comparator<ServerBought> cmpMigra = new Comparator<ServerBought>() {
        @Override
        public int compare(ServerBought o1, ServerBought o2) {
            // 计算空闲比例梯度
            int ratio_o1 = (int)(o1.cpusizel + o1.cpusizer + o1.memsizel + o1.memsizer)*10 / (int)(o1.cpu + o1.memory);
            int ratio_o2 = (int)(o2.cpusizel + o2.cpusizer + o2.memsizel + o2.memsizer)*10 / (int)(o2.cpu + o2.memory);
            // 若梯度相同, 比较空闲空间
            // 空闲空间大的, 排在右边
            if(ratio_o1 == ratio_o2){
                int result = (int)(o1.cpusizel + o1.cpusizer + o1.memsizel + o1.memsizer) -
                        (int)(o2.cpusizel + o2.cpusizer + o2.memsizel + o2.memsizer);
                if(result < 0)
                    return 1;
                else if(result > 0)
                    return -1;
                else
                    return 0;
            }
            else if (ratio_o1 < ratio_o2)
                return 1;
            else
                return -1;
        }
    };

    // 按闲置率
    static Comparator<ServerBought> cmpMigr2 = new Comparator<ServerBought>() {
        @Override
        public int compare(ServerBought o1, ServerBought o2) {
            double result = (o1.cpusizel + o1.cpusizer + o1.memsizel + o1.memsizer)*1.0 / (o1.cpu + o1.memory) -
                    (o2.cpusizel + o2.cpusizer + o2.memsizel + o2.memsizer)*1.0 / ( o2.cpu + o2.memory);
            if (result < 0) {
                return 1;
            } else if(result > 0) {
                return -1;
            }else {
                return 0;
            }
        }
    };

    // 按空间
    static Comparator<ServerBought> cmpMigr3 = new Comparator<ServerBought>() {
        @Override
        public int compare(ServerBought o1, ServerBought o2) {
            return (int)((o1.cpusizel + o1.cpusizer + o1.memsizel + o1.memsizer)-(o2.cpusizel + o2.cpusizer + o2.memsizel + o2.memsizer));
        }
    };

    static Comparator<ServerBought> cmpMigr4 = new Comparator<ServerBought>() {
        @Override
        public int compare(ServerBought o1, ServerBought o2) {
            return (int)((o2.cpusizel + o2.cpusizer + o2.memsizel + o2.memsizer)-(o1.cpusizel + o1.cpusizer + o1.memsizel + o1.memsizer));
        }
    };

    // 请求排序
    static Comparator<VirtualUsed> cmpvir = new Comparator<VirtualUsed>() {
        @Override
        public int compare(VirtualUsed o1, VirtualUsed o2) {
            if(o1.isBi!=o2.isBi){
                return o2.isBi-o1.isBi;
            }else{
                return (int)(o2.cpusize+o2.memsize)-(int)(o1.cpusize+o1.memsize);
            }
        }
    };


    /**
     * 策略2：迁移策略
     * 每次选择空闲比例最大的服务器,作为迁出服务器
     */
    public static void migrate() {
        // 迁移虚拟机
        List<ServerBought> migrateServerBoughtList = new ArrayList<>();
        // 将服务器加入List
        for(ServerBought tmp: curServerBoughtList){
            migrateServerBoughtList.add(tmp);
        }
        Collections.sort(migrateServerBoughtList, cmpMigra);
        // 计算最大迁移次数
        int leftMigNum = (int)Math.floor(curVirtualUsedMap.size()*0.005);
        int left = 0, right = migrateServerBoughtList.size() - 1;
        // 不断迁移
        while(left < right && leftMigNum > 0){
            // 选出空闲比例最大和最小的服务器
            ServerBought maxFreeServerBought = migrateServerBoughtList.get(left);
            // 请求排序
            Collections.sort(maxFreeServerBought.virtualList, cmpvir);
            ServerBought minFreeServerBought = migrateServerBoughtList.get(right);
            int changNum = change(maxFreeServerBought, minFreeServerBought, leftMigNum);
            leftMigNum -= changNum;
            migrateNum += changNum;
            if (leftMigNum <= 0) {
                break;
            }
            // 如果left所有的虚拟机都到了right位置的虚拟机了
            if (fromIsEmpty(maxFreeServerBought)) {
                left++;
                // 每次right从最后面开始
                right = migrateServerBoughtList.size() - 1;
            } else {
                // 先换to
                right--;
            }
        }
    }

    public static void migrate5() {
        // 迁移虚拟机
        List<ServerBought> migrateServerBoughtList = new ArrayList<>();
        // 将服务器加入List
        for(ServerBought tmp: curServerBoughtList){
            migrateServerBoughtList.add(tmp);
        }
        Collections.sort(migrateServerBoughtList, cmpMigr2);
        Set<String> migrated = new HashSet<>();
        // 计算最大迁移次数,按闲置率从大到小，往小的迁移。
        int maxMigNum = (int)Math.floor(curVirtualUsedMap.size()*0.005);
        if(maxMigNum<1){
            return;
        }
        // 迁移次数
        int leftMigNum = maxMigNum;
        Collections.sort(migrateServerBoughtList, cmpMigra);
        int left = 0, right = migrateServerBoughtList.size() - 1;
        // 不断迁移
        while(left < right && leftMigNum > 0) {
            // 选出空闲比例最大和最小的服务器
            ServerBought maxFreeServerBought = migrateServerBoughtList.get(left);
            // 请求排序
            Collections.sort(maxFreeServerBought.virtualList, cmpvir);
            ServerBought minFreeServerBought = migrateServerBoughtList.get(right);
            int changNum = change(maxFreeServerBought, minFreeServerBought, leftMigNum);
            leftMigNum -= changNum;
            migrateNum += changNum;
            if (leftMigNum <= 0) {
                break;
            }
            // 如果left所有的虚拟机都到了right位置的虚拟机了
            if (fromIsEmpty(maxFreeServerBought)) {
                left++;
                // 每次right从最后面开始
                right = migrateServerBoughtList.size() - 1;
            } else {
                // 先换to
                right--;
            }
        }
//        if (leftMigNum > 0) {
//            // 按空间剩余从大到小排序，空间剩余大的往小的迁移
//            Collections.sort(migrateServerBoughtList, cmpMigr4);
//            left = 0;
//            right = migrateServerBoughtList.size() - 1;
//            while (left < right && leftMigNum > 0) {
//                ServerBought maxFreeServerBought = migrateServerBoughtList.get(left);
//                // 请求排序
////                Collections.sort(maxFreeServerBought.virtualList, cmpvir);
//                ServerBought minFreeServerBought = migrateServerBoughtList.get(right);
//                int changNum = change(maxFreeServerBought, minFreeServerBought, leftMigNum);
//                leftMigNum -= changNum;
//                migrateNum += changNum;
//                if (leftMigNum <= 0) {
//                    break;
//                }
//                // 如果left所有的虚拟机都到了right位置的虚拟机了
//                if (fromIsEmpty(maxFreeServerBought)) {
//                    left++;
//                    // 每次right从最后面开始
//                    right = migrateServerBoughtList.size() - 1;
//                } else {
//                    // 先换to
//                    right--;
//                }
//            }
//        }
        if (leftMigNum > 0) {
            // 按空间剩余从大到小排序，空间剩余大的往旁边迁移
            Collections.sort(migrateServerBoughtList, cmpMigr2);
            left = 0;
            right = left + 1;
            // 不断迁移
            while(left + 1 < migrateServerBoughtList.size() && leftMigNum > 0){
                // 选出空闲比例最大和最小的服务器
                ServerBought maxFreeServerBought = migrateServerBoughtList.get(left);
                // 请求排序
//                Collections.sort(maxFreeServerBought.virtualList, cmpvir);
                ServerBought minFreeServerBought = migrateServerBoughtList.get(right);
                int changNum = change(maxFreeServerBought, minFreeServerBought, leftMigNum);
                leftMigNum -= changNum;
                migrateNum += changNum;
                if (leftMigNum <= 0) {
                    break;
                }
                // 如果left所有的虚拟机都到了right位置的虚拟机了
                if (fromIsEmpty(maxFreeServerBought)) {
                    left++;
                    right = left + 1;
                } else {
                    // TODO 下面这个步伐影响速度
                    right += 16;
                }
                if (right >= migrateServerBoughtList.size()) {
                    left++;
                    right = left + 1;
                }
            }
        }
    }


    public static boolean fromIsEmpty(ServerBought from) {
        return from.virtualList.size() == 0;
    }
    /**
     * 将from服务器上面的虚拟机放到to服务器上面
     * @param from 空闲率大的服务器
     * @param to 空闲率小的服务器，也就是负载高的
     * @param leftMigNum 当前剩余的迁移次数
     * @return
     */

    public static int change(ServerBought from, ServerBought to, int leftMigNum) {
        boolean[] toDel = new boolean[from.virtualList.size()];
        int curNow = 0;
        ArrayList<VirtualUsed> fromList = from.virtualList;
        // 空闲比例最小的服务器的容量
        // TODO 优化 迁移策略
        // 先吧fromList（默认顺序）里面的放到to(服务器)里面
        for(int h =0;h<fromList.size();h++){
            VirtualUsed virtualUsed = fromList.get(h);
            //如果部署在原服务器的双节点上
            if (virtualUsed.node == 2) {
                if (Math.min(to.cpusizel, to.cpusizer) >= virtualUsed.cpusize/2 &&
                        Math.min(to.memsizel, to.memsizer) >= virtualUsed.memsize/2) {
                    resoureceChangeBi(from,to,virtualUsed);
                    toDel[h]=true;
                    curNow++;
                }
            }//部署在原服务器的右节点的话(那么只对原服务器的右边资源进行修改)
            else if(virtualUsed.node == 1) {
                //先判断目标服务器哪一边的容量比较大
                //如果左边的容量比较大,那么就先判断左再判断右
                if(to.cpusizel+to.memsizel>=to.cpusizer+to.memsizer){
                    if (to.cpusizel >= virtualUsed.cpusize && to.memsizel >= virtualUsed.memsize) {
                        // 记录迁移日志,放服务器A节点
                        resoureceChangeOnly(from,1,to,0,virtualUsed);
                        toDel[h]=true;
                        curNow++;
                    } else if (to.cpusizer >= virtualUsed.cpusize && to.memsizer >= virtualUsed.memsize) {
                        // 记录迁移日志,放服务器B节点
                        resoureceChangeOnly(from,1,to,1,virtualUsed);
                        toDel[h]=true;
                        curNow++;
                    }
                }else{
                    if (to.cpusizer >= virtualUsed.cpusize && to.memsizer >= virtualUsed.memsize) {
                        // 记录迁移日志,放服务器B节点
                        resoureceChangeOnly(from,1,to,1,virtualUsed);
                        toDel[h]=true;
                        curNow++;
                    }else if (to.cpusizel >= virtualUsed.cpusize && to.memsizel >= virtualUsed.memsize) {
                        // 记录迁移日志,放服务器A节点
                        resoureceChangeOnly(from,1,to,0,virtualUsed);
                        toDel[h]=true;
                        curNow++;
                    }
                }
            }//部署在原服务器的左节点的话
            else{
                //先判断目标服务器哪一边的容量比较大
                //如果左边的容量比较大,那么就先判断左再判断右
                if(to.cpusizel+to.memsizel>=to.cpusizer+to.memsizer){
                    if (to.cpusizel >= virtualUsed.cpusize && to.memsizel >= virtualUsed.memsize) {
                        // 记录迁移日志,放服务器A节点
                        resoureceChangeOnly(from,0,to,0,virtualUsed);
                        toDel[h]=true;
                        curNow++;
                    } else if (to.cpusizer >= virtualUsed.cpusize && to.memsizer >= virtualUsed.memsize) {
                        // 记录迁移日志,放服务器B节点
                        resoureceChangeOnly(from,0,to,1,virtualUsed);
                        toDel[h]=true;
                        curNow++;
                    }
                }else{
                    if (to.cpusizer >= virtualUsed.cpusize && to.memsizer >= virtualUsed.memsize) {
                        // 记录迁移日志,放服务器B节点
                        resoureceChangeOnly(from,0,to,1,virtualUsed);
                        toDel[h]=true;
                        curNow++;
                    }else if (to.cpusizel >= virtualUsed.cpusize && to.memsizel >= virtualUsed.memsize) {
                        // 记录迁移日志,放服务器A节点
                        resoureceChangeOnly(from,0,to,0,virtualUsed);
                        toDel[h]=true;
                        curNow++;
                    }
                }
            }
            if(curNow>=leftMigNum){
                //TODO  从愿列表中删去虚拟机
                toDel(from,toDel);
                return curNow;
            }
        }
        // TODO  从愿列表中删去虚拟机
        toDel(from,toDel);
        return curNow;
    }

    /**
     *
     */
    public static void toDel(ServerBought from,boolean[] toDel){
        ArrayList<VirtualUsed> list = from.virtualList;
        ArrayList<VirtualUsed> newList = new ArrayList<>();
        for(int h=0;h<toDel.length;h++){
            if(!toDel[h]){
                newList.add(list.get(h));
            }
        }
        from.virtualList = newList;
    }

    /**
     * 双节点虚拟机迁移资源修正
     */
    //迁移的时候要修各自服务器的虚拟机列表，然后更新现有的虚拟机列表
    public static void resoureceChangeBi(ServerBought from,ServerBought to,VirtualUsed virtual){
        to.cpusizel -= virtual.cpusize/2;
        to.cpusizer -= virtual.cpusize/2;
        to.memsizel -= virtual.memsize/2;
        to.memsizer -= virtual.memsize/2;

        from.cpusizel += virtual.cpusize/2;
        from.cpusizer += virtual.cpusize/2;
        from.memsizel += virtual.memsize/2;
        from.memsizer += virtual.memsize/2;
        //修改服务器中虚拟机存在函数
        to.virtualList.add(virtual);
        //修改虚拟机所在服务器
        virtual.serverBought = to;
        migrateInfo.add(new MigrationInfo(virtual.no,to,2));
    }

    /**
     * 单节点虚拟机迁移资源修正
     */
    //迁移的时候要修各自服务器的虚拟机列表，然后更新现有的虚拟机列表
    public static void resoureceChangeOnly(ServerBought from,int fromNode,ServerBought to,int toNode,VirtualUsed virtualUsed){
        //从右节点迁移到左节点
        if(fromNode==1&&toNode==0){
            to.cpusizel -= virtualUsed.cpusize;
            to.memsizel -= virtualUsed.memsize;

            from.cpusizer += virtualUsed.cpusize;
            from.memsizer += virtualUsed.memsize;
            virtualUsed.node=0;
        }//从右节点迁移到右节点
        else if(fromNode==1&&toNode==1){
            to.cpusizer -= virtualUsed.cpusize;
            to.memsizer -= virtualUsed.memsize;

            from.cpusizer += virtualUsed.cpusize;
            from.memsizer += virtualUsed.memsize;
            virtualUsed.node=1;
        }//从左节点迁移到左节点
        else if(fromNode==0&&toNode==0){
            to.cpusizel -= virtualUsed.cpusize;
            to.memsizel -= virtualUsed.memsize;

            from.cpusizel += virtualUsed.cpusize;
            from.memsizel += virtualUsed.memsize;
            virtualUsed.node=0;
        }//从左节点迁移到右节点
        else{
            to.cpusizer -= virtualUsed.cpusize;
            to.memsizer -= virtualUsed.memsize;

            from.cpusizel += virtualUsed.cpusize;
            from.memsizel += virtualUsed.memsize;
            virtualUsed.node=1;
        }
        //修改服务器中虚拟机存在函数
        to.virtualList.add(virtualUsed);
        //修改虚拟机所在服务器
        virtualUsed.serverBought = to;
        migrateInfo.add(new MigrationInfo(virtualUsed.no,to,toNode));
    }

    /**
     * 输出结果
     */
    public static void write() {
        for(String log:logInfo){
            System.out.println(log);
        }
    }

    /**
     * 输出总费用
     */
    public static void write2(){
        serverFee += powerFee;
        System.out.println(serverFee);
        System.out.println("已购买的服务器大小 "  + curServerBoughtList.size());
        System.out.println("迁移次数 " + migrateNum);
    }

    /**
     * 计算每天的费用
     */
    public static void calculate(){
        for(ServerBought serverBought:curServerBoughtList){
            if(serverBought.cpusizel!=serverBought.cpu/2||serverBought.cpusizer!=serverBought.cpu/2
                    ||serverBought.memsizel!=serverBought.memory/2||serverBought.memsizer!=serverBought.memory/2)
                powerFee+=serverBought.dailycost;
        }
    }

    /**
     * 将每日信息都存入到日志汇总
     */
    public static void writeToLog(int serNumUntilNow) {
        //输出购买种类
        logInfo.add("(purchase, "+addInfo.size()+")");
        //对每个种类输出信息
        for(String servernm:addInfo.keySet()){
            List<ServerBought> tmp = addInfo.get(servernm);
            for(ServerBought serverBought:tmp){
                serverBought.no=serNumUntilNow++;
            }
            logInfo.add("("+servernm+", "+tmp.size()+")");
        }
        // 每天的迁移日志
        logInfo.add("(migration, " + migrateInfo.size() + ")");
        for(MigrationInfo migrationInfo:migrateInfo){
            String str = "("+migrationInfo.vitualId+", "+migrationInfo.toServer.no;
            str+=(migrationInfo.tonode==2?")":", "+(migrationInfo.tonode==0?"A":"B")+")");
            logInfo.add(str);
        }
        Collections.sort(putInfo, new Comparator<PushInfo>() {
            @Override
            public int compare(PushInfo o1, PushInfo o2) {
                return o1.rank-o2.rank;
            }
        });
        for(int i=0;i<putInfo.size();i++){
            int serverno = putInfo.get(i).serverBought.no;
            if(putInfo.get(i).pushNode==2){
                logInfo.add("("+serverno+")");
            }else if(putInfo.get(i).pushNode==1){
                logInfo.add("("+serverno+", B)");
            }else{
                logInfo.add("("+serverno+", A)");
            }
        }
        //清空每日信息
        addInfo.clear();
        migrateInfo.clear();
        putInfo.clear();
    }

    /**
     * 测试服务器负载
     */
    public static void test() {
    }
}

