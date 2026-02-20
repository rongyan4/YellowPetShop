package com.yellow.petshop.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页结果封装类
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    /**
     * 当前页码
     */
    private Long current;
    
    /**
     * 每页大小
     */
    private Long size;
    
    /**
     * 总记录数
     */
    private Long total;
    
    /**
     * 总页数
     */
    private Long pages;
    
    /**
     * 数据列表
     */
    private List<T> records;
    
    /**
     * 是否有下一页
     */
    private Boolean hasNext;
    
    // Getters
    public Long getCurrent() {
        return current;
    }
    
    public Long getSize() {
        return size;
    }
    
    public Long getTotal() {
        return total;
    }
    
    public Long getPages() {
        return pages;
    }
    
    public List<T> getRecords() {
        return records;
    }
    
    public List<T> getList() {
        return records;
    }
    
    public Integer getPageNum() {
        return current != null ? current.intValue() : null;
    }
    
    public Integer getPageSize() {
        return size != null ? size.intValue() : null;
    }
    
    public Boolean getHasNext() {
        return hasNext;
    }
    
    // Setters
    public void setCurrent(Long current) {
        this.current = current;
    }
    
    public void setSize(Long size) {
        this.size = size;
    }
    
    public void setTotal(Long total) {
        this.total = total;
    }
    
    public void setPages(Long pages) {
        this.pages = pages;
    }
    
    public void setRecords(List<T> records) {
        this.records = records;
    }
    
    public void setList(List<T> list) {
        this.records = list;
    }
    
    public void setPageNum(Integer pageNum) {
        this.current = pageNum != null ? pageNum.longValue() : null;
    }
    
    public void setPageSize(Integer pageSize) {
        this.size = pageSize != null ? pageSize.longValue() : null;
    }
    
    public void setHasNext(Boolean hasNext) {
        this.hasNext = hasNext;
    }
}
