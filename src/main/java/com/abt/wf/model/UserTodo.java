package com.abt.wf.model;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户待办
 */
@Data
public class UserTodo {
    /**
     * 每个流程的待办数量
     * key: processDefinitionKey WorkFlowConfig.DEF_KEY_
     */
    private Map<String, Integer> todoCountGroupedMap = new HashMap<>();

    /**
     * 所有待办合计数量
     */
    private int todoCountAll = 0;

    /**
     * 当前激活的todos列表分类的list
     */
    private List<Object> activeTodoList;

    /**
     * 当前激活的分类
     */
    private String activeKey;


    public void addTodoCount(String defKey, Integer count) {
        todoCountGroupedMap.put(defKey, count);
    }

    public void accumulateCount(int count) {
        this.todoCountAll = this.todoCountAll + count;
    }

}
