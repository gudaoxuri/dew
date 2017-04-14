package com.ecfront.dew.core.helper;

import com.ecfront.dew.common.JsonHelper;
import com.ecfront.dew.common.Resp;
import com.ecfront.dew.core.dto.PageDTO;

import java.util.List;
import java.util.stream.Collectors;

public class RespHelper {

    public static <E> Resp<E> generic(Resp resp, Class<E> bodyClazz) {
        E body = null;
        if (resp.ok() && resp.getBody() != null) {
            body = JsonHelper.toObject(resp.getBody(), bodyClazz);
        }
        return new Resp<>(resp.getCode(), resp.getMessage(), body);
    }

    public static <E> Resp<List<E>> genericList(Resp resp, Class<E> bodyClazz) {
        List<E> body = null;
        if (resp.ok() && resp.getBody() != null) {
            body = JsonHelper.toList(resp.getBody(), bodyClazz);
        }
        return new Resp<>(resp.getCode(), resp.getMessage(), body);
    }

    public static <E> Resp<PageDTO<E>> genericPage(Resp resp, Class<E> bodyClazz) {
        PageDTO<E> body = null;
        if (resp.ok() && resp.getBody() != null) {
            body = JsonHelper.toObject(resp.getBody(), PageDTO.class);
            body.setObjects(body.getObjects().stream().map(i -> JsonHelper.toObject(i, bodyClazz)).collect(Collectors.toList()));
        }
        return new Resp<>(resp.getCode(), resp.getMessage(), body);
    }

}
