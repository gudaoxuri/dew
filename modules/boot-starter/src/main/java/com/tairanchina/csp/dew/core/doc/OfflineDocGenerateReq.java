package com.tairanchina.csp.dew.core.doc;


import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OfflineDocGenerateReq {

    @NotNull
    private String docName;
    private String docDesc;
    @NotEmpty
    private Map<String, String> visitUrls = new HashMap<>();

    private List<String> swaggerJsonUrls;

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getDocDesc() {
        return docDesc;
    }

    public void setDocDesc(String docDesc) {
        this.docDesc = docDesc;
    }

    public Map<String, String> getVisitUrls() {
        return visitUrls;
    }

    public void setVisitUrls(Map<String, String> visitUrls) {
        this.visitUrls = visitUrls;
    }

    public List<String> getSwaggerJsonUrls() {
        return swaggerJsonUrls;
    }

    public void setSwaggerJsonUrls(List<String> swaggerJsonUrls) {
        this.swaggerJsonUrls = swaggerJsonUrls;
    }
}
