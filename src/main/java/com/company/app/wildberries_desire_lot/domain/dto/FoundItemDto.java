package com.company.app.wildberries_desire_lot.domain.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoundItemDto {

    private String article;
    private Date creationDate;
    private String link;
}
