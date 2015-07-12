package com.fd.jpafordata;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fd.jpafordata.dao.IFdDao;
import com.fd.jpafordata.pojo.Fd;

/**
 * Unit test for simple App.
 */
public class AppTest

{
	ApplicationContext context = null;
	IFdDao fdDao = null;

	@Before
	public void up() {
		context = new ClassPathXmlApplicationContext("main.xml");
		fdDao = context.getBean(IFdDao.class);
	}

	@Test
	public void query() {
		List<Fd> fds = fdDao.getList();
		for (Fd fd : fds) {
			System.out.println(fd.getCreateDate().toString() + ":"
					+ fd.getSname());
		}
	}

	@Test
	public void update() {
		Fd fd = new Fd(new Timestamp(System.currentTimeMillis()), UUID
				.randomUUID().toString());
		fdDao.saveFd(fd);
	}
}
