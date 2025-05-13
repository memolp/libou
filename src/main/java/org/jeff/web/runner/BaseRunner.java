package org.jeff.web.runner;

import org.jeff.web.Application;
import org.jeff.web.router.RouterChooser;

public class BaseRunner
{
    public RouterChooser router;
    public Application application;

    public BaseRunner(Application application, RouterChooser router)
    {
        this.application = application;
        this.router = router;
    }

    public void setup()
    {

    }

}
