package com.twlkyao.kuaipan;

import cn.kuaipan.android.openapi.KuaipanAPI;

public class RequestBase {
    private KuaipanAPI api; // The api of the Kuaipan.
    private String filePath; // The path of the file to upload or download.
    private String remotePath; // The remote path of the Kuaipan.

    public RequestBase() {

    }

    public RequestBase(KuaipanAPI api, String filePath, String remotePath) {
        this.api = api;
        this.filePath = filePath;
        this.remotePath = remotePath;
    }

    public KuaipanAPI getApi() {
        return api;
    }

    public void setApi(KuaipanAPI api) {
        this.api = api;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public String getRemotePath() {
        return remotePath;
    }

    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath;
    }

}
