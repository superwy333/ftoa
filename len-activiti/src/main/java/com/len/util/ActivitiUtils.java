package com.len.util;

import org.activiti.engine.*;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ActivitiUtils {


    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private HistoryService historyService;


    /**
     * 根据流程key启动流程
     * @param processInstanceKey
     * @throws Exception
     */
    public ProcessInstance startProcessByInstanceKey(String processInstanceKey) throws Exception {
        return processEngine.getRuntimeService()
                .startProcessInstanceByKey(processInstanceKey); // 流程实例id传act_re_procdef.KEY
    }

    public ProcessInstance startProcessByInstanceKey(String processInstanceKey, Map<String,Object> variables) throws Exception {
        return processEngine.getRuntimeService()
                .startProcessInstanceByKey(processInstanceKey, variables); // 流程实例id传act_re_procdef.KEY
    }


    /**
     * 完成任务
     * @param variables
     * @param taskId
     * @throws Exception
     */
    public void completeTask(Map<String,Object> variables, String taskId) throws Exception {
        processEngine.getTaskService().complete(taskId,variables);
    }

    /**
     * 根据流程实例Id获取当前任务
     * @param processInstanceId
     * @return
     * @throws Exception
     */
    public Task getTasksByProcessInstanceId(String processInstanceId) throws Exception {
        return taskService.createTaskQuery().processInstanceId("320001").singleResult();
    }












}
