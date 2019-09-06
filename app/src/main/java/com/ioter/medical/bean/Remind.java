package com.ioter.medical.bean;

public class Remind extends BaseEntity {

    /**
     * Id : sample string 1
     * Title : sample string 2
     * Content : sample string 3
     * Status : 0
     * CreateUser : sample string 4
     * CreateTime : 2019-09-06 15:51:09
     */

    private String Id;
    private String Title;
    private String Content;
    private int Status;
    private String CreateUser;
    private String CreateTime;

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String Content) {
        this.Content = Content;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int Status) {
        this.Status = Status;
    }

    public String getCreateUser() {
        return CreateUser;
    }

    public void setCreateUser(String CreateUser) {
        this.CreateUser = CreateUser;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String CreateTime) {
        this.CreateTime = CreateTime;
    }
}
