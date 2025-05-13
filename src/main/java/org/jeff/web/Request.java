package org.jeff.web;

import java.util.HashMap;
import java.util.List;

public class Request
{
    public HashMap<String, String> headers = new HashMap<>();
    public HashMap<String, String> cookies = new HashMap<>();
    public HashMap<String, List<Object>> params = new HashMap<>();
    public String method;
    public String path;
    public String version;

    public String get_header(String name)
    {
        return headers.getOrDefault(name.toUpperCase(), null);
    }

    public String get_cookie(String name)
    {
        return cookies.getOrDefault(name, null);
    }

    public Object get_argument(String name)
    {
        if(params.containsKey(name)) return params.get(name).get(0);
        return null;
    }

    public String toString()
    {
        return String.format("%s %s %s\r\n%s\r\n", method, path, version, params.toString());
    }
}
