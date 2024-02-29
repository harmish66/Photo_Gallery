package com.example.foldergallery.utils;

public class videoFolder {
    private  String path;
    private  String FolderName;
    private int numberOfvids = 0;
    private String firstvid;

    public videoFolder(){

    }
    public videoFolder(String path, String folderName) {
        this.path = path;
        FolderName = folderName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFolderName() {
        return FolderName;
    }

    public void setFolderName(String folderName) {
        FolderName = folderName;
    }

    public int getNumberOfvids() {
        return numberOfvids;
    }

    public void setNumberOfvids(int numberOfvids) {
        this.numberOfvids = numberOfvids;
    }
    public void addvid(){
        this.numberOfvids++;
    }

    public String getFirstPic() {
        return firstvid;
    }

    public void setFirstvid(String firstvid) {
        this.firstvid = firstvid;
    }
}
