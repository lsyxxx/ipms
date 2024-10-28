package com.abt.liaohe;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.util.Assert;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 *
 */
@Slf4j
public class FileFilter {


    public static void main(String[] args) throws IOException {
//        String keyword = "常规物性";
//        String root = "F:\\00平台资料汇总\\辽河数据\\辽河2024\\";
//        String target = "F:\\00平台资料汇总\\辽河数据\\辽河2024\\2024-辽河-常规物性\\";
//        System.out.println("参数: 关键字(keyword): " + keyword);
//        System.out.println("参数: 查询文件根目录(root): " + root);
//        System.out.println("参数: 复制目标文件夹目录(target): " + target);
//        findAndCopyXlsxFiles(keyword, root, target);
//        System.out.println("---- 过滤及复制文件完成! ----- ");

        SwingUtilities.invokeLater(FileFilter::createAndShowGUI);

    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("文件复制工具");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 250);
        frame.setLayout(new GridLayout(5, 2, 5, 5));

        JLabel sourceLabel = new JLabel("源文件夹:");
        JTextField sourceField = new JTextField();
        JLabel targetLabel = new JLabel("目标文件夹:");
        JTextField targetField = new JTextField();
        JLabel keywordLabel = new JLabel("关键字:");
        JTextField keywordField = new JTextField();
        JButton copyButton = new JButton("开始复制");

        frame.add(sourceLabel);
        frame.add(sourceField);
        frame.add(targetLabel);
        frame.add(targetField);
        frame.add(keywordLabel);
        frame.add(keywordField);
        frame.add(new JLabel());
        frame.add(copyButton);

        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sourceDir = sourceField.getText();
                String targetDir = targetField.getText();
                String keyword = keywordField.getText();

                if (sourceDir.isEmpty() || targetDir.isEmpty() || keyword.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "请填写所有字段。", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    findAndCopyXlsxFiles(keyword, sourceDir, targetDir);
                    JOptionPane.showMessageDialog(frame, "文件复制完成！");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "发生错误: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        frame.setVisible(true);
    }

    /**
     * 筛选出包含关键字的所有报告
     * @param keyword 报告名称关键字
     * @param root 文件夹根目录
     */
    public static void findAndCopyXlsxFiles(String keyword, String root, String targetDir) throws IOException {
        Assert.hasText(root, "根目录(root)不能为空");
        Assert.hasText(targetDir, "目标目录(targetDir)不能为空");
        File rf = new File(root);
        if (!FileUtils.isDirectory(rf)) {
            throw new RuntimeException("根目录(root)不是合法文件");
        }

        Files.walkFileTree(Paths.get(root), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.printf("0. file: %s\n", file.getFileName().toString());
                if ((file.toString().endsWith(".xlsx") || file.toString().endsWith(".xls")) && file.getFileName().toString().contains(keyword)) {
                    System.out.println("1. 查询到文件: " + file.getFileName().toFile());
                    Path targetFile = Paths.get(targetDir).resolve(file.getFileName());
                    Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("2. 复制文件: " + file + " 到 " + targetFile);

                }
                return FileVisitResult.CONTINUE;
            }
        });
    }


}
