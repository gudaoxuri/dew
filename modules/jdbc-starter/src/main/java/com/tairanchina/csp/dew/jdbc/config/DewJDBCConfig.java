package com.tairanchina.csp.dew.jdbc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "dew")
public class DewJDBCConfig {

    private Jdbc jdbc = new Jdbc();

    public static class Jdbc {

        private List<String> basePackages = new ArrayList<>();

        public List<String> getBasePackages() {
            return basePackages;
        }

        public void setBasePackages(List<String> basePackages) {
            this.basePackages = basePackages;
        }
    }

    public Jdbc getJdbc() {
        return jdbc;
    }

    public void setJdbc(Jdbc jdbc) {
        this.jdbc = jdbc;
    }
}
