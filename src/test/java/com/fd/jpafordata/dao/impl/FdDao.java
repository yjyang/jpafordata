package com.fd.jpafordata.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.fd.dao.base.impl.BaseDao;
import com.fd.jpafordata.dao.IFdDao;
import com.fd.jpafordata.pojo.Fd;

@Repository
public class FdDao extends BaseDao<Fd> implements IFdDao {
	private static final long serialVersionUID = -5857661587686994083L;
	@PersistenceContext
	private EntityManager em;

	@Override
	protected EntityManager getEm() {
		return em;
	}

	@Transactional
	@Override
	public void saveFd(Fd fd) {
		save(fd);
	}

}
