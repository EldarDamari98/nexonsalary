package com.nexonsalary.dto;

import java.util.List;

public class PagedBalanceResponseDto {

    private List<BalanceListItemDto> items;
    private int page;
    private int size;
    private long totalItems;
    private int totalPages;
    private BalanceSummaryDto summary;

    public PagedBalanceResponseDto() {
    }

    public PagedBalanceResponseDto(List<BalanceListItemDto> items,
                                   int page,
                                   int size,
                                   long totalItems,
                                   int totalPages,
                                   BalanceSummaryDto summary) {
        this.items = items;
        this.page = page;
        this.size = size;
        this.totalItems = totalItems;
        this.totalPages = totalPages;
        this.summary = summary;
    }

    public List<BalanceListItemDto> getItems() { return items; }
    public void setItems(List<BalanceListItemDto> items) { this.items = items; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public long getTotalItems() { return totalItems; }
    public void setTotalItems(long totalItems) { this.totalItems = totalItems; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public BalanceSummaryDto getSummary() { return summary; }
    public void setSummary(BalanceSummaryDto summary) { this.summary = summary; }
}