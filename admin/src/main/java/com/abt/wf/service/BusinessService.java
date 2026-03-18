package com.abt.wf.service;

import com.abt.common.model.RequestForm;
import com.abt.wf.entity.Reimburse;
import com.abt.wf.entity.WorkflowBase;
import com.abt.wf.model.ReimburseExportDTO;
import com.abt.wf.model.ReimburseRequestForm;
import jakarta.servlet.http.HttpServletResponse;
import org.camunda.bpm.engine.task.Task;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;

/**
 * 业务相关
 * T: 查询条件
 * R: 返回数据对象实体
 */
public interface BusinessService<T extends RequestForm, R extends WorkflowBase> {

    R saveEntity(R entity);
    R load(String entityId);
    String getEntityId(R entity);
    String getServiceName();

    Page<R> findAllByQueryPageable(T requestForm);

    Page<R> findMyApplyByQueryPageable(T requestForm);

    Page<R> findMyTodoByQueryPageable(T requestForm);

    Page<R> findMyDoneByQueryPageable(T requestForm);

    int countMyTodo(T form);
    int countMyTodoByRequestForm(RequestForm requestForm);

    List<R> findMyTodoList(RequestForm requestForm);

    T createRequestForm();

    /**
     * 导出列表
     */
    void export(T requestForm, HttpServletResponse response, String templatePath, String newFileName, Class<R> dataClass) throws IOException;

    /**
     * 导出详情
     * @param id 业务id
     * @return ReimburseExportDTO 报销详情导出dto
     */
    default ReimburseExportDTO exportDetail(String id) {
        return null;
    }
}
