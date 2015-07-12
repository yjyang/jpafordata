package com.fd.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

/***
 * 全局工具类
 * 
 * @author 符冬
 * 
 */
public final class MyUtils {

	/**
	 * 得到文件的后缀名<br/>
	 * System.out.println(getFileSubString("fudong.jpg"));>>>.jpg
	 * 
	 * @param fileName
	 * @return
	 */
	public final static String getFileSubString(String fileName) {
		if (fileName != null && fileName.indexOf(".") != -1) {
			return fileName.substring(fileName.indexOf("."));
		}
		return "";
	}

	/**
	 * 通过不定参数返回参数类型数组
	 * 
	 * @param objects
	 * @return
	 */
	public final static <T extends Object> T[] getObjs(T... ts) {
		return ts;
	}

	/**
	 * 去掉任何空白字符
	 * 
	 * @param content
	 * @return
	 */
	public static String getTrimStr(String content) {
		if (content == null) {
			return "";
		}
		return content.replaceAll("[\\s\\p{Zs}]+", "");
	}

	/**
	 * 删除集合当中的null值
	 * 
	 * @param list
	 */
	public static void removeNull(Collection<?> list) {
		if (isNotEmpty(list)) {
			Iterator<?> iter = list.iterator();
			while (iter.hasNext()) {
				if (iter.next() == null) {
					iter.remove();
				}
			}
		}
	}

	/**
	 * 判断是否包含内容
	 * 
	 */
	public final static <T extends Object> boolean isNotEmpty(T str) {
		return str != null && !str.toString().trim().equals("");
	}

	/**
	 * 判断Map集合是否为空
	 * 
	 * @param map
	 * @return
	 */
	public final static <T extends Map<?, ?>> boolean isNotEmpty(T map) {
		return map != null && map.size() > 0;
	}

	/**
	 * 判断数字是否大于零
	 * 
	 */
	public static <T extends Number> boolean isGtZero(T ls) {
		return ls != null && ls.doubleValue() > 0;
	}

	/**
	 * 随机count位数字字符串<br/>
	 * 可以用来生成代金券号码和密码
	 * 
	 * @param count
	 *            随机多少位
	 */
	public static String getRandomString(int count) {
		if (count > 0) {
			StringBuilder buf = new StringBuilder();
			SecureRandom rd = new SecureRandom();
			for (int i = 0; i < count; i++) {
				buf.append(rd.nextInt(10));
			}
			return buf.toString();
		} else {
			return "";
		}

	}

	/**
	 * 隐藏字符串中部分敏感信息
	 * 
	 * @param tg
	 *            目标字符串
	 * @param start
	 *            开始索引
	 * @param end
	 *            结束索引
	 * @return
	 */
	public static String hidepartChar(String tg, int start, int end) {
		return new StringBuilder(tg).replace(start, end, "**").toString();
	}

	/**
	 * 判断字符在字符串中出现的次数
	 * 
	 * @param tg
	 * @param fg
	 * @return
	 */
	public static Integer getCountInStr(String tg, char fg) {
		if (isNotEmpty(tg)) {
			int i = 0;
			if (isNotEmpty(tg)) {
				char ch[] = tg.toCharArray();
				for (char c : ch) {
					if (c == fg) {
						i++;
					}
				}
			}
			return i;
		}
		return 0;
	}

