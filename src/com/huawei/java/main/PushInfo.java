package com.huawei.java.main;

public class PushInfo {
    public ServerBought serverBought;
    public int pushNode;
    public int rank;

    public PushInfo(ServerBought serverBought,int node,int rank){
        this.serverBought=serverBought;
        this.pushNode=node;
        this.rank=rank;
    }

    public ServerBought getServerBought() {
        return serverBought;
    }

    public void setServerBought(ServerBought serverBought) {
        this.serverBought = serverBought;
    }

    public int getPushNode() {
        return pushNode;
    }

    public void setPushNode(int pushNode) {
        this.pushNode = pushNode;
    }
}
