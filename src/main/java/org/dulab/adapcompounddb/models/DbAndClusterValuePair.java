package org.dulab.adapcompounddb.models;

public class DbAndClusterValuePair {

    private final int dbValue;
    private final int clusterValue;

    public DbAndClusterValuePair(int dbValue, int clusterValue) {
        this.dbValue = dbValue;
        this.clusterValue = clusterValue;
    }

    public int getDbValue() {
        return dbValue;
    }

    public int getClusterValue() {
        return clusterValue;
    }
}
