/**
 *
 */
package com.plgchain.app.plingaHelper.util;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

/**
 * @author eae966
 *
 */
public class PlingaStringUtil implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1081612646689816720L;

	/**
	 *
	 */
	public PlingaStringUtil() {
		// TODO Auto-generated constructor stub
	}

	public static boolean isFirstCharacterPesian(String str) {
		char firstChar = str.charAt(0);
		return ((firstChar >= 0x0080) && (firstChar <= 0xFFFF));
	}

	public static boolean doesHavePersianCharacter(String str) {
		boolean result = false;
		for (int i = 0; i < str.length(); i++) {
			result = ((str.charAt(i) >= 0x0080) && (str.charAt(i) <= 0xFFFF));
			if (result)
				return result;
		}
		return result;
	}

	public static String getListStringAsString(List<String> strList) {
		String strResult = "";
		for (String str : strList)
			strResult += str + ",";
		return removeLastChar(strResult);
	}

	public static String getListObjectAsString(List<Object> strList) {
		String strResult = "";
		for (Object str : strList)
			strResult += str + ",";
		return removeLastChar(strResult);
	}

	public static String getListIntegerAsString(List<Integer> strList) {
		String strResult = "";
		for (Integer str : strList)
			strResult += String.valueOf(str) + ",";
		return removeLastChar(strResult);
	}

	public static String removeFirstChar(String s) {
		if (s.isEmpty())
			return s;
		else
			return s.substring(1);
	}

	public static String removeLastChar(String s) {
		if (s.isEmpty())
			return s;
		else
			return s.substring(0, s.length() - 1);
	}

	public static String chop(String s) {
		return removeLastChar(s);
	}

	public static String lChop(String s) {
		return removeFirstChar(s);
	}

	public static char getLastChar(String s) {
		if (s.isEmpty())
			return '\u0000';
		else
			return s.charAt(s.length() - 1);
	}

	public static char getFirstChar(String s) {
		if (s.isEmpty())
			return '\u0000';
		else
			return s.charAt(0);
	}

	public static String chomp(String s) {
		if (s.isEmpty())
			return s;
		else {
			if (getLastChar(s) == '\n')
				return chop(s);
			return s;
		}
	}

	public static String lChomp(String s) {
		if (s.isEmpty())
			return s;
		else {
			if (getFirstChar(s) == '\n')
				return lChop(s);
			return s;
		}
	}

	public static String twoSideChop(String s) {
		return chop(lChop(s));
	}

	public static String twoSideChomp(String s) {
		if (s.isEmpty())
			return s;
		else
			return chomp(lChomp(s));
	}

	public static String addCharToString(String str, char ch, int pos) {
		if (str.isEmpty())
			return str;
		else if (ch == '\u0000')
			return str;
		else if (pos < 0)
			return str;
		else if (pos >= str.length())
			return (str + ch);
		else if (pos == 0)
			return (ch + str);
		else
			return (str.substring(0, pos) + ch + str.substring(pos, str.length()));

	}

	public static String addCharToFirstPosOfString(String str, char ch) {
		return addCharToString(str, ch, 0);

	}

	public static String addCharToLastPosOfString(String str, char ch) {
		return addCharToString(str, ch, str.length());

	}

	public static String fillStringFromFirstToFixSize(String str, char ch, int size) {
		String tmpStr = "";
		for (int i = 0; i < (size - str.length()); i++)
			tmpStr += ch;
		return tmpStr + str;
	}

	public static String fillStringFromFirstToFixSize(int num, char ch, int size) {
		return fillStringFromFirstToFixSize(String.valueOf(num), ch, size);
	}

	public static Map<String, String> getStringAsHashMap(String str) {
		Map<String, String> strMap = new HashMap<String, String>();
		String seprator = "";
		if (str.contains("\r\n"))
			seprator = "\r\n";
		else if (str.contains("\r"))
			seprator = "\r";
		else if (str.contains("\n"))
			seprator = "\n";
		String[] lineStr = str.split(seprator);
		for (String strLineTmp : lineStr) {
			String[] items = strLineTmp.split("=");
			if (items.length == 2)
				strMap.put(items[0], items[1]);
		}
		return strMap;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String getHashMapAsString(Map strMap) {
		String str = "";
		if (strMap == null)
			return str;
		if (strMap.isEmpty())
			return str;
		Set<Object> keys = strMap.keySet();
		for (Object key : keys) {
			if (strMap.get(key) instanceof String)
				str += key + "=" + strMap.get(key) + "\r\n";
			else if (strMap.get(key) instanceof Character)
				str += key + "=" + strMap.get(key) + "\r\n";
			else if (strMap.get(key) instanceof Long)
				str += key + "=" + strMap.get(key) + "\r\n";
			else if (strMap.get(key) instanceof Integer)
				str += key + "=" + strMap.get(key) + "\r\n";
			else if (strMap.get(key) instanceof BigDecimal)
				str += key + "=" + strMap.get(key) + "\r\n";
			else if (strMap.get(key) instanceof Boolean)
				str += key + "=" + strMap.get(key) + "\r\n";
			else if (strMap.get(key) instanceof Double)
				str += key + "=" + strMap.get(key) + "\r\n";
			else if (strMap.get(key) instanceof Float)
				str += key + "=" + strMap.get(key) + "\r\n";
			else if (strMap.get(key) instanceof Date)
				str += key + "=" + strMap.get(key) + "\r\n";
			else if (strMap.get(key) instanceof Timestamp)
				str += key + "=" + strMap.get(key) + "\r\n";
			else if (strMap.get(key) instanceof Calendar)
				str += key + "=" + strMap.get(key) + "\r\n";
			else if (strMap.get(key) instanceof DateTime)
				str += key + "=" + strMap.get(key) + "\r\n";
			else
				str += key + "=" + getListObjectAsString((List<Object>) strMap.get(key)) + "\r\n";
		}
		return str;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map getStringAsHashMapList(String str) {
		Map strMap = new HashMap();
		if (str == null)
			return strMap;
		if (str.isEmpty())
			return strMap;
		String[] lineStr;
		if (str.contains("\r\n"))
			lineStr = str.split("\r\n");
		else
			lineStr = str.split("\n");
		for (String strLineTmp : lineStr) {
			String[] items = strLineTmp.split("=");
			if (items.length == 2) {
				if (items[1].contains(",")) {
					String[] strListArray = items[1].split(",");
					for (int i = 0; i < strListArray.length; i++)
						strListArray[i] = strListArray[i].trim();
					List<String> arrList = new ArrayList(Arrays.asList(strListArray));
					strMap.put(items[0], arrList);
				} else {
					strMap.put(items[0], items[1]);
				}
			}
		}
		return strMap;
	}

	public static String substituteWords(String str, Map<String, String> keys) {
		Pattern pattern = Pattern.compile("\\$\\{([a-zA-Z1-9-_]+)}");
		Matcher matcher = pattern.matcher(str);
		StringBuffer buffer = new StringBuffer();
		while (matcher.find()) {
			if (keys.containsKey(matcher.group(1))) {
				String replacement = keys.get(matcher.group(1));
				// quote to work properly with $ and {,} signs
				matcher.appendReplacement(buffer, replacement != null ? Matcher.quoteReplacement(replacement) : "null");
			}
		}
		matcher.appendTail(buffer);
		return buffer.toString();
	}

	public static Map<String, List<String>> getQueryParams(String url) throws UnsupportedEncodingException {
		Map<String, List<String>> params = new HashMap<String, List<String>>();
		String query = "";
		String[] urlParts = url.split("\\?");
		if (urlParts.length < 2)
			query = urlParts[0];
		else
			query = urlParts[1];
		for (String param : query.split("&")) {
			String[] pair = param.split("=");
			String key = URLDecoder.decode(pair[0], "UTF-8");
			String value = "";
			if (pair.length > 1) {
				value = URLDecoder.decode(pair[1], "UTF-8");
			}

			// skip ?& and &&
			if ("".equals(key) && pair.length == 1) {
				continue;
			}

			List<String> values = params.get(key);
			if (values == null) {
				values = new ArrayList<String>();
				params.put(key, values);
			}
			values.add(value);
		}

		return params;
	}

	public static String upperCaseFirst(String value) {

		// Convert String to char array.
		char[] array = value.toCharArray();
		// Modify first element in array.
		array[0] = Character.toUpperCase(array[0]);
		// Return string.
		return new String(array);
	}

	public static boolean isAlphabetical(String str) {
		char[] chars = str.toCharArray();

		for (char c : chars) {
			if (!Character.isLetter(c) && !Character.isWhitespace(c)) {
				return false;
			}
		}

		return true;
	}

	public static List<String> getStringAsList(String str) {
		String sep = "";
		if (str.contains("\r\n"))
			sep = "\r\n";
		else if (str.contains("\n\r"))
			sep = "\n\r";
		else if (str.contains("\r"))
			sep = "\r";
		else if (str.contains("\n"))
			sep = "\n";
		else if (str.contains("\t"))
			sep = "\t";
		else
			sep = ",";
		String[] strArray = str.split(sep);
		List<String> arrList = new ArrayList<String>();
		for (String element : strArray) {
			arrList.add(element.trim());
		}
		return arrList;
	}

	public static List<Integer> getStringAsIntegerList(String str) {
		String sep = "";
		if (str.contains("\r\n"))
			sep = "\r\n";
		else if (str.contains("\n\r"))
			sep = "\n\r";
		else if (str.contains("\r"))
			sep = "\r";
		else if (str.contains("\n"))
			sep = "\n";
		else if (str.contains("\t"))
			sep = "\t";
		else
			sep = ",";
		String[] strArray = str.split(sep);
		List<Integer> arrList = new ArrayList<Integer>();
		for (String element : strArray) {
			arrList.add(Integer.valueOf(element.trim()));
		}
		return arrList;
	}

	public static List<BigDecimal> getStringAsBigDecimalList(String str) {
		String sep = "";
		if (str.contains("\r\n"))
			sep = "\r\n";
		else if (str.contains("\n\r"))
			sep = "\n\r";
		else if (str.contains("\r"))
			sep = "\r";
		else if (str.contains("\n"))
			sep = "\n";
		else if (str.contains("\t"))
			sep = "\t";
		else
			sep = ",";
		String[] strArray = str.split(sep);
		List<BigDecimal> arrList = new ArrayList<BigDecimal>();
		for (String element : strArray) {
			arrList.add(new BigDecimal(element.trim()));
		}
		return arrList;
	}

	public static boolean isFirstCharacterAlphanumeric(String str) {
		return StringUtils.isAlphanumeric(String.valueOf(getFirstChar(str)));
	}

	public static String createCsvFromList(List<String> str) {
		String result = "";
		for (String strTmp : str)
			result += String.format("\"%s\",", strTmp);
		return removeLastChar(result);
	}



	public static String getListAsClob(List<String> strList) {
		String str = "";
		for (String strTmp : strList) {
			str += strTmp + "\n";
		}
		return str;
	}

	public static boolean compareStringIgnoreAllThings(String s1,String s2) {
		if (s1 == null)
			return false;
		if (s2 == null)
			return false;
		s1 = s1.trim();
		s2 = s2.trim();
		s1 = s1.replaceAll("\\s+","");
		s2 = s2.replaceAll("\\s+","");
		s1 = s1.replaceAll("\n","");
		s2 = s2.replaceAll("\n","");
		s1 = s1.replaceAll("\r","");
		s2 = s2.replaceAll("\r","");
		s1 = s1.replaceAll("<br>","");
		s2 = s2.replaceAll("<br>","");
		s1 = s1.replaceAll("<br/>","");
		s2 = s2.replaceAll("<br/>","");
		return s1.equalsIgnoreCase(s2);
	}

}
