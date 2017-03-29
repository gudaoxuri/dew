package com.ecfront.dew.core.dto;

import java.util.List;

public class PageDTO<E> {

    private long pageNumber;
    private long pageSize;
    private long pageTotal;
    private long recordTotal;
    private List<E> objects;

    public static <S> PageDTO<S> build(long pageNumber, long pageSize, long recordTotal, List<S> objects) {
        PageDTO<S> dto = new PageDTO<>();
        dto.pageNumber = pageNumber;
        dto.pageSize = pageSize;
        dto.recordTotal = recordTotal;
        dto.pageTotal = (recordTotal + pageSize - 1) / pageSize;
        dto.objects = objects;
        return dto;
    }

    public long getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(long pageNumber) {
        this.pageNumber = pageNumber;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public long getPageTotal() {
        return pageTotal;
    }

    public void setPageTotal(long pageTotal) {
        this.pageTotal = pageTotal;
    }

    public long getRecordTotal() {
        return recordTotal;
    }

    public void setRecordTotal(long recordTotal) {
        this.recordTotal = recordTotal;
    }

    public List<E> getObjects() {
        return objects;
    }

    public void setObjects(List<E> objects) {
        this.objects = objects;
    }

}
