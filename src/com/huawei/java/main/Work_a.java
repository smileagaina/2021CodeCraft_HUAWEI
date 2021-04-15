package com.huawei.java.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Work_a {

    private static double ratioServer1 = 2.0;
    private static double ratioServer2 = 1.0/ratioServer1;

    private static double ratioVirtual1 = 4.0;
    private static double ratioVirtual2 = 1.0/ratioVirtual1;

    // filter
    private static double filterRation1 = 2.0;
    private static double filterRation2 = 1.0/filterRation1;
    // expansion参数
    private static double expansion_para_cpu = 1.75;
    private static double expansion_para_ram = 1.8;

    private static int balance1 = 5;
    private static int balance2 = 50;
    // 服务器信息映射
    private static Map<String, Server> ServerMaps = new HashMap<>();
    // 虚拟机信息映射
    private static Map<String, Virtual> VirtualMaps = new HashMap<>();

    static Comparator<Server> cmp = Comparator.comparingDouble(s -> s.oricost);
    private static List<Server> serverList = new ArrayList<>(); // 优先队列，服务器的价格进行一个排序

    private static List<Server> serverList1 = new ArrayList<>(); // 优先队列，服务器的价格进行一个排序
    private static List<Server> serverList2 = new ArrayList<>(); // 优先队列，服务器的价格进行一个排序
    private static List<Server> serverList3 = new ArrayList<>(); // 优先队列，服务器的价格进行一个排序

    private static PriorityQueue<Virtual> virtualListCpuNeed = new PriorityQueue<>(Comparator.comparingLong(v -> v.cpu)); //优先队列，表示的是按虚拟机cpu的需求量排序
    private static PriorityQueue<Virtual> virtualListMemoryNeed = new PriorityQueue<>(Comparator.comparingLong(v -> v.memory)); //优先队列，表示的是按虚拟机内存的需求量排序

    // 所有的请求列表
    private static List<List<Request>> requestLists = new ArrayList<>();

    private static List<ServerBought> curServerBoughtList = new ArrayList<>();

    private static List<ServerBought> curServerBoughtList1 = new ArrayList<>();
    private static List<ServerBought> curServerBoughtList2 = new ArrayList<>();
    private static List<ServerBought> curServerBoughtList3 = new ArrayList<>();

    static Comparator<ServerBought> cmpCur = new Comparator<ServerBought>() {
        @Override
        public int compare(ServerBought o1, ServerBought o2) {
            return (int)(Math.max(o1.cpusizel+o1.memsizel,o1.cpusizer+o1.memsizer)-Math.max(o2.cpusizel+o2.memsizel,o2.cpusizer+o2.memsizer));
        }
    };

    static Comparator<ServerBought> cmpCurBi = new Comparator<ServerBought>() {
        @Override
        public int compare(ServerBought o1, ServerBought o2) {
//            return (int)((Math.min(o1.cpusizel,o1.cpusizer)+Math.min(o1.memsizel,o1.memsizer))-(Math.min(o2.cpusizel,o2.cpusizer)+Math.min(o2.memsizel,o2.memsizer)));
            return (int)((o1.cpusizel + o1.cpusizer + o1.memsizel + o1.memsizer)-(o2.cpusizel + o2.cpusizer + o2.memsizel + o2.memsizer));
        }
    };

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
    private static String filePath = "src/com/huawei/resource/复赛数据/training-";
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

    static int T, K;
    /**
     * 复赛
     */
    public static void read_new(String where) {
        filePath = filePath + where;
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
                if(r >filterRation1 || r <filterRation2)
                    continue;
                serverList.add(server);
                double ratio = (server.cpu*1.0/server.memory);
                if (ratio > ratioServer1) {
                    serverList1.add(server);
                } else if (ratio < ratioServer2){
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

            String[] requestDays = bufferedReader.readLine().split(" ");
            T = Integer.parseInt(requestDays[0]);
            K = Math.min(Integer.parseInt(requestDays[1]), T);
            // 前k天的请求
            int for_i = 0;
            while (for_i++ < K) {
                //每天的请求数
                int requestNumEveryDay = Integer.valueOf(bufferedReader.readLine());
                List<Request> virsK = new ArrayList<Request>();
                for (int j = 0; j < requestNumEveryDay; j++) {
                    String t = bufferedReader.readLine();
                    String[] strs = t.substring(1, t.length() - 1).split(", ");
                    virsK.add(new Request(strs,j));
                }
                requestLists.add(virsK);
            }
            //获取T-K天的用户请求序列
            for (int i = K; i < T; i++) {
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
     * 策略1：分配策略
     * 本地程序主处理入口:
     */
    public static void process_new() {
        // 对服务器进行排序，对决策不影响，只对购买新服务器有影响。
        Collections.sort(serverList, cmp);
        // 本地就按原始输入进行输出
        for (int k = 0; k < T; k++) {
            // 策略操作
            process_everyDay(k);
            // 输出
//            write();
            calculate();
        }
    }



    public static void read_new2_firstKdays() {
        Scanner sc = new Scanner(System.in);
        int serverNum = Integer.parseInt(sc.nextLine());
        //获取可用服务器列表
        for(int i=0;i<serverNum;i++){
            String t = sc.nextLine();
            String[] strs = t.substring(1,t.length()-1).split(", ");
            ServerMaps.put(strs[0], new Server(strs));
            Server server = new Server(strs);
            double r = server.cpu*1.0/server.memory;
            if(r >filterRation1 || r <filterRation2)
                continue;
            serverList.add(server);
            double ratio = (server.cpu*1.0/server.memory);
            if (ratio > ratioServer1) {
                serverList1.add(server);
            } else if (ratio < ratioServer2){
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
        String[] requestDays = sc.nextLine().split(" ");
        T = Integer.parseInt(requestDays[0]);
        K = Math.min(Integer.parseInt(requestDays[1]), T);
        // 前k天的请求
        int for_i = 0;
        while (for_i++ < K) {
            //每天的请求数
            int requestNumEveryDay = Integer.valueOf(sc.nextLine());
            List<Request> virsK = new ArrayList<Request>();
            for (int j = 0; j < requestNumEveryDay; j++) {
                String t = sc.nextLine();
                String[] strs = t.substring(1, t.length() - 1).split(", ");
                virsK.add(new Request(strs,j));
            }
            requestLists.add(virsK);
        }
    }

    /**
     * 后面的每一个读入
     */
    public static void read_new2_everyday() {
        Scanner sc = new Scanner(System.in);
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

    /**
     * 策略1：分配策略
     * 程序主处理入口:
     */
    public static void process_new2() {
        // 对服务器进行排序，对决策不影响，只对购买新服务器有影响。
        Collections.sort(serverList, cmp);
        // 后面还要读T - K 天，总共读入T天； 然后从K天开始，总共输出T天。
        for (int k = K; k < T; k++) {
            // 1.输出前一天的操作
            process_everyDay(k - K);
            write();
            // 当前是第k+1天。
            // 2.读后面的一天
            // 每天的请求数
            read_new2_everyday();
        }
        // 上面总共输出了n - k天，还有最后k天没有输出
        for (int k = T - K; k < T; k++) {
            process_everyDay(k);
            write();
        }
    }

    /**
     * 分段sort
     * @param requestList
     * @param cmpwlh
     */
    public static void sortEveryDay(List<Request> requestList, Comparator<Request> cmpwlh) {
        int len = requestList.size();
        int i = 0;
        int start = -1;
        while (true) {
            if (i >= len) {
                break;
            }
            if (start != -1 && "del".equals(requestList.get(i).operation)) {
                int end = i;
                // 对[start, end)之间的进行排序
                Collections.sort(requestList.subList(start, end), cmpwlh);
                start = -1;
            }
            while (i < len && "del".equals(requestList.get(i).operation)) {
                i++;
            }
            if (i >= len) {
                break;
            }
            // 找到第一个add的位置
            if (start == -1 && "add".equals(requestList.get(i).operation)) {
                start = i;
            }
            i++;
        }
    }
    /**
     * 处理的每一天
     * @param now
     */
    public static void process_everyDay(int now) {
        // 迁移策略
//        migrate5_new(now);
        migrate222(now);
        List<Request> list = requestLists.get(now);
        // 对每一天的请求排序
        sortEveryDay(list, cmpwlh);
//        Collections.sort(list,cmpwlh);
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
                    expansion_new(curVirtual, request, serverListSelected);
                }
            } else {
                // 2。删除策略
                delVirtual(request.virtualId);
            }
        }
        //把每天的操作信息都放入到日志中，最后统一输出
        writeToLog(serNumUntilNow);
    }
    static boolean flag = false;
    // 先把今天del的迁移
    public static void migrate222(int day) {

        // 每天开始先将 can置为 0
        for(ServerBought serverBought:curServerBoughtList){
            serverBought.can = 0;
        }

        int balance1 = 5;
        int balance2 = 50;
        // 计算最大迁移次数
        int leftMigNum = (int) Math.floor(curVirtualUsedMap.size() * 0.03);
        // 迁移过的虚拟机
        Set<String> migrated = new HashSet<>();

        // 筛选除今日删除的虚拟机, 统一迁往空闲的服务器
        // 今日的请求
        List<Request> list = requestLists.get(day);
        // 选出del的虚拟机集合
        List<VirtualUsed> delVirtualList = new ArrayList<>();
        for(Request r:list){
            if(r.operation.equals("del")){
                delVirtualList.add(curVirtualUsedMap.get(r.virtualId));
            }
        }


        // 空闲的服务器
        List<ServerBought> freeServerBoughtList = new ArrayList<>();
        for(ServerBought tmp: curServerBoughtList){
            if((tmp.cpusizel + tmp.cpusizer == tmp.cpu) && (tmp.memsizel + tmp.memsizer == tmp.memory)){
                freeServerBoughtList.add(tmp);
            }
        }
        // 将del的虚拟机迁移
        for(VirtualUsed curVirtual:delVirtualList){
            // best fit
            // 剩余空间排序
//            Collections.sort(freeServerBoughtList, cmpCur);
            // 剩余空间排序
            if(curVirtual.isBi==2){
                Collections.sort(freeServerBoughtList, cmpCurBi);
            }
            else{
                Collections.sort(freeServerBoughtList, cmpCur);
            }
            // 选择一台剩余空间小, 且能够容纳的服务器进行迁移
            for(ServerBought serverBought:freeServerBoughtList){
                leftMigNum -= changeForDel(curVirtual, curVirtual.serverBought, serverBought, leftMigNum, migrated);;
                serverBought.can = 1;
                break;
            }
            /*
            if(curVirtual.isBi==2){
                // 剩余空间排序
                Collections.sort(freeServerBoughtList, cmpCurBi);
                // 选择一台剩余空间小, 且能够容纳的服务器进行迁移
                for(ServerBought serverBought:freeServerBoughtList){
                    if( serverBought.cpusizel > curVirtual.cpusize/2 && serverBought.cpusizer > curVirtual.cpusize/2 &&
                        serverBought.memsizel > curVirtual.memsize/2 && serverBought.memsizer > curVirtual.memsize/2 ){
                        // 迁移
                        changeForDel(curVirtual, curVirtual.serverBought, serverBought, leftMigNum, migrated);
                        leftMigNum--;
                        // 设置为不可被迁移
                        serverBought.can = 1;
                        break;
                    }
                }
            }
            // 单节点
            else{
                // 剩余空间排序
                Collections.sort(freeServerBoughtList, cmpCur);
                // 选择一台剩余空间小, 且能够容纳的服务器进行迁移
                for(ServerBought serverBought:freeServerBoughtList){
                    // 先选择小的节点尝试
                    // 左节点小
                    if((serverBought.cpusizel + serverBought.memsizel) > (serverBought.cpusizer + serverBought.memsizer)){
                        // 判断左节点是否能存放
                        if((serverBought.cpusizel>=curVirtual.cpusize&&serverBought.memsizel>=curVirtual.memsize)){
                            changeForDel(curVirtual, curVirtual.serverBought, serverBought, leftMigNum, migrated);
                            serverBought.can = 1;
                            break;
                        }
                        // 若左节点不能存放, 判断右节点能否存放
                        else if((serverBought.cpusizer>=curVirtual.cpusize&&serverBought.memsizer>=curVirtual.memsize)){
                            changeForDel(curVirtual, curVirtual.serverBought, serverBought, leftMigNum, migrated);
                            serverBought.can = 1;
                            break;
                        }
                    }
                    // 先尝试放入右节点
                    else{
                        if((serverBought.cpusizer>=curVirtual.cpusize&&serverBought.memsizer>=curVirtual.memsize)){
                            changeForDel(curVirtual, curVirtual.serverBought, serverBought, leftMigNum, migrated);
                            serverBought.can = 1;
                            break;
                        }else if((serverBought.cpusizel>=curVirtual.cpusize&&serverBought.memsizel>=curVirtual.memsize)){
                            changeForDel(curVirtual, curVirtual.serverBought, serverBought, leftMigNum, migrated);
                            serverBought.can = 1;
                            break;
                        }
                    }
                }
            }*/
        }


        // 迁移虚拟机
        List<ServerBought> migrateServerBoughtList = new ArrayList<>();
        // 将服务器加入List
        for(ServerBought tmp: curServerBoughtList) {
            if(tmp.can == 0)
                migrateServerBoughtList.add(tmp);
        }

        int left = 0, right = migrateServerBoughtList.size() - 1;
        // 不断迁移
        int cycle_num = 0;
        while (cycle_num < 10 && leftMigNum > 0) {
            Collections.sort(migrateServerBoughtList, cmpMigra);
            while (left < right && leftMigNum > 0) {
                // 选出空闲比例最大和最小的服务器
                ServerBought maxFreeServerBought = migrateServerBoughtList.get(left);
                if (maxFreeServerBought.cpusizel == maxFreeServerBought.cpu / 2 && maxFreeServerBought.cpusizer == maxFreeServerBought.cpu / 2) {
                    left++;
                    right = migrateServerBoughtList.size() - 1;
                    continue;
                }
                // 请求排序
                Collections.sort(maxFreeServerBought.virtualList, cmpvir);
                ServerBought minFreeServerBought = migrateServerBoughtList.get(right);
                int changNum = change(maxFreeServerBought, minFreeServerBought, leftMigNum, migrated);
                leftMigNum -= changNum;
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
            left++;
            right = migrateServerBoughtList.size() - 1;
            cycle_num++;
        }
        if (leftMigNum > 0) {
            // 按空间剩余从大到小排序，空间剩余大的往旁边迁移
            Collections.sort(migrateServerBoughtList, cmpMigra);
            left = 0;
            right = left + 1;
            // 不断迁移
            while(left + 1 < migrateServerBoughtList.size() && leftMigNum > 0){
                // 选出空闲比例最大和最小的服务器
                ServerBought maxFreeServerBought = migrateServerBoughtList.get(left);
                if (maxFreeServerBought.cpusizel == maxFreeServerBought.cpu / 2 && maxFreeServerBought.cpusizer == maxFreeServerBought.cpu / 2) {
                    left++;
                    right = left + 1;
                    continue;
                }
                // 请求排序
//                Collections.sort(maxFreeServerBought.virtualList, cmpvir);
                ServerBought minFreeServerBought = migrateServerBoughtList.get(right);
                int changNum = change(maxFreeServerBought, minFreeServerBought, leftMigNum,migrated);
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
                    right += 20;
                }
                if (right >= migrateServerBoughtList.size()) {
                    left++;
                    right = left + 1;
                }
            }
        }

        if (leftMigNum == 0 && flag == false) {
            flag = true;
            leftMigNum = (int) Math.floor(curVirtualUsedMap.size() * 0.97);
            // 按空间剩余从大到小排序，空间剩余大的往旁边迁移
            Collections.sort(migrateServerBoughtList, cmpMigra);
            left = 0;
            right = left + 1;
            // 不断迁移
            while(left + 1 < migrateServerBoughtList.size() && leftMigNum > 0){
                // 选出空闲比例最大和最小的服务器
                ServerBought maxFreeServerBought = migrateServerBoughtList.get(left);
                if (maxFreeServerBought.cpusizel == maxFreeServerBought.cpu / 2 && maxFreeServerBought.cpusizer == maxFreeServerBought.cpu / 2) {
                    left++;
                    right = left + 1;
                    continue;
                }
                // 请求排序
//                Collections.sort(maxFreeServerBought.virtualList, cmpvir);
                ServerBought minFreeServerBought = migrateServerBoughtList.get(right);
                int changNum = change(maxFreeServerBought, minFreeServerBought, leftMigNum,migrated);
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
                    right += 20;
                }
                if (right >= migrateServerBoughtList.size()) {
                    left++;
                    right = left + 1;
                }
            }
        }
//        if (leftMigNum > 0) {
//            System.out.println(day + " kkkk");
//        }
//        System.out.println("left + right " + left + " " + right + " " + migrateServerBoughtList.size());
    }

    // 为了新的迁移策略的具体迁移操作
    public static int changeForDel(VirtualUsed virtualUsed, ServerBought from, ServerBought to, int leftMigNum , Set<String> migrated) {
        // 判断够不够
        if ((to.cpusizel == 0 || to.cpusizer == 0) || (to.memsizel == 0 || to.memsizer == 0)) {
            return 0;
        }
        boolean[] toDel = new boolean[from.virtualList.size()];
        // 查找virtualUsed在from中的下标志
        int h = from.virtualList.indexOf(virtualUsed);
        int curNow = 0;
        // 空闲比例最小的服务器的容量
        // TODO 优化 迁移策略
        //如果部署在原服务器的双节点上
        if (virtualUsed.node == 2) {
            if (Math.min(to.cpusizel, to.cpusizer) >= virtualUsed.cpusize/2 &&
                    Math.min(to.memsizel, to.memsizer) >= virtualUsed.memsize/2) {
                resoureceChangeBi(from,to,virtualUsed,migrated);
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
                    resoureceChangeOnly(from,1,to,0,virtualUsed,migrated);
                    toDel[h]=true;
                    curNow++;
                } else if (to.cpusizer >= virtualUsed.cpusize && to.memsizer >= virtualUsed.memsize) {
                    // 记录迁移日志,放服务器B节点
                    resoureceChangeOnly(from,1,to,1,virtualUsed,migrated);
                    toDel[h]=true;
                    curNow++;
                }
            }else{
                if (to.cpusizer >= virtualUsed.cpusize && to.memsizer >= virtualUsed.memsize) {
                    // 记录迁移日志,放服务器B节点
                    resoureceChangeOnly(from,1,to,1,virtualUsed,migrated);
                    toDel[h]=true;
                    curNow++;
                }else if (to.cpusizel >= virtualUsed.cpusize && to.memsizel >= virtualUsed.memsize) {
                    // 记录迁移日志,放服务器A节点
                    resoureceChangeOnly(from,1,to,0,virtualUsed,migrated);
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
                    resoureceChangeOnly(from,0,to,0,virtualUsed,migrated);
                    toDel[h]=true;
                    curNow++;
                } else if (to.cpusizer >= virtualUsed.cpusize && to.memsizer >= virtualUsed.memsize) {
                    // 记录迁移日志,放服务器B节点
                    resoureceChangeOnly(from,0,to,1,virtualUsed,migrated);
                    toDel[h]=true;
                    curNow++;
                }
            }else{
                if (to.cpusizer >= virtualUsed.cpusize && to.memsizer >= virtualUsed.memsize) {
                    // 记录迁移日志,放服务器B节点
                    resoureceChangeOnly(from,0,to,1,virtualUsed,migrated);
                    toDel[h]=true;
                    curNow++;
                }else if (to.cpusizel >= virtualUsed.cpusize && to.memsizel >= virtualUsed.memsize) {
                    // 记录迁移日志,放服务器A节点
                    resoureceChangeOnly(from,0,to,0,virtualUsed,migrated);
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
        // TODO  从愿列表中删去虚拟机
        toDel(from,toDel);
        return curNow;
    }

    /**
     * 4。分配策略：判断在已有的运行的服务器列表中是否可以放下,如果可以放下就直接完成部署
     * @param curVirtual 当前要操作新增虚拟机
     * @return
     */
    public static boolean addMatch(Virtual curVirtual,Request request) {
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
                        if ((topServerBought.cpusizel + topServerBought.cpusizer - curVirtual.cpu) < balance1 &&
                                (topServerBought.memsizel + topServerBought.memsizer - curVirtual.memory) > balance2  ||
                                (topServerBought.memsizel + topServerBought.memsizer - curVirtual.memory) < balance1 &&
                                        (topServerBought.cpusizel + topServerBought.cpusizer - curVirtual.cpu) > balance2) {
                            continue;
                        }
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
    public static void expansion_new(Virtual curVirtual, Request request, List<Server> serverListSelected) {
        boolean flag = false;
        if(curVirtual.isBi==1){
            // *** 堆顶找不到，然后找下一个，所以需要循环，直接取堆顶不行
            for (Server server : serverListSelected) {
                if ((server.cpu*1.0 >= curVirtual.cpu*expansion_para_cpu) && (server.memory*1.0 >= curVirtual.memory*expansion_para_ram) &&
                        server.cpu >= curVirtual.cpu && server.memory >= curVirtual.memory) {
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
                    flag = true;
                    break;
                }
            }
            if(!flag){
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
                        flag = true;
                        break;
                    }
                }
            }
        }else{
            // *** 堆顶找不到，然后找下一个，所以需要循环，直接取堆顶不行
            for (Server server : serverListSelected) {
                if ((server.cpu/2.0*1.0 >= curVirtual.cpu*expansion_para_cpu) && (server.memory/2.0*1.0 >= curVirtual.memory*expansion_para_ram) &&
                        server.cpu / 2 >= curVirtual.cpu && server.memory / 2 >= curVirtual.memory) {
                    double random = Math.random();
                    // 新建一个服务器
                    ServerBought serverBought = new ServerBought(seqNumber, server);
                    seqNumber++;
                    // 添加虚拟机和服务器的映射关系(也就是完成部署)
                    VirtualUsed virtualUsed = new VirtualUsed(request.virtualId, curVirtual);
                    virtualUsed.serverBought = serverBought;
                    //单节点先统一放在右边
                    virtualUsed.node = random>0.5?1:0;
                    curVirtualUsedMap.put(request.virtualId, virtualUsed);
                    // 消耗服务器资源
                    consumeResource(serverBought, curVirtual, virtualUsed, random>0.5?1:0);
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
                    putInfo.add(new PushInfo(serverBought, random>0.5?1:0,request.rank));
                    flag = true;
                    break;
                }
            }
            if(!flag){
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
                        flag = true;
                        break;
                    }
                }
            }
        }
    }


    // 按梯度下降，前result结果0-0.1,0.1-0.2,0.2-0.3;总体剩余比例大的在前面，同样比例梯度，空闲空间大的在前面
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

    // 请求排序
    static Comparator<VirtualUsed> cmpvir = new Comparator<VirtualUsed>() {
        @Override
        public int compare(VirtualUsed o1, VirtualUsed o2) {
            if(o1.isBi!=o2.isBi){
                return o2.isBi - o1.isBi;
            }else{
                return (int)(o2.cpusize+o2.memsize)-(int)(o1.cpusize+o1.memsize);
            }
        }
    };

    public static void migrate5_new(int day) {
        // 迁移虚拟机
        List<ServerBought> migrateServerBoughtList = new ArrayList<>();
        // 将服务器加入List
        for(ServerBought tmp: curServerBoughtList){
            migrateServerBoughtList.add(tmp);
        }
        Set<String> migrated = new HashSet<>();
        // 计算最大迁移次数
        int leftMigNum = (int) Math.floor(curVirtualUsedMap.size() * 0.03);
        int left = 0, right = migrateServerBoughtList.size() - 1;
        // 不断迁移
        int cycle_num = 0;
        while (cycle_num < 10 && leftMigNum > 0) {
            Collections.sort(migrateServerBoughtList, cmpMigra);
            while (left < right && leftMigNum > 0) {
                // 选出空闲比例最大和最小的服务器
                ServerBought maxFreeServerBought = migrateServerBoughtList.get(left);
                if (maxFreeServerBought.cpusizel == maxFreeServerBought.cpu / 2 && maxFreeServerBought.cpusizer == maxFreeServerBought.cpu / 2) {
                    left++;
                    right = migrateServerBoughtList.size() - 1;
                    continue;
                }
                // 请求排序
                Collections.sort(maxFreeServerBought.virtualList, cmpvir);
                ServerBought minFreeServerBought = migrateServerBoughtList.get(right);
                int changNum = change(maxFreeServerBought, minFreeServerBought, leftMigNum, migrated);
                leftMigNum -= changNum;
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
            left++;
            right = migrateServerBoughtList.size() - 1;
            cycle_num++;
        }
        if (leftMigNum > 0) {
            // 按空间剩余从大到小排序，空间剩余大的往旁边迁移
            Collections.sort(migrateServerBoughtList, cmpMigra);
            left = 0;
            right = left + 1;
            // 不断迁移
            while(left + 1 < migrateServerBoughtList.size() && leftMigNum > 0){
                // 选出空闲比例最大和最小的服务器
                ServerBought maxFreeServerBought = migrateServerBoughtList.get(left);
                if (maxFreeServerBought.cpusizel == maxFreeServerBought.cpu / 2 && maxFreeServerBought.cpusizer == maxFreeServerBought.cpu / 2) {
                    left++;
                    right = left + 1;
                    continue;
                }
                // 请求排序
//                Collections.sort(maxFreeServerBought.virtualList, cmpvir);
                ServerBought minFreeServerBought = migrateServerBoughtList.get(right);
                int changNum = change(maxFreeServerBought, minFreeServerBought, leftMigNum,migrated);
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
                    right += 20;
                }
                if (right >= migrateServerBoughtList.size()) {
                    left++;
                    right = left + 1;
                }
            }
        }
//        if (leftMigNum > 0) {
//            System.out.println(day + " kkkk");
//        }
//        System.out.println("left + right " + left + " " + right + " " + migrateServerBoughtList.size());
    }

    /**
     * 复赛迁移
     * @param day
     * @return
     */
    public static void migrate(int day) {
        // 计算最大迁移次数
        int leftMigNum = (int) Math.floor(curVirtualUsedMap.size() * 0.03);
        Set<String> migrated = new HashSet<>();
        for (int i = 1; i <= 3; i++) {
            // 先迁移只有2个虚拟机的服务器
            List<ServerBought> migrateServerBoughtList_only2 = new ArrayList<>();
            List<ServerBought> migrateServerBoughtList_else = new ArrayList<>();
            int num = i;
            // 迁移虚拟机
//            List<ServerBought> migrateServerBoughtList = new ArrayList<>();
            // 将服务器加入List
            for(ServerBought tmp: curServerBoughtList){
//                migrateServerBoughtList.add(tmp);
                if (tmp.virtualList.size() == num) {
                    migrateServerBoughtList_only2.add(tmp);
                } else {
                    migrateServerBoughtList_else.add(tmp);
                }
            }
            if (leftMigNum > 0) {
                // 对于migrateServerBoughtList_only2的每一台服务器
                migrate_fun1(leftMigNum, migrateServerBoughtList_only2, migrateServerBoughtList_else, migrated);
            }
//        migrate_fun2(leftMigNum);
        }

    }
    /**
     * 传入具体的剩余迁移次数和迁移服务器列表；每成功调用一次，用掉一次迁移机会
     * @param leftNum
     * @param migrateServerBoughtList_only2
     * @param migrateServerBoughtList_else
     */
    public static void migrate_fun1(int leftNum, List<ServerBought> migrateServerBoughtList_only2, List<ServerBought> migrateServerBoughtList_else, Set<String> migrated) {
        for (ServerBought tempOnlyServerBought : migrateServerBoughtList_only2) {
            if (leftNum <= 0) {
                return;
            }
            // 对于其中的每一个虚拟机，我们都从migrateServerBoughtList_else找到一个权重最小的可以容纳服务器然后放入
            for (VirtualUsed nowSelectedVirtualUsed : tempOnlyServerBought.virtualList) {
                // 当前要操作的虚拟机，迁移到migrateServerBoughtList_else中的toServerBought服务器上
                ServerBought toServerBought = null;
                int changeBi = -1;
                for (ServerBought tmpElseServerBought : migrateServerBoughtList_else) {
                    long cmpValue = Integer.MAX_VALUE;
                    // 如果是双节点
                    if (nowSelectedVirtualUsed.isBi == 1) {
                        // 必须是装得下的
                        if (tmpElseServerBought.memsizel >= nowSelectedVirtualUsed.memsize/2 && tmpElseServerBought.memsizer >= nowSelectedVirtualUsed.memsize/2
                                && tmpElseServerBought.cpusizel >= nowSelectedVirtualUsed.cpusize/2 && tmpElseServerBought.cpusizer >= nowSelectedVirtualUsed.cpusize/2) {
                            long tmp = tmpElseServerBought.cpusizel + tmpElseServerBought.memsizel;
                            if ((tmpElseServerBought.cpusizel ==  tmpElseServerBought.cpu/2 &&  tmpElseServerBought.memsizel ==  tmpElseServerBought.memory/2)) {
                                tmp = (long)(2 * (tmpElseServerBought.cpusizel + tmpElseServerBought.memsizel));
                            }
                            if ((tmpElseServerBought.cpusizer ==  tmpElseServerBought.cpu/2 &&  tmpElseServerBought.memsizer ==  tmpElseServerBought.memory/2)) {
                                tmp = (long)(2 * (tmpElseServerBought.cpusizer + tmpElseServerBought.memsizer));
                            }
                            if (tmp < cmpValue) {
                                cmpValue = tmp;
                                changeBi = 2;// 双节点
                                toServerBought = tmpElseServerBought;
                            }
                        }
                    } else {
                        // 如果是单节点
                        // 必须是装得下的左节点
                        if (tmpElseServerBought.cpusizel >= nowSelectedVirtualUsed.cpusize && tmpElseServerBought.memsizel >= nowSelectedVirtualUsed.memsize) {
                            long tmp = 0;
                            if ((tmpElseServerBought.cpusizel + tmpElseServerBought.memsizel - (nowSelectedVirtualUsed.cpusize + nowSelectedVirtualUsed.memsize)) < 30
                                    && (tmpElseServerBought.cpusizel + tmpElseServerBought.memsizel - (nowSelectedVirtualUsed.cpusize + nowSelectedVirtualUsed.memsize)) > 60) {
                                tmp = tmpElseServerBought.cpusizel + tmpElseServerBought.memsizel;
                            } else {
                                tmp = 2 * (tmpElseServerBought.cpusizel + tmpElseServerBought.memsizel);
                            }
                            if ((tmpElseServerBought.cpusizel ==  tmpElseServerBought.cpu/2 &&  tmpElseServerBought.memsizel ==  tmpElseServerBought.memory/2)) {
                                tmp = (long)(2 * (tmpElseServerBought.cpusizel + tmpElseServerBought.memsizel));
                            }
                            if (tmp < cmpValue) {
                                cmpValue = tmp;
                                changeBi = 0;// 左节点
                                toServerBought = tmpElseServerBought;
                            }
                        }
                        // 必须是装得下的右节点
                        if (tmpElseServerBought.cpusizer >= nowSelectedVirtualUsed.cpusize && tmpElseServerBought.memsizer >= nowSelectedVirtualUsed.memsize) {
                            long tmp = 0;
                            if ((tmpElseServerBought.cpusizer + tmpElseServerBought.memsizer - (nowSelectedVirtualUsed.cpusize + nowSelectedVirtualUsed.memsize)) < 30
                                    && (tmpElseServerBought.cpusizer + tmpElseServerBought.memsizer - (nowSelectedVirtualUsed.cpusize + nowSelectedVirtualUsed.memsize)) > 60) {
                                tmp = tmpElseServerBought.cpusizer + tmpElseServerBought.memsizer;
                            } else {
                                tmp = 2 * (tmpElseServerBought.cpusizer + tmpElseServerBought.memsizer);
                            }
                            if ((tmpElseServerBought.cpusizer ==  tmpElseServerBought.cpu/2 &&  tmpElseServerBought.memsizer ==  tmpElseServerBought.memory/2)) {
                                tmp = (long)(2 * (tmpElseServerBought.cpusizer + tmpElseServerBought.memsizer));
                            }
                            if (tmp < cmpValue) {
                                cmpValue = tmp;
                                changeBi = 1;// 右节点
                                toServerBought = tmpElseServerBought;
                            }
                        }
                    }
                }
                // 表示迁移成功
                if (-1 != changeBi) {
                    // 如果迁移次数没了
                    if (leftNum <= 0) {
                        return;
                    }
                    // HashSet没用
                    // 双节点
                    if (changeBi == 2) {
                        resoureceChangeBi(tempOnlyServerBought, toServerBought, nowSelectedVirtualUsed, migrated);
                    } else {
                        resoureceChangeOnly(tempOnlyServerBought, nowSelectedVirtualUsed.isBi, toServerBought, changeBi, nowSelectedVirtualUsed, migrated);
                    }
                    leftNum -= 1;
                    migrateNum++;
                }
            }
        }
    }

    /**
     * 把空闲率高的往空闲率低的执行。
     * @param leftMigNum
     */
    public static void migrate_fun2(int leftMigNum) {

    }

    public static boolean fromIsEmpty(ServerBought from) {
        return from.virtualList.size() == 0;
    }
    /**
     * 将from服务器上面的虚拟机放到to服务器上面
     * @param from 空闲率大的服务器
     * @param to 空闲率小的服务器，也就是负载高的
     * @param leftMigNum 当前剩余的迁移次数
     * @param migrated 存储当天已迁移的虚拟机
     * @return
     */
    public static int change(ServerBought from, ServerBought to, int leftMigNum , Set<String> migrated) {
        // 判断够不够
        if ((to.cpusizel == 0 || to.cpusizer == 0) || (to.memsizel == 0 || to.memsizer == 0)) {
            return 0;
        }
        boolean[] toDel = new boolean[from.virtualList.size()];
        int curNow = 0;
        ArrayList<VirtualUsed> fromList = from.virtualList;
        // 空闲比例最小的服务器的容量
        // TODO 优化 迁移策略
        // 先吧fromList（默认顺序）里面的放到to(服务器)里面
        for(int h =0;h<fromList.size();h++){
            VirtualUsed virtualUsed = fromList.get(h);
//            if(migrated.contains(virtualUsed.no)){
//                continue;
//            }
            //如果部署在原服务器的双节点上
            if (virtualUsed.node == 2) {
                if (Math.min(to.cpusizel, to.cpusizer) >= virtualUsed.cpusize/2 &&
                        Math.min(to.memsizel, to.memsizer) >= virtualUsed.memsize/2) {
                    resoureceChangeBi(from,to,virtualUsed,migrated);
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
                        resoureceChangeOnly(from,1,to,0,virtualUsed,migrated);
                        toDel[h]=true;
                        curNow++;
                    } else if (to.cpusizer >= virtualUsed.cpusize && to.memsizer >= virtualUsed.memsize) {
                        // 记录迁移日志,放服务器B节点
                        resoureceChangeOnly(from,1,to,1,virtualUsed,migrated);
                        toDel[h]=true;
                        curNow++;
                    }
                }else{
                    if (to.cpusizer >= virtualUsed.cpusize && to.memsizer >= virtualUsed.memsize) {
                        // 记录迁移日志,放服务器B节点
                        resoureceChangeOnly(from,1,to,1,virtualUsed,migrated);
                        toDel[h]=true;
                        curNow++;
                    }else if (to.cpusizel >= virtualUsed.cpusize && to.memsizel >= virtualUsed.memsize) {
                        // 记录迁移日志,放服务器A节点
                        resoureceChangeOnly(from,1,to,0,virtualUsed,migrated);
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
                        resoureceChangeOnly(from,0,to,0,virtualUsed,migrated);
                        toDel[h]=true;
                        curNow++;
                    } else if (to.cpusizer >= virtualUsed.cpusize && to.memsizer >= virtualUsed.memsize) {
                        // 记录迁移日志,放服务器B节点
                        resoureceChangeOnly(from,0,to,1,virtualUsed,migrated);
                        toDel[h]=true;
                        curNow++;
                    }
                }else{
                    if (to.cpusizer >= virtualUsed.cpusize && to.memsizer >= virtualUsed.memsize) {
                        // 记录迁移日志,放服务器B节点
                        resoureceChangeOnly(from,0,to,1,virtualUsed,migrated);
                        toDel[h]=true;
                        curNow++;
                    }else if (to.cpusizel >= virtualUsed.cpusize && to.memsizel >= virtualUsed.memsize) {
                        // 记录迁移日志,放服务器A节点
                        resoureceChangeOnly(from,0,to,0,virtualUsed,migrated);
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
     * 最后的具体的删除操作
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
    public static void resoureceChangeBi(ServerBought from,ServerBought to,VirtualUsed virtual, Set<String> migrated){
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
        migrated.add(virtual.no);
    }

    /**
     * 单节点虚拟机迁移资源修正
     */
    public static void resoureceChangeOnly(ServerBought from,int fromNode,ServerBought to,int toNode,VirtualUsed virtualUsed, Set<String> migrated){
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
        migrated.add(virtualUsed.no);
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

    public static void calculate2(){
        for(ServerBought serverBought:curServerBoughtList1){
            if(serverBought.cpusizel!=serverBought.cpu/2||serverBought.cpusizer!=serverBought.cpu/2
                    ||serverBought.memsizel!=serverBought.memory/2||serverBought.memsizer!=serverBought.memory/2)
                powerFee+=serverBought.dailycost;
        }
        for(ServerBought serverBought:curServerBoughtList2){
            if(serverBought.cpusizel!=serverBought.cpu/2||serverBought.cpusizer!=serverBought.cpu/2
                    ||serverBought.memsizel!=serverBought.memory/2||serverBought.memsizer!=serverBought.memory/2)
                powerFee+=serverBought.dailycost;
        }
        for(ServerBought serverBought:curServerBoughtList3){
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
        System.out.println("----111 ");
        for (Server server : serverList1) {
            System.out.println(server.toString());
        }
        System.out.println("----222 ");
        for (Server server : serverList2) {
            System.out.println(server.toString());
        }
        System.out.println("----333 ");
        for (Server server : serverList3) {
            System.out.println(server.toString());
        }
        System.out.println("hello orginal");
        for (Server server : serverList) {
            System.out.println(server.toString());
        }
    }


    /**
     * 根据虚拟机规格选相应的服务器规格列表
     */
    public static List<ServerBought> chooseServer(VirtualUsed virtualUsed) {
        double ratio = virtualUsed.cpusize * 1.0 / virtualUsed.memsize;
        if (ratio > ratioVirtual1) {
            return curServerBoughtList1;
        } else if (ratio < ratioVirtual2) {
            return curServerBoughtList2;
        } else {
            return curServerBoughtList3;
        }
    }

    public static List<ServerBought> chooseServer(Virtual virtual) {
        double ratio = virtual.cpu * 1.0 / virtual.memory;
        if (ratio > ratioVirtual1) {
            return curServerBoughtList1;
        } else if (ratio < ratioVirtual2) {
            return curServerBoughtList2;
        } else {
            return curServerBoughtList3;
        }
    }

    /**
     * 输出结果
     */
    public static void write() {
        for(String log:logInfo){
            System.out.println(log);
        }
        // 每日的输出都要清空
        logInfo.clear();
        // 清空
        System.out.flush();
    }

    /**
     * 输出总费用
     */
    public static void write2(){
        serverFee += powerFee;
        System.out.println(serverFee);
        System.out.println("已购买的服务器大小 "  + curServerBoughtList.size());
        System.out.println("已购买的服务器1大小 "  + curServerBoughtList1.size());
        System.out.println("已购买的服务器2大小 "  + curServerBoughtList2.size());
        System.out.println("已购买的服务器3大小 "  + curServerBoughtList3.size());
        System.out.println("迁移次数 " + migrateNum);
    }


    /**
     * 测试服务器上的虚拟机的数量
     */
    public static void test_num() {
        int numAll = 0;
        int numAll2 = 0;
        int num_max = 0;
        for (ServerBought serverBought : curServerBoughtList) {
            numAll += serverBought.virtualList.size();
            if (serverBought.virtualList.size() == 4) {
                numAll2++;
            }
            num_max = Math.max(num_max, serverBought.virtualList.size());
        }
        System.out.println(numAll2 + " " + numAll + " " + num_max);
    }

}

