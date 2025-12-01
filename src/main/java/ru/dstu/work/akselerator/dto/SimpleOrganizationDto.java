package ru.dstu.work.akselerator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SimpleOrganizationDto {
    private Long id;
    private String name;
    private String orgType;
    private String inn;
}
