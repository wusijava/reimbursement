package com.wusi.reimbursement.controller;

import com.alibaba.fastjson.JSONObject;
import com.wusi.reimbursement.common.Response;
import com.wusi.reimbursement.entity.Homework;
import com.wusi.reimbursement.entity.RequestContext;
import com.wusi.reimbursement.query.HomeworkQuery;
import com.wusi.reimbursement.service.HomeworkService;
import com.wusi.reimbursement.utils.DataUtil;
import com.wusi.reimbursement.utils.DateUtil;
import com.wusi.reimbursement.vo.HomeworkList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ Description   :  作业
 * @ Author        :  wusi
 * @ CreateDate    :  2020/12/24$ 15:56$
 */
@RestController
@Slf4j
@RequestMapping(value = "api/")
public class HomeworkController {
    @Autowired
    private HomeworkService homeworkService;
    @ResponseBody
    @RequestMapping(value = "addHomework")
    public Response<String> addHomework(HomeworkList homeworkList){
        RequestContext.RequestUser user = RequestContext.getCurrentUser();
        Homework homework=getHomework(homeworkList);
        if(DataUtil.isNotEmpty(user)&&"admin".equals(user.getUsername())){
            homework.setName("吴思");
        }
        if(DataUtil.isNotEmpty(user)&&"zmx".equals(user.getUsername())){
            homework.setName("张明霞");
        }
        homework.setCreateTime(new Date());
        try {
            homeworkService.insert(homework);
        } catch (Exception e) {
            log.error("添加作业异常,{}", JSONObject.toJSONString(homeworkList));
        }
        return Response.ok("添加成功!");

    }

    private Homework getHomework(HomeworkList homeworkList) {
        Homework work=new Homework();
        work.setContent(homeworkList.getContent());
        work.setSubject(homeworkList.getSubject());
        work.setTimeConsuming(homeworkList.getTimeConsuming());
        work.setUrl(homeworkList.getUrl());
        work.setEvaluate(homeworkList.getEvaluate());
        if(DataUtil.isNotEmpty(homeworkList.getRemark())){
            work.setRemark(homeworkList.getRemark());
        }
        return work;
    }
    @ResponseBody
    @RequestMapping(value = "homeworkList")
    public Response<Page<HomeworkList>> homeworkList(HomeworkQuery query){
        if (DataUtil.isEmpty(query.getPage())) {
            query.setPage(0);
        }
        if (DataUtil.isEmpty(query.getLimit())) {
            query.setLimit(5);
        }
        Pageable pageable= PageRequest.of(query.getPage(), query.getLimit());
        Page<Homework> page=homeworkService.queryPage(query,pageable);
        List<HomeworkList> voList=new ArrayList<>();
        for(Homework homework:page.getContent()){
            voList.add(getWork(homework));
        }
        Page<HomeworkList> voPage=new PageImpl<>(voList,pageable,page.getTotalElements());
        return Response.ok(voPage);
    }

    private HomeworkList getWork(Homework homework) {
        HomeworkList list=new HomeworkList();
        list.setName(homework.getName());
        list.setSubject(homework.getSubject());
        list.setCreateTime(DateUtil.formatDate(homework.getCreateTime(), "yyyy-MM-dd"));
        list.setContent(homework.getContent());
        list.setUrl(homework.getUrl());
        return list;
    }
}
