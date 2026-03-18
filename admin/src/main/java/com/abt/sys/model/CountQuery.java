package com.abt.sys.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountQuery {
    private String name;
    private long count;

    public CountQuery(int name, long count) {
        this.name = String.valueOf(name);
        this.count = count;
    }

    public static CountQuery empty() {
        return new CountQuery("", 0);
    }

    @Override
    public String toString() {
        return "CountQuery {" + name + ": " + count + '}';
    }
}
