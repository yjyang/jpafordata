package com.fd.jpafordata.dao;

import com.fd.dao.base.IBaseDao;
import com.fd.jpafordata.pojo.Fd;

public interface IFdDao extends IBaseDao<Fd> {
	void saveFd(Fd fd);
}
