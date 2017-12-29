package com.trasen.workflow.controller;

import com.trasen.workflow.model.MsgDTO;
import com.trasen.workflow.model.ProcessInstDTO;
import com.trasen.workflow.model.TaskInstDTO;
import com.trasen.workflow.service.TasklistService;
import org.apache.log4j.Logger;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangxiahui on 17/10/16.
 */
@RestController
@RequestMapping(value="/wf")
public class WorkflowController {

    @Autowired
    private ProcessEngine camunda;

    @Autowired
    private TasklistService tasklistService;

    private static final Logger logger = Logger.getLogger(WorkflowController.class);


    @RequestMapping(value="/process/{key}",method = RequestMethod.POST)
    public Map<String,Object> queryItem(@PathVariable String key){
        //结果集
        Map<String,Object> result = new HashMap();
        result.put("msg","工作流实例开始执行");
        result.put("code",0);
        try {
            logger.info("======提交工作流key:"+key);

            ProcessInstance processInstance = camunda.getRuntimeService().startProcessInstanceByKey(key);
            ProcessInstDTO processInstDTO = new ProcessInstDTO();
            processInstDTO.setId(processInstance.getId());
            processInstDTO.setBusinessKey(processInstance.getBusinessKey());
            processInstDTO.setCaseInstanceId(processInstance.getCaseInstanceId());
            processInstDTO.setProcessDefinitionId(processInstance.getProcessDefinitionId());
            processInstDTO.setProcessInstanceId(processInstance.getProcessInstanceId());
            processInstDTO.setTenantId(processInstance.getTenantId());
            result.put("processInstance",processInstDTO);
            result.put("code",1);
        } catch (Exception e) {
            logger.error("执行工作流实例异常" + e.getMessage(), e);
            result.put("code",0);
            result.put("msg",e.getMessage());
        }
        return result;
    }


    @RequestMapping(value="/startProcess/{key}",method = RequestMethod.POST)
    public Map<String,Object> queryItem(@PathVariable String key,@RequestBody Map<String,Object> param){
        //结果集
        Map<String,Object> result = new HashMap();
        result.put("msg","工作流实例开始执行");
        result.put("code",0);
        try {
            logger.info("======提交工作流key:"+key);
            ProcessInstance processInstance = camunda.getRuntimeService().startProcessInstanceByKey(key, param);
            Task task = camunda.getTaskService().createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
            Map<String,Object> variables = camunda.getRuntimeService().getVariables(task.getExecutionId());
            variables.put("type", 1);
            camunda.getTaskService().complete(task.getId(), variables);
            ProcessInstDTO processInstDTO = new ProcessInstDTO();
            processInstDTO.setId(processInstance.getId());
            processInstDTO.setBusinessKey(processInstance.getBusinessKey());
            processInstDTO.setCaseInstanceId(processInstance.getCaseInstanceId());
            processInstDTO.setProcessDefinitionId(processInstance.getProcessDefinitionId());
            processInstDTO.setProcessInstanceId(processInstance.getProcessInstanceId());
            processInstDTO.setTenantId(processInstance.getTenantId());
            result.put("processInstance",processInstDTO);
            result.put("code",1);
        } catch (Exception e) {
            logger.error("执行工作流实例异常" + e.getMessage(), e);
            result.put("code",0);
            result.put("msg",e.getMessage());
        }
        return result;
    }




    @RequestMapping(value="/getTodoMsgList/{name}",method = RequestMethod.POST)
    public Map<String,Object> getTask(@PathVariable String name,@RequestBody Map<String,Object> param){
        //结果集
        Map<String,Object> result = new HashMap();
        result.put("msg","成功");
        result.put("code",0);
        try {
            List<Task> tasks ;
            if(param!=null&&param.get("htOwner")!=null){
                tasks = camunda.getTaskService().createTaskQuery().taskAssignee(name)
                        .processVariableValueEquals("htOwner", param.get("htOwner").toString()).list();

            }else{
                tasks = camunda.getTaskService().createTaskQuery().taskAssignee(name).list();
            }
            System.out.println("======获取["+name+"]待办消息["+tasks.size()+"]条=====");
            List<MsgDTO> msgDTOList = new ArrayList<>();
            for(Task task : tasks){
                MsgDTO vo = new MsgDTO();
                vo.setName(task.getAssignee());
                vo.setTitle(task.getName());
                vo.setMsgContent(task.getDescription());
                vo.setSendTime(task.getCreateTime());
                vo.setProcessKey(task.getProcessDefinitionId());
                vo.setProcessId(task.getProcessInstanceId());
                vo.setTaskKey(task.getTaskDefinitionKey());
                vo.setTaskId(task.getId());
                Map<String,Object> variables = camunda.getRuntimeService().getVariables(task.getExecutionId());
                vo.setVariables(variables);
                msgDTOList.add(vo);
            }
            result.put("list",msgDTOList);
            result.put("code",1);
        } catch (Exception e) {
            logger.error("获取待办消息异常" + e.getMessage(), e);
            result.put("code",0);
            result.put("msg",e.getMessage());
        }
        return result;
    }

