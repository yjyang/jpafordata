package com.fd.jpafordata.pojo;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "fd")
public class Fd {
	@Id
	@GeneratedValue
	private Long sid;
	private Timestamp createDate = new Timestamp(System.currentTimeMillis());
	private String sname;

	public Long getSid() {
		return sid;
	}

	public Fd(Timestamp createDate, String sname) {
		super();
		this.createDate = createDate;
		this.sname = sname;
	}

	public Fd() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void setSid(Long sid) {
		this.sid = sid;
	}

	public Timestamp getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}

	public String getSname() {
		return sname;
	}

	public void setSname(String sname) {
		this.sname = sname;
	}

}
