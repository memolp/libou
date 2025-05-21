package org.jeff.web;

import org.jeff.web.router.RouterChooser;

public class HttpContext
{
    public RouterChooser chooser;
    public Application application;
    public int buffSize = 1024 * 1024 * 5;
}
