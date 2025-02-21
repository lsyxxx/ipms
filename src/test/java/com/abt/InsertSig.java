package com.abt;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
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
//        getImage();
        insertOne("079", "李伟", "C:\\Users\\Administrator\\Desktop\\liwei079.png");
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
                    String base64 = "data:image/png;base64," + Base64.getEncoder().encodeToString(Files.readAllBytes(entry));
                    insertImage(jobNumber, username, entry.toFile(), base64);
                }
            }
        }
    }

    public static void insertImage(String jobNumber, String username, File file, String base64) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            // 读取图片文件并转换为二进制数据
            if (!file.exists()) {
                throw new FileNotFoundException(file.getName() + "签名图片不存在!");
            }
            FileInputStream inputStream = new FileInputStream(file);

            int count = 0;
            String sql = "select count(1) from u_sig where job_number = ? and user_name = ?";
            PreparedStatement s1 = connection.prepareStatement(sql);
            s1.setString(1, jobNumber);
            s1.setString(2, username);
            final ResultSet resultSet = s1.executeQuery();
            if (resultSet.next()) {
                count = resultSet.getInt(1);
                System.out.printf("已存在用户[%s, %s]的数据\n", jobNumber, username);
            }
            if (count > 0) {
                sql = "update u_sig set base64 = ? where job_number = ? and user_name = ?";
                PreparedStatement s2 = connection.prepareStatement(sql);
                s2.setString(1, base64);
                s2.setString(2, jobNumber);
                s2.setString(3, username);
                final int update = s2.executeUpdate();
                if (update > 0) {
                    System.out.println("Image Update successfully!");
                }
            } else {
                // SQL 语句
                sql = "INSERT INTO u_sig (id, job_number, user_name, base64, file_name) VALUES (NEWID(), ?, ?, ?, ?)";

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
            }
            // 关闭输入流
            inputStream.close();
        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 插入单独的
     * @param jobNumber
     * @param imgPath
     */
    public static void insertOne(String jobNumber, String name, String imgPath) throws IOException {
        File file = new File(imgPath);
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] fileBytes = new byte[(int) file.length()];
        fileInputStream.read(fileBytes);
        fileInputStream.close();

        // 使用Base64进行编码
        String base64 = "data:image/png;base64," + Base64.getEncoder().encodeToString(fileBytes);
        System.out.println("base64Str: " + ("data:image/png;base64,"+ base64));
        insertImage(jobNumber, name, file, base64);
    }

}
