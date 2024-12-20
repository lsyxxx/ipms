package com.abt.oa.model;

import com.abt.common.model.Pair;
import com.abt.oa.entity.Announcement;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 详情
 */
@Data
public class AnnouncementDetail  {

    /**
     * 通知人数
     */
    private int notifyCount;

    /**
     * 通知人员
     */
    private List<String> notifyUsers = new ArrayList<>();

    /**
     * 已读人数
     */
    private int readCount;

    /**
     * 回复人数
     */
    private int commentCount;

    /**
     * 未读人数
     */
    private int unreadCount;

    /**
     * 未回复人数
     */
    private int uncommentCount;

    /**
     * 回复内容，仅有回复内容的
     * key: 回复人员姓名，value: 回复内容
     */
    private List<Pair> commentMap = new ArrayList<>();

    /**
     * 已回复的人员姓名
     */
    private List<String> commentUsers = new ArrayList<>();

    /**
     * 未回复的人员姓名
     */
    private List<String> uncommentUsers = new ArrayList<>();

    private Announcement announcement;
}
