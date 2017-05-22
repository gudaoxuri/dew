package com.ecfront.dew.core.dto;


import com.ecfront.dew.common.Resp;

import java.util.Date;

/**
 *  第三方服务影响对象，继承自Resp，添加了数据来源说明（用于区分是首选还是备选数据源）
 * @param <E>
 */
public class TPSIResp<E> extends Resp<E> {

    private Source source;

    public TPSIResp<E> primary() {
        this.source = new Source(true, "", null);
        return this;
    }

    public TPSIResp<E> optional(String desc, Date updateTime) {
        this.source = new Source(false, desc, updateTime);
        return this;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public class Source {
        private boolean primary;
        private String desc;
        private Date updateTime;

        public Source() {
        }

        public Source(boolean primary, String desc, Date updateTime) {
            this.primary = primary;
            this.desc = desc;
            this.updateTime = updateTime;
        }

        public boolean isPrimary() {
            return primary;
        }

        public void setPrimary(boolean primary) {
            this.primary = primary;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public Date getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(Date updateTime) {
            this.updateTime = updateTime;
        }
    }

}
