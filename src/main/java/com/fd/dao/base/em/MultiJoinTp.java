package com.fd.dao.base.em;

/**
 * 联结类型
 * 
 * @author 符冬
 * 
 */
public enum MultiJoinTp {

	/**
	 * 内联结
	 */
	INNER {

		@Override
		public String getValue() {
			return "  INNER  JOIN  ";
		}
	},
	/**
	 * 左外联结
	 */
	LEFT {

		@Override
		public String getValue() {
			return "  LEFT  OUTER  JOIN  ";
		}

	},
	/***
	 * 右外联结
	 */
	RIGHT {
		@Override
		public String getValue() {
			return "  RIGHT  OUTER  JOIN  ";
		}
	};

	@Override
	public String toString() {

		return getValue();
	}

	public abstract String getValue();
}
