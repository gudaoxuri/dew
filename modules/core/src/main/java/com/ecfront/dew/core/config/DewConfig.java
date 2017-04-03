package com.ecfront.dew.core.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "dew")
public class DewConfig {

    private DewBasic basic = new DewBasic();
    private DewCluster cluster = new DewCluster();

    public static class DewBasic {

        private String name;
        private String version = "1.0";
        private String desc;
        private String webSite;
        private DewDoc doc = new DewDoc();
        private DewEntity entity = new DewEntity();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getWebSite() {
            return webSite;
        }

        public void setWebSite(String webSite) {
            this.webSite = webSite;
        }

        public static class DewDoc {

            private String basePackage;

            public String getBasePackage() {
                return basePackage;
            }

            public void setBasePackage(String basePackage) {
                this.basePackage = basePackage;
            }
        }

        public static class DewEntity {

            private List<String> basePackages = new ArrayList<String>() {{
                add("com.ecfront.dew");
            }};

            public List<String> getBasePackages() {
                return basePackages;
            }

            public void setBasePackages(List<String> basePackages) {
                this.basePackages = basePackages;
            }
        }

        public DewDoc getDoc() {
            return doc;
        }

        public void setDoc(DewDoc doc) {
            this.doc = doc;
        }

        public DewEntity getEntity() {
            return entity;
        }

        public void setEntity(DewEntity entity) {
            this.entity = entity;
        }

    }

    public DewBasic getBasic() {
        return basic;
    }

    public static class DewCluster {

    }

    public void setBasic(DewBasic basic) {
        this.basic = basic;
    }

    public DewCluster getCluster() {
        return cluster;
    }

    public void setCluster(DewCluster cluster) {
        this.cluster = cluster;
    }

}
