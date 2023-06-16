/**
 *
 */
package com.plgchain.app.plingaHelper.util;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;

/**
 *
 */
public class ArrayListHelper implements Serializable {

	private static final long serialVersionUID = 5326130269915003835L;

	public static <T> List<T> parseJsonToArrayList(String jsonString, Class<T> clazz) {
        JSONArray jsonArray = JSON.parseArray(jsonString);
        return jsonArray.toJavaList(clazz);
    }

}
