package com.abt.wf.service;

import com.abt.wf.entity.UserSignature;

/**
 * 签名
 */
public interface SignatureService {


    /**
     * 获取base64编码图片
     * @param jobNumber 工号
     * @return base64字符串
     */
    UserSignature getSignature(String jobNumber);

    /**
     * 获取base64字符串
     */
    String getUserSignatureBase64String(String jobNumber);

    UserSignature getSignatureByUserid(String userid);

    String getUserSignatureBase64StringByUserid(String userid);

    String getSignatureDir();
}
