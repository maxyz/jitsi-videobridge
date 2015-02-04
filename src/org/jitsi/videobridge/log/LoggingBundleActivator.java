/*
 * Jitsi Videobridge, OpenSource video conferencing.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jitsi.videobridge.log;

import org.ice4j.ice.*;
import org.jitsi.service.configuration.*;
import org.jitsi.videobridge.*;
import org.jitsi.videobridge.metrics.*;
import org.jitsi.videobridge.osgi.*;
import org.osgi.framework.*;

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
    private InfluxDBLoggingService loggingService;

    /**
     * The <tt>MetricService</tt> instance in use.
     */
    private MetricService metricService;

    private ServiceRegistration<LoggingService> serviceRegistration;

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

        boolean enabled = false;

        if (cfg.getBoolean(InfluxDBLoggingService.ENABLED_PNAME, false))
        {
            enabled = true;
            loggingService = new InfluxDBLoggingService(cfg);
        }

        else if (cfg.getBoolean(MetricService.ENABLED_PNAME, false))
        {
            enabled = true;
            metricService = new MetricService(cfg);
        }

        if (enabled)
        {
            serviceRegistration
                = bundleContext.registerService(
                LoggingService.class, new LoggingService()
                {
                    @Override
                    public void logEvent(Event event)
                    {
                        loggingService.logEvent(event);
                        metricService.logEvent(event);
                    }

                    @Override
                    public void conferenceCreated(Conference conference)
                    {
                        // Do the same as above.. after PR review.
                    }

                    @Override
                    public void conferenceExpired(Conference conference)
                    {
                        // same..
                    }

                    @Override
                    public void endpointCreated(Endpoint endpoint)
                    {
                        // ...
                    }

                    @Override
                    public void endpointDisplayNameChanged(Endpoint endpoint)
                    {

                    }

                    @Override
                    public void contentCreated(Content content)
                    {

                    }

                    @Override
                    public void contentExpired(Content content)
                    {

                    }

                    @Override
                    public void transportChannelAdded(Channel channel)
                    {

                    }

                    @Override
                    public void transportChannelRemoved(Channel channel)
                    {

                    }

                    @Override
                    public void transportStateChanged(IceUdpTransportManager iceUdpTransportManager, IceProcessingState oldState, IceProcessingState newState)
                    {

                    }

                    @Override
                    public void transportCreated(IceUdpTransportManager iceUdpTransportManager)
                    {

                    }

                    @Override
                    public void transportConnected(IceUdpTransportManager iceUdpTransportManager)
                    {

                    }

                    @Override
                    public void channelCreated(RtpChannel channel)
                    {

                    }

                    @Override
                    public void channelExpired(Channel channel)
                    {

                    }

                    @Override
                    public void channelStartedStreaming(RtpChannel rtpChannel)
                    {

                    }
                }, null);
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
