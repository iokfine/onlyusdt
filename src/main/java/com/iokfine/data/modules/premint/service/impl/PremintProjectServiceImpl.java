package com.iokfine.data.modules.premint.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.iokfine.data.modules.premint.dao.modal.PremintProject;
import com.iokfine.data.modules.premint.dao.repository.PremintProjectRepository;
import com.iokfine.data.modules.premint.service.PremintProjectService;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author hjx
 * @date 2022/9/29
 */
@Service
public class PremintProjectServiceImpl implements PremintProjectService {

    @Resource
    private PremintProjectRepository premintProjectRepository;

    @Override
    public void save(PremintProject premintProject) {

        PremintProject premintProject1 = premintProjectRepository.findByUrl(premintProject.getUrl());
        if (premintProject1 != null) {
            premintProject.setId(premintProject1.getId());
        }
        premintProjectRepository.save(premintProject);
    }

    @Override
    public List<PremintProject> getByTab(String tab,String type) {
        PremintProject premintProject =new PremintProject();
        premintProject.setTab(tab);
        if(ObjectUtil.isNotEmpty(type)){
            premintProject.setType(type);
        }
        return premintProjectRepository.findAll(Example.of(premintProject));
    }

    @Override
    public List<PremintProject> getAll() {
        return premintProjectRepository.findAll();
    }
}
