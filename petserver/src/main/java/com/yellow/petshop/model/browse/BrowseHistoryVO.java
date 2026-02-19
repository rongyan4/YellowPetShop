package com.yellow.petshop.model.browse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BrowseHistoryVO {
    private Long id;
    private Long commodityId;
    private String name;
    private BigDecimal price;
    private String mainPicUrl;
    private Integer sold;
    private LocalDateTime browseTime;
}
