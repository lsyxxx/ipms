package com.abt.common.util;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 *
 */
public class MoneyUtil {

    private static final String[] cnNums = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
    private static final String[] cnIntRadice = {"", "拾", "佰", "仟"};
    private static final String[] cnIntUnits = {"", "万", "亿", "兆"};
    private static final String[] cnDecUnits = {"角", "分"};
    private static final String cnInteger = "整";
    private static final String cnIntLast = "元";

    /**
     * 中文大写
     * @return string
     */
    public static String toUpperCase(String money) {
        if (StringUtils.isBlank(money)) {
            return "null";
        }
        StringBuilder result = new StringBuilder();
        String integer = "";
        String point = "";
        if (money.matches("^[0-9]{0,16}|[0-9]{0,16}+([.]{0,1}[0-9]{0,2})$")) {
            BigDecimal bigDecimal = new BigDecimal(money);
            money = new DecimalFormat("##0.00").format(bigDecimal);
            integer = money.split("\\.").length == 1 ? money : money.split("\\.")[0];
            point = money.split("\\.").length == 1 ? "00" : money.split("\\.")[1];
            if ("0".equals(integer)) {
                result = new StringBuilder("零" + cnIntLast);
            } else {
                int zeroCount = 0;
                char[] array = integer.toCharArray();
                String n = "";
                int p = 0, q = 0, m = 0;
                for (int i = 0; i < integer.length(); i++) {
                    n = Character.toString(array[i]);
                    p = integer.length() - i - 1;
                    q = p / 4;
                    m = p % 4;
                    if (n.equals("0")) {
                        zeroCount++;
                    } else {
                        if (zeroCount > 0) {
                            result.append(cnNums[0]);
                        }
                        zeroCount = 0;
                        result.append(cnNums[Integer.parseInt(n)]).append(cnIntRadice[m]);
                    }
                    if (m == 0 && zeroCount < 4) {
                        result.append(cnIntUnits[q]);
                    }
                }
                result.append(cnIntLast);
            }
            if ("00".equals(point)) {
                result.append(cnInteger);
            } else {
                String n = "";
                char[] array = point.toCharArray();
                for (int i = 0; i < point.length(); i++) {
                    n = Character.toString(array[i]);
                    if (!n.equals("0")) {
                        result.append(cnNums[Integer.parseInt(n)]).append(cnDecUnits[i]);
                    }
                }
            }
        } else {
            return "请检查金额（整数位不能超过16位,小数不能超过2位）";
        }
        return result.toString();
    }

    /**
     * 将数字转为百分比
     * @param number 数字
     * @param scale 转为百分比后保留的小数位数
     */
    public static String toPercentage(Double number, int scale) {
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(scale); // 保留小数位
        nf.setMaximumFractionDigits(scale); // 最大小数位
        return nf.format(number);
    }



}
