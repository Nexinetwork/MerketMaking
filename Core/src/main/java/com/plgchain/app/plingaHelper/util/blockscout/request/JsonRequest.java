/**
 *
 */
package com.plgchain.app.plingaHelper.util.blockscout.request;

import java.io.Serializable;
import org.json.JSONArray;
import org.json.JSONObject;

public class JsonRequest implements Serializable{
    private static final long serialVersionUID = -5838243711973341916L;
	private int id;
    private String jsonrpc;
    private String method;
    private JSONArray params;

    public JsonRequest(int id, String jsonrpc, String method, Object[] params) {
        this.id = id;
        this.jsonrpc = jsonrpc;
        this.method = method;
        this.params = new JSONArray(params);
    }

    public String toJsonString() {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("jsonrpc", jsonrpc);
        json.put("method", method);
        json.put("params", params);

        return json.toString();
    }
}
