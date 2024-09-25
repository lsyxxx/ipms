package com.abt;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Base64;

/**
 *
 */
public class InsertSig {

    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;database=ZJData;trustServerCertificate=true";
    private static final String USER = "sa";
    private static final String PASSWORD = "123456";
    private static final String IMG_ROOT = "F:\\sig";
    private static final String IMG_TEST = "F:\\sig\\test\\";

    public static void main(String[] args) throws IOException {
        getImage();
    }

    public static void getImage() throws IOException {
        Path dir = Paths.get(IMG_ROOT);
        if (!Files.isDirectory(dir)) {
            throw new IllegalArgumentException("提供的路径不是一个有效的目录");
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path entry : stream) {
                if (Files.isRegularFile(entry)) {
                    String fileName = entry.getFileName().toString();
                    System.out.printf("fileName: %s\n", fileName);
                    //公司
                    String name = "";
                    if (fileName.startsWith("D") || fileName.startsWith("G")) {
                        name = fileName.substring(1);
                    } else {
                        name = fileName;
                    }
                    //工号
                    String jobNumber = name.substring(0, 3);
                    //姓名
                    String username = name.substring(3);
                    username = username.split("-")[0];
                    System.out.printf("username: %s\n", username);
                    String base64 = "data:image/png;base64" + Base64.getEncoder().encodeToString(Files.readAllBytes(entry));
                    insertImage(jobNumber, username, entry.toFile(), base64);
                }
            }
        }
    }

    public static void insertImage(String jobNumber, String username, File file, String base64) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            // 读取图片文件并转换为二进制数据
            FileInputStream inputStream = new FileInputStream(file);

            // SQL 语句
            String sql = "INSERT INTO u_sig (id, job_number, user_name, base64, file_name) VALUES (NEWID(), ?, ?, ?, ?)";

            // 使用 PreparedStatement 插入数据
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, jobNumber);
            statement.setString(2, username);
            statement.setString(3, base64);
            statement.setString(4, file.getName());

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Image saved successfully!");
            }

            // 关闭输入流
            inputStream.close();
        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @Accessors(chain = true)
    static class ImageData {
        private String path;
        private String jobNumber;
        private String userid;
        private String username;
        private String imageData;
        private String id;
    }
}
