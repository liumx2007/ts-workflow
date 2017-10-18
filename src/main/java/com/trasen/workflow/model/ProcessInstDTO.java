package com.trasen.workflow.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by zhangxiahui on 17/10/18.
 */
@Getter
@Setter
public class ProcessInstDTO {

    private String id;
    private String businessKey;
    private String caseInstanceId;
    private String processDefinitionId;
    private String processInstanceId;
    private String tenantId;
}
