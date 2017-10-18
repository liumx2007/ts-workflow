package com.trasen.workflow.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by zhangxiahui on 17/10/18.
 */
@Getter
@Setter
public class TaskInstDTO {

    private String id;
    private String taskDefKey;
    private String procDefKey;
    private String name;
    private String assignee;
    private String startTime;
    private String endTime;
    private String deleteReason;



}
