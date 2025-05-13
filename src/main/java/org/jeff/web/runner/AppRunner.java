package org.jeff.web.runner;

import org.jeff.web.Application;
import org.jeff.web.router.RouterChooser;

public class AppRunner extends BaseRunner
{
    public AppRunner(Application application)
    {
        super(application, new RouterChooser());
    }

    public AppRunner(Application application, RouterChooser router)
    {
        super(application, router);
    }

    @Override
    public void setup()
    {

    }

}
