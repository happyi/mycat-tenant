package com.example.demo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class CustomerApi {

    @Resource
    CustomerDao customerDao;

    @RequestMapping("get")
    @ResponseBody
    public List getAll(String tenantSchema){
        System.out.println(tenantSchema);

        ThreadLocalUtil.setTenant(tenantSchema);
        return customerDao.findAll();
    }

}
