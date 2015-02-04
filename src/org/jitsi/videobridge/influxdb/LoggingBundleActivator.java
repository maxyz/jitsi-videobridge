/*
 * Jitsi Videobridge, OpenSource video conferencing.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jitsi.videobridge.influxdb;

import org.jitsi.service.configuration.*;
import org.jitsi.videobridge.osgi.*;
import org.osgi.framework.*;
import org.osgi.service.event.*;

import java.beans.*;
import java.beans.EventHandler;
import java.util.*;

/**
 * Implements a <tt>BundleActivator</tt> for <tt>LoggingService</tt>.
 *
 * @author Boris Grozev
 */
public class LoggingBundleActivator
        implements BundleActivator
{
    /**
     * The <tt>LoggingService</tt> instance in use.
     */
    private InfluxDBLoggingHandler loggingHandler;

    private ServiceRegistration<EventHandler> serviceRegistration;

    /**
     * Initializes a <tt>LoggingService</tt>.
     * @param bundleContext the <tt>bundleContext</tt> to use.
     * @throws Exception
     */
    @Override
    public void start(BundleContext bundleContext)
        throws Exception
    {
        ConfigurationService cfg =
            ServiceUtils2.getService(
                    bundleContext,
                    ConfigurationService.class);

        if (cfg.getBoolean(InfluxDBLoggingHandler.ENABLED_PNAME, false))
        {
            String[] topics = new String[] {
                "org/jitsi/*"
            };

            Dictionary props = new Hashtable();
            props.put(EventConstants.EVENT_TOPIC, topics);

            loggingHandler = new InfluxDBLoggingHandler(cfg);

            serviceRegistration
                = bundleContext.registerService(
                InfluxDBLoggingHandler.class, loggingHandler, props);
        }
    }

    /**
     * Removes the previously initialized <tt>LoggingService</tt> instance
     * from <tt>bundleContext</tt>.
     * @param bundleContext the <tt>bundleContext</tt> to use.
     * @throws Exception
     */
    @Override
    public void stop(BundleContext bundleContext)
        throws Exception
    {
        if (serviceRegistration != null)
        {
            serviceRegistration.unregister();
        }

    }
}
