package com.abt.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 键值对
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pair {
    private Object key;
    private Object value;

}
