/**
 *
 */
package com.plgchain.app.plingaHelper.util;

import java.io.Serializable;
import java.util.ArrayList;
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

	public static <T> List<List<T>> chunkList(List<T> list, int chunkSize) {
        List<List<T>> chunks = new ArrayList<>();

        for (int i = 0; i < list.size(); i += chunkSize) {
            int end = Math.min(i + chunkSize, list.size());
            chunks.add(list.subList(i, end));
        }

        return chunks;
    }

}
