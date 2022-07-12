package com.iokfine.data.modules.notify.dao.repository;

import com.iokfine.data.modules.notify.dao.model.MailMsgLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


/**
 * @author hjx
 * @date 2021/10/18
 */
@Repository
public interface MailMsgLogRepository extends JpaRepository<MailMsgLog, Integer>, JpaSpecificationExecutor<MailMsgLog> {

}
