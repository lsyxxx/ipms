package com.abt.sigcut;

import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class SignatureCutout {

    // ======== 可调参数 ========

    /** 裁剪时左右留白像素 */
    private static final int PADDING_X = 3;

    /** 裁剪时上下留白像素 */
    private static final int PADDING_Y = 3;

    /** 签名加深倍率，越大越黑 */
    private static final double ALPHA_GAIN = 1.8;

    /** 签名加深偏移，负数会进一步压掉浅灰 */
    private static final double ALPHA_BIAS = -10.0;

    /** 去掉很浅的背景噪声 */
    private static final int ALPHA_NOISE_THRESHOLD = 20;

    static {
        // 自动加载 OpenCV 本地库
        OpenCV.loadLocally();

        // 如果你不用 org.openpnp:opencv，而是官方 OpenCV，
        // 可以改成下面这种方式：
        // System.load("D:/opencv/build/java/x64/opencv_java490.dll");
    }

    /**
     * 主处理流程：
     * 1. 读图
     * 2. 灰度化
     * 3. 自动找到签名区域
     * 4. 少量留白裁剪
     * 5. 背景透明
     * 6. 签名加深
     * 7. 输出 PNG
     */
    public static void processSignature(String inputPath, String outputPath) {
        Mat src = Imgcodecs.imread(inputPath, Imgcodecs.IMREAD_COLOR);
        if (src.empty()) {
            throw new RuntimeException("无法读取图片：" + inputPath);
        }

        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);

        // 1. 自动找到签名区域
        Rect signatureRect = findSignatureRect(gray);

        // 2. 加极少留白，避免裁到笔画
        Rect cropRect = addPadding(signatureRect, src.cols(), src.rows(), PADDING_X, PADDING_Y);

        // 3. 裁剪灰度图（后续做透明）
        Mat croppedGray = new Mat(gray, cropRect).clone();

        // 4. 根据灰度生成透明通道，并加深签名
        Mat alpha = buildAlphaMask(croppedGray);

        // 5. 生成黑色签名 + 透明背景 PNG（BGRA）
        Mat result = buildTransparentBlackImage(alpha);

        // 6. 保存结果（必须 PNG，JPG 不支持透明）
        boolean ok = Imgcodecs.imwrite(outputPath, result);
        if (!ok) {
            throw new RuntimeException("保存图片失败：" + outputPath);
        }

        // 释放资源
        src.release();
        gray.release();
        croppedGray.release();
        alpha.release();
        result.release();
    }

    /**
     * 自动找签名区域
     *
     * 思路：
     * - 灰度图
     * - 轻微模糊降噪
     * - Otsu 二值化
     * - 轻微膨胀，避免边缘细笔画没被算进去
     * - 找所有非零点
     * - 计算最小外接矩形
     */
    private static Rect findSignatureRect(Mat gray) {
        Mat blur = new Mat();
        Imgproc.GaussianBlur(gray, blur, new Size(3, 3), 0);

        Mat mask = new Mat();
        Imgproc.threshold(
                blur,
                mask,
                0,
                255,
                Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU
        );

        // 稍微膨胀一点，防止非常细的笔锋边界被裁掉
        Mat kernel = Imgproc.getStructuringElement(
                Imgproc.MORPH_ELLIPSE,
                new Size(2, 2)
        );
        Imgproc.dilate(mask, mask, kernel);

        Mat points = new Mat();
        Core.findNonZero(mask, points);

        if (points.empty()) {
            blur.release();
            mask.release();
            kernel.release();
            points.release();
            throw new RuntimeException("未检测到签名区域，请检查图片是否过白或内容过浅");
        }

        Rect rect = Imgproc.boundingRect(points);

        blur.release();
        mask.release();
        kernel.release();
        points.release();

        return rect;
    }

    /**
     * 给签名区域加少量边距
     */
    private static Rect addPadding(Rect rect, int imageWidth, int imageHeight, int padX, int padY) {
        int x = Math.max(0, rect.x - padX);
        int y = Math.max(0, rect.y - padY);

        int right = Math.min(imageWidth, rect.x + rect.width + padX);
        int bottom = Math.min(imageHeight, rect.y + rect.height + padY);

        return new Rect(x, y, right - x, bottom - y);
    }

    /**
     * 根据灰度图生成透明度 Alpha
     *
     * 原理：
     * - 白色背景接近 255
     * - 黑色签名接近 0
     * - alpha = 255 - gray
     * - 再通过 gain/bias 加深签名
     * - 最后阈值去掉很浅的灰色背景
     */
    private static Mat buildAlphaMask(Mat croppedGray) {
        Mat white = new Mat(croppedGray.size(), CvType.CV_8UC1, new Scalar(255));
        Mat alpha = new Mat();

        // alpha = 255 - gray
        Core.subtract(white, croppedGray, alpha);

        // 加深签名
        alpha.convertTo(alpha, CvType.CV_8UC1, ALPHA_GAIN, ALPHA_BIAS);

        // 去掉浅背景噪声
        Imgproc.threshold(alpha, alpha, ALPHA_NOISE_THRESHOLD, 255, Imgproc.THRESH_TOZERO);

        white.release();
        return alpha;
    }

    /**
     * 生成透明背景黑色签名图
     *
     * 输出为 BGRA：
     * B = 0
     * G = 0
     * R = 0
     * A = alpha
     */
    private static Mat buildTransparentBlackImage(Mat alpha) {
        Mat b = Mat.zeros(alpha.size(), CvType.CV_8UC1);
        Mat g = Mat.zeros(alpha.size(), CvType.CV_8UC1);
        Mat r = Mat.zeros(alpha.size(), CvType.CV_8UC1);

        List<Mat> channels = new ArrayList<>(4);
        channels.add(b);
        channels.add(g);
        channels.add(r);
        channels.add(alpha);

        Mat result = new Mat();
        Core.merge(channels, result);

        b.release();
        g.release();
        r.release();

        return result;
    }
}