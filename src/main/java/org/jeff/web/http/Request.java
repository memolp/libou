package org.jeff.web.http;

import java.util.HashMap;
import java.util.List;

public class Request
{
    public HashMap<String, String> headers = new HashMap<>();
    public HashMap<String, String> cookies = new HashMap<>();
    public HashMap<String, List<String>> params = new HashMap<>();
    public String method;
    public String path;
    public String version;

    public String get_header(String name)
    {
        return headers.getOrDefault(name.toUpperCase(), null);
    }
}