	/**
	 * 得到long类型集合 如果遇到不能转换为long类型的字符串跳过 返回能转换为long类型的long类型集合
	 * 
	 * @param strings
	 * @return
	 */
	public static List<Long> getListByStrs(String... strings) {
		List<Long> list = new ArrayList<Long>();
		if (strings != null) {
			for (String s : strings) {
				try {
					if (isNotEmpty(s)) {
						list.add(Long.valueOf(s));
					}
				} catch (NumberFormatException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return list;
	}

	/**
	 * 把首字母变成大写
	 * 
	 * @param str要转换的字符串
	 * @return
	 */
	public static String toUpcaseFist(String str) {
		if (str != null && !str.trim().equals("")) {
			if (str.length() == 1) {
				return str.toUpperCase();
			} else {
				return str.substring(0, 1).toUpperCase() + str.substring(1);
			}
		} else {
			return "";
		}

	}

	/**
	 * 根据键值对得到map<String,Object>对象
	 * 
	 * @param ag
	 * @return
	 */
	public static LinkedHashMap<String, Object> getMap(Object... ag) {
		LinkedHashMap<String, Object> mp = new LinkedHashMap<String, Object>();
		if (ag != null && ag.length > 0 && ag.length % 2 == 0) {
			int i = 0;
			for (@SuppressWarnings("unused")
			Object o : ag) {
				mp.put(String.valueOf(ag[i]), ag[++i]);
				i++;
				if (i == ag.length) {
					break;
				}

			}
		}
		return mp;
	}

	/**
	 * 根据键值对得到map<String,String>对象
	 * 
	 * @param ag
	 * @return
	 */
	public static LinkedHashMap<String, String> getStrValueMap(String... ag) {
		LinkedHashMap<String, String> mp = new LinkedHashMap<String, String>();
		if (ag != null && ag.length > 0 && ag.length % 2 == 0) {
			int i = 0;
			for (@SuppressWarnings("unused")
			String o : ag) {
				mp.put(ag[i], ag[++i]);
				i++;
				if (i == ag.length) {
					break;
				}

			}
		}
		return mp;
	}

	/**
	 * 动态图像转换成静态图片
	 * 
	 * @param file图片文件
	 */
	public static void convertImageToStatic(File file) {
		try {
			BufferedImage bufferedimage = ImageIO.read(file);
			if (bufferedimage != null) {
				ImageIO.write(bufferedimage, "gif", file);// 1131.gif是静态的
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private final static Pattern pt = Pattern.compile("^[1-9]+[0-9]*[.]?\\d*$");

	/**
	 * 判断是否为数字
	 * 
	 * @param tg
	 * @return
	 */
	public static Boolean isNumber(String tg) {
		return tg == null ? false : pt.matcher(tg.trim()).matches();
	}

	/**
	 * 处理浮点数相加运算
	 * 
	 * @param v
	 * @param v2
	 * @return
	 */
	public static Double floatAdd(Double v, Double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.add(b2).doubleValue();
	}

	/**
	 * 处理浮点数相减运算
	 * 
	 * @param v
	 *            被减数
	 * @param v2
	 *            减数
	 * @return
	 */
	public static Double floatSubtract(Double v, Double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.subtract(b2).doubleValue();
	}

	/**
	 * 处理浮点数相除
	 * 
	 * @param v
	 * @param v2
	 * @return
	 */
	public static Double floatDiv(Double v, Double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2).doubleValue();
	}

	/**
	 * 处理浮点数相乘
	 * 
	 * @param v
	 * @param v2
	 * @return
	 */
	public static Double floatMulply(Double v, Double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.multiply(b2).doubleValue();
	}

	/**
	 * 判断集合是否不为空
	 * 
	 * @param list
	 * @return
	 */
	public static <T> Boolean isNotEmpty(Collection<T> list) {
		return list != null && list.size() > 0;
	}

	/**
	 * 判断对象数组是否不为空
	 * 
	 * @param arr
	 * @return
	 */
	public static <T> Boolean isNotEmpty(T[] arr) {
		return arr != null && arr.length > 0;
	}

	/**
	 * 对页面显示内容进行编码
	 * 
	 * @param str
	 * @return
	 */
	public static String htmlEncoding(String str) {
		StringBuffer bfu = new StringBuffer();
		if (str != null) {
			String s = "&#";
			char[] cs = str.toCharArray();
			for (char c : cs) {
				int it = c;
				bfu.append(s).append(it).append(";");
			}
		}
		return bfu.toString();

	}

	/***
	 * 自动属性赋值
	 * 
	 * @param clazz
	 * @param objs
	 * @param propertys
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> packageObject(Class<T> clazz, List<Object> vlist,
			String... propertys) {
		try {
			List<T> list = new ArrayList<T>();
			if (propertys.length == 1) {
				for (Object ov : vlist) {
					Object obj = clazz.newInstance();
					Field fd = clazz.getDeclaredField(propertys[0]);
					fd.setAccessible(true);
					if (ov != null) {

						if (ov instanceof BigDecimal) {
							ov = ((BigDecimal) ov).doubleValue();
						} else if (ov instanceof BigInteger) {
							ov = ((BigInteger) ov).longValue();
						}

						if (fd.getType().isEnum()) {
							Class<Enum> cls = (Class<Enum>) fd.getType();
							if (ov instanceof Number) {
								Enum[] ccs = (Enum[]) fd.getType()
										.getEnumConstants();
								fd.set(obj, Enum.valueOf(cls, ccs[Number.class
										.cast(ov).intValue()].name()));
							} else {
								fd.set(obj, Enum.valueOf(cls, ov.toString()));
							}

						} else {
							fd.set(obj, ov);
						}
					}
					list.add((T) obj);

				}

			} else {
				for (Object o : vlist) {
					Object[] ov = (Object[]) o;
					Object obj = clazz.newInstance();
					for (int i = 0; i < propertys.length; i++) {
						Field fd = clazz.getDeclaredField(propertys[i]);
						fd.setAccessible(true);
						if (ov[i] != null) {
							if (ov[i] instanceof BigDecimal) {
								ov[i] = ((BigDecimal) ov[i]).doubleValue();
							} else if (ov[i] instanceof BigInteger) {
								ov[i] = ((BigInteger) ov[i]).longValue();
							}

							if (fd.getType().isEnum()) {
								Class<Enum> cls = (Class<Enum>) fd.getType();

								if (ov[i] instanceof Number) {

									Enum[] ccs = (Enum[]) fd.getType()
											.getEnumConstants();
									fd.set(obj, Enum.valueOf(cls,
											ccs[Number.class.cast(ov[i])
													.intValue()].name()));

								} else {
									fd.set(obj,
											Enum.valueOf(cls, ov[i].toString()));
								}

							} else {
								fd.set(obj, ov[i]);
							}
						}
					}
					list.add((T) obj);
				}
			}
			return list;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 得到json数据格式
	 * 
	 * @param flag
	 *            obj[key] key数组
	 * @param property
	 *            name,age..对象属性数组
	 * @param values
	 *            fudong,22.对象属性对应的值
	 * @return
	 */
	public static StringBuffer getJson(String[] flag, String[] property,
			List<String[]> values) {
		StringBuffer buf = new StringBuffer();
		if (flag != null && property != null && property.length > 0) {
			if (values != null && values.size() > 0
					&& property.length == values.get(0).length
					&& values.size() == flag.length) {
				Iterator<String[]> ite = values.iterator();
				buf.append("({");
				for (int j = 0; j < flag.length; j++) {
					buf.append("\"").append(flag[j]).append("\"").append(":");
					String[] ss = ite.next();
					buf.append("{");
					for (int i = 0; i < property.length; i++) {
						buf.append(property[i]).append(":").append("\"")
								.append(ss[i]).append("\"");
						if (property.length - 1 > i) {
							buf.append(",");
						}
					}
					buf.append("}");
					if (ite.hasNext()) {
						buf.append(",");
					}
				}
				buf.append("})");
			}

		}
		return buf;
	}

	/**
	 * 生成唯一订单号
	 * 
	 * <p>
	 * MyUtils.genOrderId(10000000)>>>>> 20130806010000000
	 * </p>
	 * 
	 * @param srcId加入生成的数字
	 * @return
	 */
	public final static String genOrderId(long srcId) {
		DateFormat YMDALL = new SimpleDateFormat("yyyyMMdd");
		StringBuilder bd = new StringBuilder(YMDALL.format(new Date()));
		for (int i = 0; i < 6 - String.valueOf(srcId).length(); i++) {
			bd.append("0");
		}
		bd.append(srcId);
		return bd.toString();
	}

	/**
	 * 四舍五入
	 * 
	 * @param v
	 *            待四舍五入的值
	 * @param setPrecision
	 *            保留多少位小数
	 * @return
	 */
	public static double getRoundValue(double v, int setPrecision) {
		return new BigDecimal(Double.toString(v)).setScale(setPrecision,
				BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 切分集合
	 * 
	 * @param list
	 *            要切分的集合
	 * @param size
	 *            最大切分大小
	 */
	public final static <T> List<List<T>> dividedList(List<T> list, int maxsize) {
		List<List<T>> all = new ArrayList<List<T>>();
		if (isNotEmpty(list) && maxsize > 0) {
			List<T> sublist = new ArrayList<T>();
			all.add(sublist);
			if (list.size() > maxsize) {
				for (T t : list) {
					if (sublist.size() >= maxsize) {
						sublist = new ArrayList<T>();
						all.add(sublist);
					}
					sublist.add(t);

				}
			} else {
				for (T t : list) {
					sublist.add(t);
				}
			}
		}
		return all;
	}

	/**
	 * 得到对象属性值列表
	 * 
	 * @param prop
	 *            属性名称
	 * @param ts
	 *            对象集合
	 * @return 返回 集合列表类型和属性类型一致
	 */
	public static <T, V> List<V> getListOVs(String prop, List<T> ts) {
		List<V> list = new ArrayList<V>();
		if (isNotEmpty(ts) && isNotEmpty(prop)) {
			try {
				Method m = ts
						.get(0)
						.getClass()
						.getDeclaredMethod(
								"get" + prop.toUpperCase().charAt(0)
										+ prop.substring(1), null);
				for (T t : ts) {
					list.add((V) m.invoke(t, null));
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return list;
	}

	/**
	 * 得到list中指定字段值的long 类型集合
	 * 
	 * @param prop
	 *            属性名称
	 * @param ts
	 *            对象集合
	 * @return
	 */
	public static <T> List<Long> getIds(String prop, List<T> ts) {
		List<Long> list = new ArrayList<Long>();
		if (isNotEmpty(ts) && isNotEmpty(prop)) {
			try {
				Field fd = ts.get(0).getClass().getDeclaredField(prop);
				Method m = ts
						.get(0)
						.getClass()
						.getDeclaredMethod(
								"get" + prop.toUpperCase().charAt(0)
										+ prop.substring(1), null);
				if (fd.getType() == Long.class) {
					for (T t : ts) {
						list.add((Long) m.invoke(t, null));
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return list;
	}

	/***
	 * 得到对象属性字符串的集合
	 * 
	 * @param prop
	 *            对象属性名称
	 * @param ts
	 *            对象集合
	 * @return
	 */
	public static <T> List<String> getStrVs(String prop, List<T> ts) {
		List<String> tts = new ArrayList<String>();
		if (isNotEmpty(ts) && isNotEmpty(prop)) {
			try {
				Field fd = ts.get(0).getClass().getDeclaredField(prop);
				Method m = ts
						.get(0)
						.getClass()
						.getDeclaredMethod(
								"get" + prop.toUpperCase().charAt(0)
										+ prop.substring(1), null);
				if (fd.getType() == String.class) {
					for (T t : ts) {
						tts.add((String) m.invoke(t, null));
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return tts;
	}
}
