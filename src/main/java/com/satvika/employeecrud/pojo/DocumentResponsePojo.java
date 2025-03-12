package com.satvika.employeecrud.pojo;

public class DocumentResponsePojo {

    private String fileName;
    private String fileUrl;

    public DocumentResponsePojo() {}

    public DocumentResponsePojo(String fileName, String fileUrl) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
