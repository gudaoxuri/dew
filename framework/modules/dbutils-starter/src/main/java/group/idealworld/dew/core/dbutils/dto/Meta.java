package group.idealworld.dew.core.dbutils.dto;

import lombok.Data;

@Data
public class Meta {

    private int type;
    private String code;
    private String label;

    public Meta(int type, String code, String label) {
        this.type = type;
        this.code = code;
        this.label = label;
    }
}
