package com.zhuojian.ct.model;

/**
 * Created by wuhaitao on 2016/3/10.
 */
public class CTImage {
    private int id;
    private String type;
    private String file;
    private String diagnosis;
    private int consultationId;

    public CTImage() {
    }

    public CTImage(int id, String type, String file, String diagnosis, int consultationId) {
        this.id = id;
        this.type = type;
        this.file = file;
        this.diagnosis = diagnosis;
        this.consultationId = consultationId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public int getConsultationId() {
        return consultationId;
    }

    public void setConsultationId(int consultationId) {
        this.consultationId = consultationId;
    }
}
