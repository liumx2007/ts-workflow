package com.trasen.workflow.model;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Your own DTO class used to retrieve results with additional data in one query
 */
@Getter
@Setter
public class TaskDTO {

  private String id;
  private String nameWithoutCascade;
  private String parentTaskIdWithoutCascade;
  private String descriptionWithoutCascade;
  private int priorityWithoutCascade;
  private Date createTime;
  private String ownerWithoutCascade;
  private String assigneeWithoutCascade;
  private String delegationStateString;
  private String executionId;
  private String processInstanceId;
  private String processDefinitionId;
  private String taskDefinitionKeyWithoutCascade;
  private Date dueDateWithoutCascade;





}
