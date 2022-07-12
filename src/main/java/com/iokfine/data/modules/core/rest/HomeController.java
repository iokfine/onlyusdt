package com.iokfine.data.modules.core.rest;

import com.iokfine.data.modules.core.domain.annotation.rest.AnonymousGetMapping;
import org.springframework.stereotype.Controller;

@Controller
public class HomeController {

    private static final String REDIRECT_INDEX = "forward:index.html";

    @AnonymousGetMapping("/")
    public String index() {
        return REDIRECT_INDEX;
    }

}