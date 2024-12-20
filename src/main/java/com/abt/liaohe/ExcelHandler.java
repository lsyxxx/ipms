package com.abt.liaohe;


import java.io.File;
import java.io.IOException;

@FunctionalInterface
public interface ExcelHandler {
    void handle(File file) throws IOException;
}
