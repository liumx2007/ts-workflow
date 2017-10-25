package com.trasen.workflow.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Map;

/**
 * Created by zhangxiahui on 17/10/17.
 */
@Getter
@Setter
public class MsgDTO {

    private String name;
    private String title;
    private String msgContent;
    private Date sendTime;
    private String processId;
    private String processKey;
    private String taskId;
    private String taskKey;
    private Map<String, Object> variables;
    private String subName;
    private String subTitle;
    private String subTaskId;
    private String subTaskKey;
}
