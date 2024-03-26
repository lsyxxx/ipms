package com.abt.wf.util;

import com.abt.sys.exception.BusinessException;
import com.abt.wf.entity.WorkflowBase;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.impl.juel.ExpressionFactoryImpl;
import org.camunda.bpm.impl.juel.jakarta.el.ExpressionFactory;
import org.camunda.bpm.impl.juel.jakarta.el.ValueExpression;
import org.camunda.bpm.impl.juel.SimpleContext;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.model.bpmn.impl.instance.StartEventImpl;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Collection;
import java.util.stream.Collectors;

import com.abt.common.exception.MissingRequiredParameterException;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;

/**
 *
 */
public class WorkFlowUtil {


    /**
     * 递归查找node
     * @param startNode 起始node，一般为startEvent
     * @param list 查找的所有node集合，包含传入的起始node
     * @param vars 流程参数
     */
    public static void findActivityNodes(FlowNode startNode, List<FlowNode> list, Map<String, Object> vars) {
        list.add(startNode);
        final FlowNode nextNode = findUniqueNode(startNode, vars);
        if (nextNode != null) {
            findActivityNodes(nextNode, list, vars);
        }
    }


    /**
     * 根据流程参数寻找node连接的唯一node
     * @param node 起始node
     * @param vars 流程参数map
     * @return 连接node
     */
    public static FlowNode findUniqueNode(FlowNode node, Map<String, Object> vars) {
        final Collection<SequenceFlow> outgoing = node.getOutgoing();
        if (outgoing.isEmpty()) {
            return null;
        } else if (outgoing.size() == 1) {
            return node.getSucceedingNodes().singleResult();
        } else {
            //多条连线
            ExpressionFactory factory = new ExpressionFactoryImpl();
            SimpleContext context = new SimpleContext();
            for (SequenceFlow sequenceFlow : outgoing) {
                final ConditionExpression conditionExpression = sequenceFlow.getConditionExpression();
                if (conditionExpression == null) {
                    //没有表达式
                    continue;
                }
                final String type = conditionExpression.getType();
                if (!"bpmn:tFormalExpression".equals(type)) {
                    throw new BusinessException("条件不是juel表达式，无法解析 - " + conditionExpression.getTextContent());
                }
                //--- 暂时仅解析el表达式, 其他类型可能需要用到elResolver
                final String text = conditionExpression.getTextContent();
                for(Map.Entry<String, Object> entry : vars.entrySet()) {
                    String k = entry.getKey();
                    //判断表达式中是否包含参数。
                    //不包含的话，exp.getValue(context) 报错：找不到property
                    if (text.contains(k)) {
                        Object v = entry.getValue();
                        context.setVariable(k, factory.createValueExpression(v, v.getClass()));
                        ValueExpression exp = factory.createValueExpression(context, conditionExpression.getTextContent(), Boolean.class);
                        Object value = exp.getValue(context);
                        String result = value.toString();
                        if (Boolean.parseBoolean(result)) {
                            return sequenceFlow.getTarget();
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 获取预览节点
     * @param bpmnModelInstance bpmn模型
     * @param vars 流程参数map
     * @return 只包含task 不包含startEvent, endEvent
     */
    public static List<FlowNode> getPreviewFlowNode(BpmnModelInstance bpmnModelInstance, Map<String, Object> vars) {
        final Collection<StartEvent> startEvents = bpmnModelInstance.getModelElementsByType(StartEvent.class);
        StartEventImpl startEvent = (StartEventImpl) startEvents.iterator().next();
        List<FlowNode> previewList = new ArrayList<>();
        findActivityNodes(startEvent, previewList, vars);
        //去掉startEvent和endEvent
        return previewList.stream().filter(i -> !(i instanceof StartEvent || i instanceof EndEvent)).collect(Collectors.toList());
    }

    public static void ensureProcessDefinitionKey(WorkflowBase form) {
        if (StringUtils.isNotBlank(form.getProcessDefinitionKey())) {
            return;
        }
        throw new MissingRequiredParameterException("ProcessDefinitionKey(流程定义key)");
    }

    public static void ensureProcessDefinitionId(WorkflowBase form) {
        if (StringUtils.isNotBlank(form.getProcessDefinitionId())) {
            return;
        }
        throw new MissingRequiredParameterException("ProcessDefinitionId(流程定义id)");
    }

    public static void ensureProcessId(WorkflowBase form) {
        if (StringUtils.isNotBlank(form.getProcessInstanceId())) {
            return;
        }
        throw new MissingRequiredParameterException("ProcessInstanceId(流程实例id)");
    }

    public static Collection<CamundaProperty> queryUserTaskBpmnModelExtensionProperties(BpmnModelInstance bpmnModelInstance, String taskDefId) {
        UserTask userTaskModel = bpmnModelInstance.getModelElementById(taskDefId);
        ExtensionElements extensionElements = userTaskModel.getExtensionElements();
        return extensionElements.getElementsQuery()
                .filterByType(CamundaProperties.class)
                .singleResult()
                .getCamundaProperties();
    }

}
