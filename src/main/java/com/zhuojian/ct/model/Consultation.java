package com.zhuojian.ct.model;

/**
 * Created by wuhaitao on 2016/3/9.
 */
public class Consultation {
    private String id;
    private String created;
    private String ctfile;
    private String record;
    private String updated;

    public Consultation() {
    }

    public Consultation(String id) {
        this.id = id;
    }

    public Consultation(String id, String created, String ctfile, String record, String updated) {
        this.id = id;
        this.created = created;
        this.ctfile = ctfile;
        this.record = record;
        this.updated = updated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCtfile() {
        return ctfile;
    }

    public void setCtfile(String ctfile) {
        this.ctfile = ctfile;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }
}
