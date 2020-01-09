package com.wusi.reimbursement.controller;

import com.wusi.reimbursement.common.Response;
import com.wusi.reimbursement.entity.Reimbursement;
import com.wusi.reimbursement.entity.RequestContext;
import com.wusi.reimbursement.query.ReimbursementQuery;
import com.wusi.reimbursement.service.ReimbursementService;
import com.wusi.reimbursement.service.RoleService;
import com.wusi.reimbursement.utils.DataUtil;
import com.wusi.reimbursement.utils.DateUtil;
import com.wusi.reimbursement.vo.HomeMenuList;
import com.wusi.reimbursement.vo.ReimbursementList;
import com.wusi.reimbursement.vo.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ Description   :  登录控制器
 * @ Author        :  wusi
 * @ CreateDate    :  2020/1/7$ 11:25$
 */
@RestController
public class BaseController {
    @Autowired
    private RoleService roleService;
    @Autowired
    private ReimbursementService reimbursementService;
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Response<UserInfo> login(String username, String password) {
        RequestContext.RequestUser user = RequestContext.getCurrentUser();
        UserInfo info = new UserInfo();
        info.setMobile(user.getMobile());
        info.setUsername(user.getUsername());
        info.setUid(user.getUid());
        return Response.ok(info);
    }
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public Response<List<HomeMenuList>> list() {
        List<HomeMenuList> list = roleService.findPermissionByType(0);
        return Response.ok(list);
    }
    @RequestMapping(value = "/productList", method = RequestMethod.POST)
    @ResponseBody
    public Response<Page<ReimbursementList>> productList(ReimbursementQuery query) {
        if (DataUtil.isEmpty(query.getPage())) {
            query.setPage(0);
        }
        if (DataUtil.isEmpty(query.getLimit())) {
            query.setLimit(10);
        }
        Pageable pageable = PageRequest.of(query.getPage(), query.getLimit());
        Page<Reimbursement>  page= reimbursementService.queryPage(query,pageable);
        List<ReimbursementList> volist=new ArrayList<>();
        for (Reimbursement reimbursement:page.getContent()){
            volist.add(getReimbursementLististVo(reimbursement));
        }
        Page<ReimbursementList> vopage=new PageImpl<>(volist, pageable, page.getTotalElements());
        return Response.ok(vopage);
    }
    private ReimbursementList getReimbursementLististVo(Reimbursement reimbursement) {
        ReimbursementList reimbursementList = new ReimbursementList();
        reimbursementList.setId(reimbursement.getId());
        reimbursementList.setProductName(reimbursement.getProductName());
        reimbursementList.setTotalPrice(reimbursement.getTotalPrice());
        reimbursementList.setBuyChannel(reimbursement.getBuyChannel());
        reimbursementList.setBuyDate(DateUtil.formatDate(reimbursement.getBuyDate(), DateUtil.PATTERN_YYYY_MM_DD));
        reimbursementList.setReimbursementDate(reimbursement.getReimbursementDate()==null ? "未上交单据":DateUtil.formatDate(reimbursement.getReimbursementDate(), DateUtil.PATTERN_YYYY_MM_DD));
        reimbursementList.setRemitDate(reimbursement.getRemitDate()==null ? "未到账":DateUtil.formatDate(reimbursement.getRemitDate(), DateUtil.PATTERN_YYYY_MM_DD));
        reimbursementList.setState(reimbursement.getStateDesc());
        reimbursementList.setRemark(reimbursement.getRemark());
        return reimbursementList;
    }
    @RequestMapping(value = "/toDetails", method = RequestMethod.POST)
    @ResponseBody
    public Response<ReimbursementList> todetails(ReimbursementQuery query) {
        Reimbursement reimbursement=reimbursementService.queryOne(query);
        return Response.ok(getReimbursementLististVo(reimbursement));
    }
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public Response<String> update(ReimbursementList query) throws ParseException {
        System.out.println(query.toString());
        Reimbursement reimbursement=getReimbursement(query);
        reimbursementService.updateById(reimbursement);
        return Response.ok("ok");
    }
    private Reimbursement getReimbursement(ReimbursementList reimbursementList) throws ParseException {
        Reimbursement reimbursement = new Reimbursement();
        reimbursement.setId(reimbursementList.getId());
        reimbursement.setProductName(reimbursementList.getProductName());
        reimbursement.setTotalPrice(reimbursementList.getTotalPrice());
        reimbursement.setBuyChannel(reimbursementList.getBuyChannel());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date buydate=simpleDateFormat.parse(reimbursementList.getBuyDate());
        reimbursement.setBuyDate(buydate);
        if("未上交单据".equals(reimbursementList.getReimbursementDate())){
            reimbursement.setReimbursementDate(null);
        }else{
            Date reimbursementDate=simpleDateFormat.parse(reimbursementList.getReimbursementDate());
            reimbursement.setReimbursementDate(reimbursementDate);
        }
        if("未到账".equals(reimbursementList.getRemitDate())){
            reimbursement.setRemitDate(null);
        }else{
            Date remitDate=simpleDateFormat.parse(reimbursementList.getRemitDate());
            reimbursement.setRemitDate(remitDate);
        }
        reimbursement.setState(reimbursement.getStatecode(reimbursementList.getState()));
        reimbursement.setRemark(reimbursementList.getRemark());
        return reimbursement;
    }
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public Response<String> save(ReimbursementList reimbursementList) throws ParseException {
        Reimbursement reimbursement=saveReimbursement(reimbursementList);
        reimbursementService.insert(reimbursement);
        return Response.ok("");
    }
    private Reimbursement saveReimbursement(ReimbursementList reimbursementList) throws ParseException {
        Reimbursement reimbursement = new Reimbursement();
        reimbursement.setId(reimbursementList.getId());
        reimbursement.setProductName(reimbursementList.getProductName());
        reimbursement.setTotalPrice(reimbursementList.getTotalPrice());
        reimbursement.setBuyChannel(reimbursementList.getBuyChannel());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date buydate=simpleDateFormat.parse(reimbursementList.getBuyDate());
        reimbursement.setBuyDate(buydate);
        reimbursement.setState(-1);
        reimbursement.setRemark(reimbursementList.getRemark());
        return reimbursement;
    }
}
