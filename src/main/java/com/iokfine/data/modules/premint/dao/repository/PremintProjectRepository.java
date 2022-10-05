package com.iokfine.data.modules.premint.dao.repository;

import com.iokfine.data.modules.premint.dao.modal.PremintProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PremintProjectRepository extends JpaRepository<PremintProject, Integer>, JpaSpecificationExecutor<PremintProject> {

    PremintProject findByUrl(String url);

    List<PremintProject> findByTab(String tab);
}
