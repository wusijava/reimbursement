package com.wusi.reimbursement.controller;

import com.wusi.reimbursement.common.Response;
import com.wusi.reimbursement.entity.RequestContext;
import com.wusi.reimbursement.vo.UserInfo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ Description   :  登录控制器
 * @ Author        :  wusi
 * @ CreateDate    :  2020/1/7$ 11:25$
 */
@RestController
public class LoginController {
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
}
