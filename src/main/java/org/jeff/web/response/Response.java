package org.jeff.web.response;


import java.util.HashMap;

public interface Response
{
    Response set_version(String version);
    Response set_status(int status);
    Response set_status(int status, String body);
    Response set_header(String name, String value);
    Response only_header(boolean v);
    Response write(String text);
    Response set_cookie(String name, String value);
    Response set_cookie(String name, String value, String expires);
    Response set_cookie(String name, String value, int max_age);
    Response set_cookie(String name, String value, String path, String expires);
    Response set_cookie(String name, String value, String path, int max_age);
    Response set_cookie(String name, String value, String path, String domain, String expires);
    Response set_cookie(String name, String value, String path, String domain, int max_age);
}
