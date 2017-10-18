package com.trasen.workflow;

import com.trasen.workflow.model.TaskInstDTO;
import com.trasen.workflow.service.TasklistService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by zhangxiahui on 17/10/18.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes= Application.class)
@Transactional
public class TestServiceTest {

    @Autowired
    TasklistService tasklistService;

    @Test
    public void getTempDataList(){
        List<TaskInstDTO> list = tasklistService.getTaskInstList("9b54fac2-b2e2-11e7-8ff9-ac7f3ee52f3b");
        System.out.println(list.size());
        for(TaskInstDTO taskInstDTO : list){
            System.out.println(taskInstDTO.getName());
        }

    }



}
