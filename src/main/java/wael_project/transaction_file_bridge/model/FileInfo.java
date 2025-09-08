package wael_project.transaction_file_bridge.model;

import java.util.Date;

public class FileInfo {
    private String name;
    private long size;
    private Date lastModified;
    private boolean exists;

    public FileInfo(String name, long size, Date lastModified, boolean exists) {
        this.name = name;
        this.size = size;
        this.lastModified = lastModified;
        this.exists = exists;
    }

    // Getters
    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public boolean isExists() {
        return exists;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setSize(long size) {
        this.size = size;
    }
}