    @RequestMapping(value="/task/{id}/complete",method = RequestMethod.POST)
    public Map<String,Object> complete(@PathVariable String id){
        //结果集
        Map<String,Object> result = new HashMap();
        result.put("msg","提交成功");
        result.put("code",0);
        try{

            Task task = camunda.getTaskService().createTaskQuery().taskId(id).singleResult();
            MsgDTO vo = new MsgDTO();
            vo.setName(task.getAssignee());
            vo.setTitle(task.getName());
            vo.setMsgContent(task.getDescription());
            vo.setSendTime(task.getCreateTime());
            vo.setProcessKey(task.getProcessDefinitionId());
            vo.setProcessId(task.getProcessInstanceId());
            vo.setTaskKey(task.getTaskDefinitionKey());
            vo.setTaskId(task.getId());
            Map<String,Object> variables = camunda.getRuntimeService().getVariables(task.getExecutionId());
            variables.put("type", 1);
            vo.setVariables(variables);

            camunda.getTaskService().complete(id, variables);

            List<HistoricTaskInstance>  subList = camunda.getHistoryService().createHistoricTaskInstanceQuery().processInstanceId(task.getProcessInstanceId()).list();
            for(HistoricTaskInstance hisTask : subList){
                if(hisTask.getDeleteReason()==null){
                    vo.setSubName(hisTask.getAssignee());
                    vo.setSubTitle(hisTask.getName());
                    vo.setSubTaskId(hisTask.getId());
                    vo.setSubTaskKey(hisTask.getTaskDefinitionKey());
                }
            }
            result.put("task",vo);
            result.put("code",1);
        } catch (Exception e) {
            logger.error("用户服务节提交异常" + e.getMessage(), e);
            result.put("code",0);
            result.put("msg",e.getMessage());
        }
        return result;
    }

    @RequestMapping(value="/task/{id}/return",method = RequestMethod.POST)
    public Map<String,Object> returnNode(@PathVariable String id){
        //结果集
        Map<String,Object> result = new HashMap();
        result.put("msg","驳回成功");
        result.put("code",0);
        try{
            Task task = camunda.getTaskService().createTaskQuery().taskId(id).singleResult();
            MsgDTO vo = new MsgDTO();
            vo.setName(task.getAssignee());
            vo.setTitle(task.getName());
            vo.setMsgContent(task.getDescription());
            vo.setSendTime(task.getCreateTime());
            vo.setProcessKey(task.getProcessDefinitionId());
            vo.setProcessId(task.getProcessInstanceId());
            vo.setTaskKey(task.getTaskDefinitionKey());
            vo.setTaskId(task.getId());
            Map<String,Object> variables = camunda.getRuntimeService().getVariables(task.getExecutionId());
            variables.put("type", 2);
            vo.setVariables(variables);

            camunda.getTaskService().complete(id, variables);

            List<HistoricTaskInstance>  subList = camunda.getHistoryService().createHistoricTaskInstanceQuery().processInstanceId(task.getProcessInstanceId()).list();
            for(HistoricTaskInstance hisTask : subList){
                if(hisTask.getDeleteReason()==null){
                    vo.setSubName(hisTask.getAssignee());
                    vo.setSubTitle(hisTask.getName());
                    vo.setSubTaskId(hisTask.getId());
                    vo.setSubTaskKey(hisTask.getTaskDefinitionKey());
                }
            }
            result.put("task",vo);
            result.put("code",1);
        } catch (Exception e) {
            logger.error("用户节点驳回异常" + e.getMessage(), e);
            result.put("code",0);
            result.put("msg",e.getMessage());
        }
        return result;
    }

    @RequestMapping(value="/queryProcess/{processId}",method = RequestMethod.POST)
    public Map<String,Object> queryProcess(@PathVariable String processId){
        //结果集
        Map<String,Object> result = new HashMap();
        result.put("msg","查询进度成功");
        result.put("code",0);
        try{
            List<TaskInstDTO> list = tasklistService.getTaskInstList(processId);
            result.put("list",list);
            result.put("code",1);
        } catch (Exception e) {
            logger.error("查询进程实例进度异常" + e.getMessage(), e);
            result.put("code",0);
            result.put("msg",e.getMessage());
        }
        return result;
    }
    @RequestMapping(value="/getTask/{taskId}",method = RequestMethod.POST)
    public Map<String,Object> getTask(@PathVariable String taskId){
        //结果集
        Map<String,Object> result = new HashMap();
        result.put("msg","查询节点ID成功");
        result.put("code",0);
        try{
            Task task = camunda.getTaskService().createTaskQuery().taskId(taskId).singleResult();
            MsgDTO vo = new MsgDTO();
            vo.setName(task.getAssignee());
            vo.setTitle(task.getName());
            vo.setMsgContent(task.getDescription());
            vo.setSendTime(task.getCreateTime());
            vo.setProcessKey(task.getProcessDefinitionId());
            vo.setProcessId(task.getProcessInstanceId());
            vo.setTaskKey(task.getTaskDefinitionKey());
            vo.setTaskId(task.getId());
            Map<String,Object> variables = camunda.getRuntimeService().getVariables(task.getExecutionId());
            vo.setVariables(variables);
            result.put("task",vo);
            result.put("code",1);
        } catch (Exception e) {
            logger.error("查询节点ID" + e.getMessage(), e);
            result.put("code",0);
            result.put("msg",e.getMessage());
        }
        return result;
    }
}
