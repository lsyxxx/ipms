package com.abt.oa.service;

/** 单服务进程内安全取号；同一规则不能与其他取号入口混用。 */
public interface ConcurrentPkService {
    /** 独立提交流水；写入或提交失败抛错，不返回编号、不自动重试。 */
    String nextNumber(String ruleName);
}
