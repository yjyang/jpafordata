package com.fd.dao.base.em;

/**
 * 条件运算类型
 * 
 * @author 符冬
 * 
 */
public enum SqlOp {
	/**
	 * 交集
	 */
	AND {

		@Override
		public String getValue() {
			return "  and  ";
		}

	},
	/**
	 * /** 并集
	 */
	OR {

		@Override
		public String getValue() {
			return " or ";
		}
	};

	@Override
	public String toString() {

		return getValue();
	}

	public abstract String getValue();
}