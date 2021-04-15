package com.huawei.java.main;

public class MigrationInfo {
    public String vitualId;
    public ServerBought toServer;
    public int tonode;

    public MigrationInfo(String vitualId, ServerBought toServer, int tonode) {
        this.vitualId = vitualId;
        this.toServer = toServer;
        this.tonode = tonode;
    }
}
