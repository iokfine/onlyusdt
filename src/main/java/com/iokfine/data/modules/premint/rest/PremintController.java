package com.iokfine.data.modules.premint.rest;

import com.iokfine.data.modules.core.domain.annotation.rest.AnonymousGetMapping;
import com.iokfine.data.modules.core.domain.base.RespMsg;
import com.iokfine.data.modules.premint.service.PremintProjectService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class PremintController {


    @Resource
    private PremintProjectService premintProjectService;


    @GetMapping("/fetch/project")
    public RespMsg fetch(String tab,String type)  {

        return RespMsg.successResult(premintProjectService.getByTab(tab,type));
    }
}
