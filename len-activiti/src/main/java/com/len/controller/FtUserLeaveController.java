package com.len.controller;


import com.len.base.CurrentUser;
import com.len.entity.UserLeave;
import com.len.service.UserLeaveService;
import com.len.util.ActivitiUtils;
import com.len.util.CommonUtil;
import com.len.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/ftleave")
@Slf4j
public class FtUserLeaveController {

    private static final String FT_USER_LEAVE_PROCESS_INSTANCE_KEY = "myProcess_1";

    @Autowired
    private ActivitiUtils activitiUtils;

    @Autowired
    private UserLeaveService userLeaveService;


    /**
     * 请假申请表单提交
     * @param model
     * @param userLeave
     * @return
     */
    @PostMapping("addLeave")
    @ResponseBody
    public JsonUtil addLeave(Model model, @RequestBody UserLeave userLeave) {
        JsonUtil j = new JsonUtil();
        try {
            // TODO 请假资格校验
            // TODO userLeave参数校验
            // 组装流程变量
            //TODO 获取当前用户职位，即post
            CurrentUser user = CommonUtil.getUser();
            Map<String,Object> variables = new HashMap<>();
            variables.put("days",userLeave.getDays());
            variables.put("post","zy");
            variables.put("assignee",user.getUsername());
            // 启动请假流程
            ProcessInstance processInstance = activitiUtils.startProcessByInstanceKey(FT_USER_LEAVE_PROCESS_INSTANCE_KEY,variables);
            variables.put("processInstanceId",processInstance.getProcessInstanceId()); // 启动流程之后把流程实例id存入流程变量，方便后续查询taskId
            // 插入userLeave数据
            userLeave.setProcessInstanceId(processInstance.getProcessInstanceId());
            userLeaveService.insert(userLeave);

            j.setFlag(true);
            j.setMsg("提交请假申请成功");
        }catch (Exception e) {
            log.error("提交请假申请失败",e);
            j.setFlag(false);
            j.setMsg("提交请假申请失败");
        }
        return j;
    }






}
