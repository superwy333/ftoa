package test;


import com.len.Application;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class FTactivitiTest {

    @Autowired
    private ApplicationContext ioc;

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

//    API文档
//    https://www.activiti.org/userguide/#api.services.start.processinstance

    /**
     * 部署流程
     */
    @Test
    public void deploy() {
        processEngine.getRepositoryService()
                .createDeployment()
                .addClasspathResource("processes/qjdemo2.bpmn")
                .deploy();
    }

    @Test
    public void deployWithZip() {
        InputStream in=this.getClass().getClassLoader().getSystemResourceAsStream("processes/qjlc.zip");
        ZipInputStream zipInputStream=new ZipInputStream(in);
        Deployment deployment=processEngine.getRepositoryService()
                .createDeployment().addZipInputStream(zipInputStream)
                .name("请假流程WithZip")
                .deploy();
        System.out.println("流程部署ID:"+deployment.getId());
        System.out.println("流程部署Name:"+deployment.getName());

    }



    /**
     * 删除已经部署的流程
     */
    @Test
    public void delete() {
        String[] ids = {"295001"};
        for (int i =0;i<ids.length;i++) {
            processEngine.getRepositoryService()
                    .deleteDeployment(ids[i],true);
        }
    }

    /**查询流程定义*/
    @Test
    public void findProcessDefinition(){
        List<ProcessDefinition> list = processEngine.getRepositoryService()//与流程定义和部署对象相关的Service
                .createProcessDefinitionQuery()//创建一个流程定义的查询
                /**指定查询条件,where条件*/
//                        .deploymentId(deploymentId)//使用部署对象ID查询
//                        .processDefinitionId(processDefinitionId)//使用流程定义ID查询
//                        .processDefinitionKey(processDefinitionKey)//使用流程定义的key查询
//                        .processDefinitionNameLike(processDefinitionNameLike)//使用流程定义的名称模糊查询

                /**排序*/
                .orderByProcessDefinitionVersion().asc()//按照版本的升序排列
//                        .orderByProcessDefinitionName().desc()//按照流程定义的名称降序排列

                /**返回的结果集*/
                .list();//返回一个集合列表，封装流程定义
//                        .singleResult();//返回惟一结果集
//                        .count();//返回结果集数量
//                        .listPage(firstResult, maxResults);//分页查询
        if(list!=null && list.size()>0){
            for(ProcessDefinition pd:list){
                System.out.println("流程定义ID:"+pd.getId());//流程定义的key+版本+随机生成数
                System.out.println("流程定义的名称:"+pd.getName());//对应helloworld.bpmn文件中的name属性值
                System.out.println("流程定义的key:"+pd.getKey());//对应helloworld.bpmn文件中的id属性值
                System.out.println("流程定义的版本:"+pd.getVersion());//当流程定义的key值相同的相同下，版本升级，默认1
                System.out.println("资源名称bpmn文件:"+pd.getResourceName());
                System.out.println("资源名称png文件:"+pd.getDiagramResourceName());
                System.out.println("部署对象ID："+pd.getDeploymentId());
                System.out.println("#########################################################");
            }
        }
    }

    /**
     * 启动流程
     */
    @Test
    public void start() {
        System.out.println("【Before】Number of process instances: " + runtimeService.createProcessInstanceQuery().count());
        Map<String,Object> variables = new HashMap<>();
        variables.put("assignee","lisa");
        variables.put("days","22");
        variables.put("post","zy");
        ProcessInstance processInstance = processEngine.getRuntimeService()
                .startProcessInstanceByKey("test-process1",variables); // 流程实例id传act_re_procdef.KEY
        System.out.println("【After】Number of process instances: " + runtimeService.createProcessInstanceQuery().count());
    }

    /**
     * 完成任务
     */
    @Test
    public void completeTask() {
        processEngine.getTaskService().complete("1");

    }

    /**
     * 完成任务
     * 带流程变量
     */
    @Test
    public void completeTask2() {
        String post = "cwkzy"; // 财务科职员
        String assignee = "";
        Map<String,Object> variables = new HashMap<>();
        variables.put("post","zy");
        variables.put("days",18);
        if ("cwkzy".equals(post)) assignee = "财务科科长";
        if ("xxkzy".equals(post)) assignee = "信息科科长";
        variables.put("assignee",assignee);
        processEngine.getTaskService().complete("320006",variables);

    }



    @Test
    public void contextLoads() {
        System.out.println(ioc.containsBean("taskService"));
        System.out.println(taskService);

    }

}
