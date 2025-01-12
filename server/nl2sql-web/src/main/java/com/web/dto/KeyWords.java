package com.web.dto;


import lombok.Data;

import java.util.List;

@Data
public class KeyWords {
    private List<String> tables;
    private List<String> columns;
}
