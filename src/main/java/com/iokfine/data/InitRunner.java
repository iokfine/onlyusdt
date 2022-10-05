package com.iokfine.data;

import com.iokfine.data.modules.premint.service.PremintPaser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author hjx
 * @date 2021/8/18
 */
@Service
@Slf4j
public class InitRunner implements ApplicationRunner {

    @Resource
    private PremintPaser premintPaser;

    private void initUserProfile(){

    }

    @Override
    public void run(ApplicationArguments args) {
//        premintPaser.catchMore();
//        premintPaser.catchProject();
    }

}

