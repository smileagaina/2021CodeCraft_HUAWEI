package com.huawei.java.main;

public class Request {
    public String operation;
    public String virtualId;
    public String virtualName;
    public int rank;

    public Request(String[] strings,int j){
        if(strings[0].equals("add")){
            this.operation=strings[0];
            this.virtualId=strings[2];
            this.virtualName=strings[1];
            this.rank=j;
        }else{
            this.operation=strings[0];
            this.virtualId=strings[1];
            this.rank=j;
        }
    }
}
