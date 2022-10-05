package com.iokfine.data.modules.premint.service;

import com.iokfine.data.modules.premint.dao.modal.PremintProject;

import java.util.List;

/**
 * @author hjx
 * @date 2022/9/29
 */
public interface PremintProjectService {

    void save(PremintProject premintProject);

    List<PremintProject> getByTab(String tab,String type);

    List<PremintProject> getAll();


}
