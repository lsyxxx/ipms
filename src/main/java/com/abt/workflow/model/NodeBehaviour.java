package com.abt.workflow.model;

public interface NodeBehaviour{

    void id();

    String info();

    void print();

    /**
     * 根据模板创建新的实例
     * @param model 模板
     * @return 新的实例
     */
//    T newInstance(T model);

    /**
     * 节点执行方法
     */
//    void execute();



}
