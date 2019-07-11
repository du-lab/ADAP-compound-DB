package org.dulab.adapcompounddb.models;

public class DbAndClusterValuePair {

    private int dbValue;
    private int clusterValue;

    public DbAndClusterValuePair() {
        this(0, 0);
    }

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

    public void setDbValue(int dbValue) {
        this.dbValue = dbValue;
    }

    public void setClusterValue(int clusterValue) {
        this.clusterValue = clusterValue;
    }

    @Override
    public String toString() {
        return String.format("{\"dbValue\": %d, \"clusterValue\": %d}", dbValue, clusterValue);
    }
}
