package com.abt.salary.controller;

import com.aspose.pdf.*;

/**
 *
 */
public class PdfTest {
    public static void main(String[] args) {
        // Initialize document object
        Document document = new Document();

        //Add page
        Page page = document.getPages().add();

        // Add text to new page
        page.getParagraphs().add(new TextFragment("Hello World!"));

        // Save updated PDF
        document.save("HelloWorld_out.pdf");
    }
}
