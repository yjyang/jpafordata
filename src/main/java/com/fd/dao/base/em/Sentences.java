package com.fd.dao.base.em;

/**
 * 所有支持的查询语句
 * 
 * @author 符冬
 * 
 */
public enum Sentences {
	SELECT {

		@Override
		public String getValue() {
			return "  SELECT  ";
		}
	},
	FROM {

		@Override
		public String getValue() {
			return "  FROM  ";
		}
	},
	WHERE {

		@Override
		public String getValue() {
			return "  WHERE  ";
		}
	},
	/**
	 * 分组
	 */
	GROUPBY {

		@Override
		public String getValue() {
			return "  GROUP  BY  ";
		}
	},
	/**
	 * 分组聚集查询条件
	 */
	HAVING {

		@Override
		public String getValue() {
			return "  HAVING  ";
		}
	},
	/**
	 * 并且
	 */
	AND {

		@Override
		public String getValue() {
			return "  AND  ";
		}
	},
	OR {
		@Override
		public String getValue() {
			return "  OR  ";
		}
	},
	ON {

		@Override
		public String getValue() {

			return "  ON  ";
		}
	},
	/**
	 * 降序
	 */
	DESC {
		@Override
		public String getValue() {
			return "  DESC  ";
		}
	},

	/**
	 * 去掉重复值
	 */
	DISTINCT {

		@Override
		public String getValue() {
			return "  DISTINCT  ";
		}
	},
	IS_NULL {

		@Override
		public String getValue() {

			return "  IS  NULL  ";
		}
	},
	IS_NOT_NULL {

		@Override
		public String getValue() {
			return "  IS  NOT  NULL  ";
		}
	},
	ORDERBY {

		@Override
		public String getValue() {
			return "  ORDER  BY  ";
		}
	};

	@Override
	public String toString() {
		return getValue();
	}

	public abstract String getValue();
}
