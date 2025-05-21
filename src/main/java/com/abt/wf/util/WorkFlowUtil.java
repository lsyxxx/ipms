package com.abt.wf.util;

import com.abt.common.model.User;
import com.abt.sys.exception.BusinessException;
import com.abt.wf.config.Constants;
import com.abt.wf.entity.SubcontractTestingSettlementMain;
import com.abt.wf.entity.WorkflowBase;
import com.abt.wf.model.ReimburseForm;
import com.abt.wf.model.UserTaskDTO;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.impl.juel.jakarta.el.MapELResolver;
import org.camunda.bpm.impl.juel.jakarta.el.StandardELContext;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.impl.juel.ExpressionFactoryImpl;
import org.camunda.bpm.impl.juel.jakarta.el.ExpressionFactory;
import org.camunda.bpm.impl.juel.jakarta.el.ValueExpression;
import org.camunda.bpm.impl.juel.SimpleContext;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.model.bpmn.impl.instance.StartEventImpl;

import java.util.*;
import java.util.stream.Collectors;

import com.abt.common.exception.MissingRequiredParameterException;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;

import static com.abt.wf.config.Constants.*;
import static com.abt.wf.config.Constants.DECISION_PASS_DESC;

/**
 *
 */
@Slf4j
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
                final boolean result = evaluateExpression(text, vars);
                if (result) {
                    return sequenceFlow.getTarget();
                }
//                for(Map.Entry<String, Object> entry : vars.entrySet()) {
//                    String k = entry.getKey();
//                    //判断表达式中是否包含参数。
//                    //不包含的话，exp.getValue(context) 报错：找不到property
//                    if (text.contains(k)) {
//                        Object v = entry.getValue();
//                        context.setVariable(k, factory.createValueExpression(v, v.getClass()));
//                        ValueExpression exp = factory.createValueExpression(context, conditionExpression.getTextContent(), Boolean.class);
//                        Object value = exp.getValue(context);
//                        String result = value.toString();
//                        if (Boolean.parseBoolean(result)) {
//                            return sequenceFlow.getTarget();
//                        }
//                    }
//                }
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

    public static void ensureProcessId(String processInstanceId) {
        if (StringUtils.isNotBlank(processInstanceId)) {
            return;
        }
        throw new MissingRequiredParameterException("ProcessInstanceId(流程实例id)");
    }

    public static void ensureProperty(String prop, String propName) {
        if (StringUtils.isNotBlank(prop)) {
            return;
        }
        throw new MissingRequiredParameterException("propName");
    }

    public static Collection<CamundaProperty> queryUserTaskBpmnModelExtensionProperties(BpmnModelInstance bpmnModelInstance, String taskDefId) {
        UserTask userTaskModel = bpmnModelInstance.getModelElementById(taskDefId);
        final String camundaCandidateUsers = userTaskModel.getCamundaCandidateUsers();
        ExtensionElements extensionElements = userTaskModel.getExtensionElements();
        if (extensionElements != null) {
            return extensionElements.getElementsQuery()
                    .filterByType(CamundaProperties.class)
                    .singleResult()
                    .getCamundaProperties();
        }
        return null;
    }

    public static String decisionTranslate(String decision) {
        switch (decision) {
            case DECISION_REJECT -> {
                return DECISION_REJECT_DESC;
            }
            case DECISION_PASS -> {
                return DECISION_PASS_DESC;
            }
            default -> throw new BusinessException("审批决策只能为pass/reject, 实际参数: " + decision);
        }
    }

    public static boolean isPass(String decision) {
        return DECISION_PASS.equals(decision);
    }

    public static boolean isReject(String decision) {
        return DECISION_REJECT.equals(decision);
    }

    public static boolean evaluateExpression(String expression, Map<String, Object> variables) {
        // 1创建 ExpressionFactory
        ExpressionFactory factory = ExpressionFactory.newInstance();

        // 2创建 ELContext 并添加变量解析器
        StandardELContext context = new StandardELContext(factory);
        context.addELResolver(new MapELResolver()); // 允许解析 Map 变量

        // 3绑定变量到 ELContext
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            context.getVariableMapper().setVariable(entry.getKey(),
                    factory.createValueExpression(entry.getValue(), entry.getValue().getClass()));
        }

        // 4解析表达式
        ValueExpression valueExpression = factory.createValueExpression(context, expression, Boolean.class);

        // 5计算结果并返回
        return (Boolean) valueExpression.getValue(context);
    }

    public static String getStringVariable(DelegateTask delegateTask, String varName) {
        final Object variable = delegateTask.getVariable(varName);
        if (variable == null) {
            return "";
        }
        return variable.toString();
    }

    public static String getStringVariable(DelegateExecution delegateExecution, String varName) {
        final Object variable = delegateExecution.getVariable(varName);
        if (variable == null) {
            return "";
        }
        return variable.toString();
    }

    public static String getProcessDefinitionKey(DelegateTask delegateTask) {
        return delegateTask.getProcessDefinitionId().split(":")[0];
    }

    public static String getProcessDefinitionKey(DelegateExecution delegateExecution) {
        return delegateExecution.getProcessDefinitionId().split(":")[0];
    }

}
