package com.wusi.reimbursement.controller;

import com.wusi.reimbursement.common.Response;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @ Description   :  附件下载controller
 * @ Author        :  wusi
 * @ CreateDate    :  2020/1/15$ 17:28$
 */
@RestController
public class DownLoadController  {
    @RequestMapping("filedownload")
    public Response<String> fileDownLoad(HttpServletResponse response) throws IOException {
        File file = new File("D:\\a.zip");

        InputStream ins = new FileInputStream(file);
        /* 设置文件ContentType类型，这样设置，会自动判断下载文件类型 */
        response.setContentType("application/zip");
        /* 设置文件头：最后一个参数是设置下载文件名 */
       response.setHeader("Content-Disposition", "attachment;filename="+file.getName());
        try{
            OutputStream os = response.getOutputStream();
            byte[] b = new byte[1024];
            int len;
            while((len = ins.read(b)) > 0){
                os.write(b,0,len);
            }
            os.flush();
            os.close();
            ins.close();
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
        return Response.ok("success");
    }
}
