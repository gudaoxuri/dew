package com.ecfront.dew.core.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "dew")
public class DewConfig {

    private DewBasic basic;
    private DewDoc doc;

    public static class DewBasic {

        private String name;
        private String version;
        private String desc;
        private String webSite;

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

    public DewBasic getBasic() {
        return basic;
    }

    public void setBasic(DewBasic basic) {
        this.basic = basic;
    }

    public DewDoc getDoc() {
        return doc;
    }

    public void setDoc(DewDoc doc) {
        this.doc = doc;
    }
}
