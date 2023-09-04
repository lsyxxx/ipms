package com.abt.flow.service;

import java.util.List;

/**
 * 部署流程
 */
public interface DeployService {


    /**
     * 部署单一流程文件
     * @param file
     */
    void deploySingle(String file);


    /**
     * 部署文件中所有流程文件
     */
    void deployProcesses(String dest);

    /**
     * 部署默认目录resource/processes下所有流程文件
     */
    void deployAll();

    /**
     * 查看所有已部署流程
     * @return
     */
    List getAllDeployed();

    /**
     * 获取流程文件
     * @return
     */
    List getProcessDefinitionFiles();

}
