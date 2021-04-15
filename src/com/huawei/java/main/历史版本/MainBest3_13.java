//package com.huawei.java.main.历史版本;
//
//import com.huawei.java.main.*;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.InputStreamReader;
//import java.util.*;
//
//public class MainBest3_13 {
//    // 服务器信息映射
//    private static Map<String, Server> ServerMaps = new HashMap<>();
//    // 虚拟机信息映射
//    private static Map<String, Virtual> VirtualMaps = new HashMap<>();
//    //    static Comparator cmp = new Comparator<Server>() {
////        @Override
////        public int compare(Server o1, Server o2) {
////            return (int)(1000*(o2.cpu*0.75 + o2.memory*0.23 + o2.oricost*0.02) - 1000*(o1.cpu*0.75 + o1.memory*0.23 + o1.oricost*0.02));
////        }
////
////    };
////    static Comparator<Server> cmp = Comparator.comparingDouble(s -> s.oricost*1.0/(s.cpu + s.memory));
//
//    static Comparator<Server> cmp = Comparator.comparingDouble(s -> s.oricost);
//
//    /**
//     * 策略2：数据结构优化部分
//     * 优先队列信息
//     * // 为了速度，属性先改成public
//     */
////    private static PriorityQueue<Server> serverList = new PriorityQueue<>(Comparator.comparingDouble(s -> s.oricost*1.0/(s.cpu + s.memory))); // 优先队列，服务器的价格进行一个排序
//    private static PriorityQueue<Server> serverListCpuCost = new PriorityQueue<>(Comparator.comparingDouble(s -> s.oricost*1.0/s.cpu)); // 优先队列，服务器CPU均价进行一个排序
//    private static PriorityQueue<Server> serverListMemoryCost = new PriorityQueue<>(Comparator.comparingDouble(s -> s.oricost*1.0/s.memory)); // 优先队列，服务器Memory均价进行一个排序
//    private static PriorityQueue<Server> serverList = new PriorityQueue<>(cmp); // 优先队列，服务器的价格进行一个排序
//    //    Comparator.comparingDouble(s -> s.oricost*1.0/(s.cpu + s.memory))
//
////    private static PriorityQueue<Server> serverListmem1 = new PriorityQueue<>(cmp); // 优先队列，服务器的价格进行一个排序
////    private static PriorityQueue<Server> serverListmem15 = new PriorityQueue<>(cmp); // 优先队列，服务器的价格进行一个排序
////    private static PriorityQueue<Server> serverListmem2 = new PriorityQueue<>(cmp); // 优先队列，服务器的价格进行一个排序
////    private static PriorityQueue<Server> serverListmem25 = new PriorityQueue<>(cmp); // 优先队列，服务器的价格进行一个排序
////    private static PriorityQueue<Server> serverListmem3 = new PriorityQueue<>(cmp); // 优先队列，服务器的价格进行一个排序
////    private static PriorityQueue<Server> serverListcpu1 = new PriorityQueue<>(cmp); // 优先队列，服务器的价格进行一个排序
////    private static PriorityQueue<Server> serverListcpu15 = new PriorityQueue<>(cmp); // 优先队列，服务器的价格进行一个排序
////    private static PriorityQueue<Server> serverListcpu2 = new PriorityQueue<>(cmp); // 优先队列，服务器的价格进行一个排序
////    private static PriorityQueue<Server> serverListcpu25 = new PriorityQueue<>(cmp); // 优先队列，服务器的价格进行一个排序
////    private static PriorityQueue<Server> serverListcpu3 = new PriorityQueue<>(cmp); // 优先队列，服务器的价格进行一个排序
//
//    private static PriorityQueue<Server> serverList1 = new PriorityQueue<>(cmp); // 优先队列，服务器的价格进行一个排序
//    private static PriorityQueue<Server> serverList2 = new PriorityQueue<>(cmp); // 优先队列，服务器的价格进行一个排序
//    private static PriorityQueue<Server> serverList3 = new PriorityQueue<>(cmp); // 优先队列，服务器的价格进行一个排序
//
//
//    private static PriorityQueue<Virtual> virtualListCpuNeed = new PriorityQueue<>(Comparator.comparingLong(v -> v.cpu)); //优先队列，表示的是按虚拟机cpu的需求量排序
//    private static PriorityQueue<Virtual> virtualListMemoryNeed = new PriorityQueue<>(Comparator.comparingLong(v -> v.memory)); //优先队列，表示的是按虚拟机内存的需求量排序
//
//
//    // 所有的请求列表
//    private static List<List<String[]>> requestLists = new ArrayList<>();
//    // TODO 服务器列表1, 维护双节点资源较多的
//    private static PriorityQueue<ServerBought> curServerBoughtList = new PriorityQueue(new Comparator<ServerBought>() {
//        //        @Override
////        public int compare(ServerBought o1, ServerBought o2) {
////            return (int)(Math.min(o2.cpusizel,o2.cpusizer)+Math.min(o2.memsizel,o2.memsizer))-(int)(Math.min(o1.cpusizel,o1.cpusizer)+Math.min(o1.memsizel,o1.memsizer));
////        }
//        @Override
//        public int compare(ServerBought o1, ServerBought o2) {
//            return (int)(Math.min(o1.cpusizel,o1.cpusizer)+Math.min(o1.memsizel,o1.memsizer))-(int)(Math.min(o2.cpusizel,o2.cpusizer)+Math.min(o2.memsizel,o2.memsizer));
//        }
//    });
//    // TODO 服务器列表1, 维护单节点资源较多的
//    private static PriorityQueue<ServerBought> curServerBoughtListSingle = new PriorityQueue(new Comparator<ServerBought>() {
//        @Override
//        public int compare(ServerBought o1, ServerBought o2) {
//            int resource1L = (int)(o1.cpusizel+o1.memsizel);
//            int resource1R = (int)(o1.cpusizer+o1.memsizer);
//            int resource2L = (int)(o2.cpusizel+o2.memsizel);
//            int resource2R = (int)(o2.cpusizer+o2.memsizer);
//            if(resource1L+resource2L>resource1R+resource2R){
//                return resource2L-resource1L;
//            }else{
//                return resource2R-resource1R;
//            }
//        }
//    });
//    // 已存在的虚拟机的列表
//    private static Map<String, VirtualUsed> curVirtualUsedMap = new HashMap<>();
//    // 已有的购买的服务器列表数量
//    private static int seqNumber = 0;
//    // 文件路径
//    private static final String filePath = "src/com/huawei/resource/training-2.txt";
//    // 总费用
//    private static long resFeeAll = 0;
//    // 服务器购买成本
//    private static long serverFee = 0;
//    // 服务器运行成本
//    private static long powerFee = 0;
//    //保存所有的输出信息
//    private static List<String> logInfo = new ArrayList<>();
//    //保存添加服务器日志
//    private static Map<String,List<ServerBought>> addInfo = new HashMap<>();
//    //保存部署信息
//    private static List<PushInfo> putInfo = new ArrayList<>();
//
//
//    public static void main(String[] args) {
//        curVirtualUsedMap.clear();
//        // 读取
//        read();
//        // 处理
//        process();
//        // 输出
//        write();
//        //
//        write2();
//    }
//    /**
//     * 读取文件
//     */
//    public static void read() {
//        try  {
//            String encoding = "utf-8" ;
//            File file= new  File(filePath);
//            InputStreamReader read =  new  InputStreamReader(new FileInputStream(file),encoding); //考虑到编码格式
//            BufferedReader bufferedReader =  new  BufferedReader(read);
//            int serverNum = Integer.parseInt(bufferedReader.readLine());
//            //获取可用服务器列表
//            for(int i=0;i<serverNum;i++){
//                String t = bufferedReader.readLine();
//                String[] strs = t.substring(1,t.length()-1).split(", ");
//                ServerMaps.put(strs[0], new Server(strs));
//                Server server = new Server(strs);
//                serverList.add(server);
//
//                serverListCpuCost.add(new Server(strs));
//                serverListMemoryCost.add(new Server(strs));
//                int ratio = (int)(server.cpu*1.0/server.memory + 0.5);
//                if (ratio > 1) {
//                    serverList1.add(server);
//                } else if (ratio < 1){
//                    serverList2.add(server);
//                } else {
//                    serverList3.add(server);
//                }
//            }
//            int virtualNum = Integer.parseInt(bufferedReader.readLine());
//            //获取可用虚拟机列表
//            for (int i = 0; i < virtualNum; i++) {
//                String t = bufferedReader.readLine();
//                String[] strs = t.substring(1,t.length()-1).split(", ");
//                VirtualMaps.put(strs[0], new Virtual(strs));
//                virtualListCpuNeed.add(new Virtual(strs));
//                virtualListMemoryNeed.add(new Virtual(strs));
//            }
//            int requestDays = Integer.parseInt(bufferedReader.readLine());
//            //获取T天的用户请求序列
//            for (int i = 0; i < requestDays; i++) {
//                //每天的请求数
//                int requestNumEveryDay = Integer.valueOf(bufferedReader.readLine());
//                // TODO 下面可以区分单双节点
//                List<String[]> virs = new ArrayList<>();
//                for (int j = 0; j < requestNumEveryDay; j++) {
//                    String t = bufferedReader.readLine();
//                    String[] strs = t.substring(1, t.length() - 1).split(", ");
//                    virs.add(strs);
//                }
//                //  暂时先一次性读入存储再处理。
//                requestLists.add(virs);
//            }
//            read.close();
//        } catch  (Exception e) {
//            System.out.println( "读取文件内容出错" );
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 读取标准输入
//     */
//    public static void read2() {
//        Scanner sc = new Scanner(System.in);
//        int serverNum = Integer.parseInt(sc.nextLine());
//        //获取可用服务器列表
//        for(int i=0;i<serverNum;i++){
//            String t = sc.nextLine();
//            String[] strs = t.substring(1,t.length()-1).split(", ");
//            ServerMaps.put(strs[0], new Server(strs));
//            Server server = new Server(strs);
//            serverList.add(server);
//            serverListCpuCost.add(new Server(strs));
//            serverListMemoryCost.add(new Server(strs));
//            int ratio = (int)(server.cpu*1.0/server.memory + 0.5);
//            if (ratio > 1) {
//                serverList1.add(server);
//            } else if (ratio < 1){
//                serverList2.add(server);
//            } else {
//                serverList3.add(server);
//            }
//        }
//        int virtualNum = Integer.parseInt(sc.nextLine());
//        //获取可用虚拟机列表
//        for (int i = 0; i < virtualNum; i++) {
//            String t = sc.nextLine();
//            String[] strs = t.substring(1,t.length()-1).split(", ");
//            VirtualMaps.put(strs[0], new Virtual(strs));
//            virtualListCpuNeed.add(new Virtual(strs));
//            virtualListMemoryNeed.add(new Virtual(strs));
//        }
//        int requestDays = Integer.parseInt(sc.nextLine());
//        //获取T天的用户请求序列
//        for (int i = 0; i < requestDays; i++) {
//            //每天的请求数
//            int requestNumEveryDay = Integer.valueOf(sc.nextLine());
//            // TODO 下面可以区分单双节点
//            List<String[]> virs = new ArrayList<>();
//            for (int j = 0; j < requestNumEveryDay; j++) {
//                String t = sc.nextLine();
//                String[] strs = t.substring(1, t.length() - 1).split(", ");
//                virs.add(strs);
//            }
//            //  暂时先一次性读入存储再处理。
//            requestLists.add(virs);
//        }
//    }
//
//
//    /**
//     * 策略1：分配策略
//     * 程序主处理入口:
//     */
//    public static void process() {
//        // 处理每一天的请求
//        for (List<String[]> list : requestLists) {
//            int serNumUntilNow= seqNumber;
//            // 扩容策略
////            expansion();
//            // 迁移策略
////            migrate();
//            // 分配策略：add和del
//            //每一条请求
//            for (String[] strs : list) {
//                if ("add".equals(strs[0])) {
//                    // 获取当前Virtual的规格
//                    Virtual curVirtual = VirtualMaps.get(strs[1]);
//                    PriorityQueue<Server> serverListSelected = switchServer(curVirtual);
//                    // 1. 先判断能不能放,如果可以放的话在addMatch的时候已经完成部署
//                    boolean canStore = addMatch(curVirtual, strs[2]);
//                    // 2. 如果不够放下就新采购一个服务器，然后把虚拟机部署到这台服务器上面
//                    if (!canStore) {
//                        if(curVirtual.isBi==1){
//                            // TODO 策略5 按CPU优先级来选（可选内存优先级，其他）
//                            for (Server server : serverListSelected) {
//                                if (server.cpu >= curVirtual.cpu && server.memory >= curVirtual.memory) {
//                                    // 新建一个服务器
//                                    ServerBought serverBought = new ServerBought(seqNumber, server);
//                                    seqNumber++;
//                                    // 添加虚拟机和服务器的映射关系(也就是完成部署)
//                                    VirtualUsed virtualUsed = new VirtualUsed(strs[2],curVirtual);
//                                    virtualUsed.server = serverBought;
//                                    virtualUsed.node = 2;
//                                    curVirtualUsedMap.put(strs[2],virtualUsed);
//                                    // TODO 消耗服务器资源
//                                    consumeResource(serverBought,curVirtual,2);
//                                    // 已购买列表添加一个服务器
//                                    curServerBoughtList.add(serverBought);
////                                    curServerBoughtListSingle.add(serverBought);
//                                    // 计算成本1：购买成本
//                                    serverFee += server.oricost;
//                                    //添加每日的购买信息
//                                    if(addInfo.get(serverBought.name)==null){
//                                        addInfo.put(serverBought.name,new ArrayList<ServerBought>());
//                                    }
//                                    addInfo.get(serverBought.name).add(serverBought);
//                                    // TODO 输出部署信息
//                                    putInfo.add(new PushInfo(serverBought,2));
//                                    break;
//                                }
//                            }
//                        }else{
//                            // TODO 策略5 按优先级来选
//                            for (Server server : serverListSelected) {
//                                if (server.cpu/2 >= curVirtual.cpu && server.memory/2 >= curVirtual.memory) {
//                                    // 新建一个服务器
//                                    ServerBought serverBought = new ServerBought(seqNumber, server);
//                                    seqNumber++;
//                                    // 添加虚拟机和服务器的映射关系(也就是完成部署)
//                                    VirtualUsed virtualUsed = new VirtualUsed(strs[2],curVirtual);
//                                    virtualUsed.server = serverBought;
//                                    //单节点先统一放在右边
//                                    virtualUsed.node = 1;
//                                    curVirtualUsedMap.put(strs[2],virtualUsed);
//                                    // TODO 消耗服务器资源
//                                    consumeResource(serverBought,curVirtual,1);
//                                    //已购买列表添加一个服务器
//                                    curServerBoughtList.add(serverBought);
////                                    curServerBoughtListSingle.add(serverBought);
//                                    // 计算成本1：购买成本
//                                    serverFee += server.oricost;
//                                    //添加每日的购买信息
//                                    if(addInfo.get(serverBought.name)==null){
//                                        addInfo.put(serverBought.name,new ArrayList<ServerBought>());
//                                    }
//                                    addInfo.get(serverBought.name).add(serverBought);
//                                    // TODO 输出部署信息
//                                    putInfo.add(new PushInfo(serverBought,1));
//                                    break;
//                                }
//                            }
//                        }
//                    }
//                } else {
//                    // 删除该虚拟机
//                    delVirtual(strs[1]);
//                }
//            }
//
//            //把每天的操作信息都放入到日志中，最后统一输出
//            writeToLog(serNumUntilNow);
//
//            //计算每天的费用
////            calculate();
//        }
//    }
//
//    /**
//     * 分配策略：判断在已有的运行的服务器列表中是否可以放下,如果可以放下就直接完成部署
//     * @param curVirtual 当前要操作新增虚拟机
//     * @return
//     */
//    public static boolean addMatch(Virtual curVirtual,String virtualno) {
////        if(curServerBoughtList.isEmpty()||curServerBoughtListSingle.isEmpty()){
////            return false;
////        }
//        if(curServerBoughtList.isEmpty()){
//            return false;
//        }
//        boolean flag = false;
//
//        // 获取当前存量服务器具的堆顶
//        // TODO 可以遍历一下，用时间换成本
//        try {
//            if(curVirtual.isBi==1){
//                for(ServerBought topServerBought:curServerBoughtList){
//                    if(topServerBought.cpusizel>=curVirtual.cpu/2&&topServerBought.cpusizer>=curVirtual.cpu/2
//                            &&topServerBought.memsizel>=curVirtual.memory/2&&topServerBought.memsizer>=curVirtual.memory/2){
//                        // 添加虚拟机和服务器的映射关系(也就是完成部署)
//                        VirtualUsed virtualUsed = new VirtualUsed(virtualno,curVirtual);
//                        virtualUsed.server = topServerBought;
//                        virtualUsed.node = 2;
//                        curVirtualUsedMap.put(virtualno,virtualUsed);
//                        // TODO 消耗服务器资源
//                        consumeResource(topServerBought,curVirtual,2);
//                        // TODO 输出部署信息
//                        putInfo.add(new PushInfo(topServerBought,2));
//
//                        //资源重组
//                        reSort(topServerBought);
//
//                        flag = true;
//                        break;
//                    }
//                }
//            }else{
//                for(ServerBought topServerBought:curServerBoughtList){
//                    if((topServerBought.cpusizel>=curVirtual.cpu&&topServerBought.memsizel>=curVirtual.memory)){
//                        // 添加虚拟机和服务器的映射关系(也就是完成部署)
//                        VirtualUsed virtualUsed = new VirtualUsed(virtualno,curVirtual);
//                        virtualUsed.server = topServerBought;
//                        //单节点放在左边
//                        virtualUsed.node = 0;
//                        curVirtualUsedMap.put(virtualno,virtualUsed);
//                        // TODO 消耗服务器资源
//                        consumeResource(topServerBought,curVirtual,0);
//                        // TODO 输出部署信息
//                        putInfo.add(new PushInfo(topServerBought,0));
//
//                        //资源重组
//                        reSort(topServerBought);
//
//                        flag = true;
//                        break;
//                    }else if((topServerBought.cpusizer>=curVirtual.cpu&&topServerBought.memsizer>=curVirtual.memory)){
//                        VirtualUsed virtualUsed = new VirtualUsed(virtualno,curVirtual);
//                        virtualUsed.server = topServerBought;
//                        //单节点放在右边
//                        virtualUsed.node = 1;
//                        curVirtualUsedMap.put(virtualno,virtualUsed);
//                        // TODO 消耗服务器资源
//                        consumeResource(topServerBought,curVirtual,1);
//                        // TODO 输出部署信息
//                        putInfo.add(new PushInfo(topServerBought,1));
//
//                        //资源重组
//                        reSort(topServerBought);
//
//                        flag = true;
//                        break;
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return flag;
//    }
//
//    /**
//     * 从服务器上删除虚拟机
//     */
//    public static void delVirtual(String virtualNo){
//        VirtualUsed virtualUsed = curVirtualUsedMap.get(virtualNo);
//        ServerBought serverBought = virtualUsed.server;
//        if(virtualUsed.node==2){
//            serverBought.cpusizel+= virtualUsed.cpusize/2;
//            serverBought.cpusizer+= virtualUsed.cpusize/2;
//            serverBought.memsizel+= virtualUsed.memsize/2;
//            serverBought.memsizer+= virtualUsed.memsize/2;
//        }else if(virtualUsed.node==1){
//            serverBought.cpusizer+= virtualUsed.cpusize;
//            serverBought.memsizer+= virtualUsed.memsize;
//        }else {
//            serverBought.cpusizel+=virtualUsed.cpusize;
//            serverBought.memsizel+=virtualUsed.memsize;
//        }
//        curVirtualUsedMap.remove(virtualNo);
//        reSort(serverBought);
//    }
//
//    /**
//     * 选择合适的服务器列表进行遍历
//     */
//    public static PriorityQueue<Server> switchServer(Virtual curVirtual){
//        int ratio = (int)(curVirtual.cpu*1.0/curVirtual.memory + 0.5);
//        if (ratio > 1) {
//            return serverList1;
//        } else if (ratio < 1){
//            return serverList2;
//        } else {
//            return serverList3;
//        }
//    }
//
//
//    /**
//     * 消耗服务器资源
//     * serverBought表示被消耗的服务器实例，virtual表示要部署的虚拟的规格，node表示怎么消耗(0表示部署在左节点，1表示在右节点，2表示双节点部署)
//     */
//    public static void consumeResource(ServerBought serverBought,Virtual virtual,int node) {
//        if(node==2){
//            serverBought.cpusizel-=virtual.cpu/2;
//            serverBought.cpusizer-=virtual.cpu/2;
//            serverBought.memsizel-=virtual.memory/2;
//            serverBought.memsizer-=virtual.memory/2;
//        }else if(node==1){
//            serverBought.cpusizer-=virtual.cpu;
//            serverBought.memsizer-=virtual.memory;
//        }else{
//            serverBought.cpusizel-=virtual.cpu;
//            serverBought.memsizel-=virtual.memory;
//        }
//    }
//
//    /**
//     * 资源重组(对服务器列表中的服务器进行重新排序)
//     */
//    public static void reSort(ServerBought serverBought){
//        curServerBoughtList.remove(serverBought);
//        curServerBoughtListSingle.remove(serverBought);
//        curServerBoughtList.add(serverBought);
//        curServerBoughtListSingle.add(serverBought);
//    }
//
//    /**
//     * 策略3：扩容策略
//     * 该怎么购买
//     */
//    public static void expansion() {
//        // 购买服务器
//    }
//
//    /**
//     * 策略4：迁移策略
//     * 该怎么购买
//     */
//    public static void migrate() {
//        // 迁移虚拟机
//    }
//
//    /**
//     * 输出结果
//     */
//    public static void write() {
//        for(String log:logInfo){
//            System.out.println(log);
//        }
//    }
//
//    /**
//     * 输出总费用
//     */
//    public static void write2(){
//        System.out.println(serverFee);
//        System.out.println(seqNumber);
//        System.out.println(curServerBoughtList.size());
//    }
//
//    /**
//     * 计算每天的费用
//     */
//    public static void calculate(){
//        for(ServerBought serverBought:curServerBoughtList){
//            if(serverBought.cpusizel!=serverBought.cpu/2||serverBought.cpusizer!=serverBought.cpu/2
//                    ||serverBought.memsizel!=serverBought.memory/2||serverBought.memsizer!=serverBought.memory/2)
//                powerFee+=serverBought.dailycost;
//        }
//    }
//
//    /**
//     * 将每日信息都存入到日志汇总
//     */
//    public static void writeToLog(int serNumUntilNow) {
//        //输出购买种类
//        logInfo.add("(purchase, "+addInfo.size()+")");
//        //对每个种类输出信息
//        for(String servernm:addInfo.keySet()){
//            List<ServerBought> tmp = addInfo.get(servernm);
//            for(ServerBought serverBought:tmp){
//                serverBought.no=serNumUntilNow++;
//            }
//            logInfo.add("("+servernm+", "+tmp.size()+")");
//        }
//        logInfo.add("(migration, 0)");
//        for(int i=0;i<putInfo.size();i++){
//            int serverno = putInfo.get(i).serverBought.no;
//            if(putInfo.get(i).pushNode==2){
//                logInfo.add("("+serverno+")");
//            }else if(putInfo.get(i).pushNode==1){
//                logInfo.add("("+serverno+", B)");
//            }else{
//                logInfo.add("("+serverno+", A)");
//            }
//        }
//        //清空每日信息
//        addInfo.clear();
//        putInfo.clear();
//    }
//
//}
//
