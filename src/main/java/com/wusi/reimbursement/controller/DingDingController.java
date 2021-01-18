/*
package com.wusi.reimbursement.controller;

import com.wusi.reimbursement.common.Response;
import com.wusi.reimbursement.utils.DingDingTalkUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

*/
/**
 * @ Description   :  钉钉机器人群发器
 * @ Author        :  wusi
 * @ CreateDate    :  2021/1/18$ 14:32$
 *//*

@RestController
@Slf4j
public class DingDingController {
    @RequestMapping(value = "sendDingDingMessage")
    public Response sendDingDingMessage(String content)  {
        try {
            DingDingTalkUtils.sendDingDingMsg(content);
        } catch (Exception e) {
            log.error("群发异常,{}", content);
            return Response.ok("群发失败!");
        }
        return Response.ok("群发成功!");
    }

}
*/
