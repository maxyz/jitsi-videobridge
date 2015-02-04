/*
 * Jitsi Videobridge, OpenSource video conferencing.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jitsi.videobridge.metrics;

import java.util.*;

import org.ice4j.ice.*;
import org.jitsi.service.configuration.*;
import org.jitsi.util.*;
import org.jitsi.videobridge.*;
import org.jitsi.videobridge.log.*;

/**
 * A generic service interface to push metrics to cloud based collector servers.
 *
 * Service publishers can be enabled with environment variables (e.g.
 * sip-communicator file) with prefix: "org.jitsi.videobridge.metricservice.".
 * Publisher instances must implement
 * <tt>MetricServicePublisher</tt> interface. It's not necessary to implement
 * all publish methods. Methods not implemented will simply be ignored (though
 * metric will not be published)
 *
 * Current metric entry points (metrics):
 * <ul>
 * <li>Create and expire conference</li>
 * <li>Create and expire channel</li>
 * <li>Conference length</li>
 * <li>Channel connection (incl. remote IP address)</li>
 * </ul>
 *
 * @author zbettenbuk
 */
public class MetricService
    implements LoggingService
{

    private static final Logger logger = Logger.getLogger(MetricService.class);
    private List<MetricServicePublisher> publishers;

    public static final String METRIC_CONFERENCES = "Conferences";
    public static final String METRIC_CHANNELS = "Channels";
    public static final String METRIC_CONFERENCELENGTH = "Conference length";
    public static final String METRIC_CHANNELSTART_POSTFIX = " start";

    /**
     * The name of the property which specifies whether metrics logging is
     * enabled.
     */
    public static final String ENABLED_PNAME
        = "org.jitsi.videobridge.metrics.METRICS_ENABLED";

    public MetricService(ConfigurationService config)
    {
        this.publishers = new LinkedList<MetricServicePublisher>();
        List<String> propNames
            = config.getPropertyNamesByPrefix(
                    "org.jitsi.videobridge.metricservice.",
                    false);
        logger.info("Metric services enabled: "
                        + (propNames == null ? "0" : propNames.size()));
        if (propNames != null)
        {
            for (String propName : propNames)
            {
                logger.info("Initialising metric service: " + propName);
                try
                {
                    String serviceClassName = config.getString(propName);
                    Class<?> serviceClass = Class.forName(serviceClassName);
                    MetricServicePublisher publisher
                        = (MetricServicePublisher) serviceClass.getConstructor()
                            .newInstance();
                    this.publishers.add(publisher);
                }
                catch (Throwable t)
                {
                    logger.error("Error initialising metric service", t);
                }
            }
        }
    }

    /**
     * Method to publish numeric type metrics
     *
     * @param metricName Name of the metric
     * @param metricValue Value of the metric
     */
    private void publishNumericMetric(String metricName, int metricValue)
    {
        for (MetricServicePublisher publisher : this.publishers)
        {
            try
            {
                publisher.publishNumericMetric(metricName, metricValue);
            }
            catch (UnsupportedOperationException e)
            {
                if (logger.isDebugEnabled())
                    logger.debug(publisher.getName()
                        + " publisher doesn't support numeric metric: "
                        + metricName);
            }
            catch (Throwable t)
            {
                logger.error("Error publishing metric \"" + metricName
                             + "\" with publisher: " + publisher.getName(), t);
            }
        }
    }

    /**
     * Method to publish string type metrics
     *
     * @param metricName Name of the metric
     * @param metricValue Value of the metric
     */
    private void publishStringMetric(String metricName, String metricValue)
    {
        for (MetricServicePublisher publisher : this.publishers)
        {
            try
            {
                publisher.publishStringMetric(metricName, metricValue);
            }
            catch (UnsupportedOperationException e)
            {
                logger.debug(publisher.getName()
                             + " publisher doesn't support string metric: "
                             + metricName);
            }
            catch (Throwable t)
            {
                logger.error("Error publishing metric \"" + metricName
                             + "\" with publisher: " + publisher.getName(), t);
            }
        }
    }

    /**
     * Method to publish an incremental metric. Metric value with
     * <tt>metricName</tt> will be increased by 1.
     *
     * @param metricName Name of the metric
     */
    private void publishIncrementalMetric(String metricName)
    {
        for (MetricServicePublisher publisher : this.publishers)
        {
            try
            {
                publisher.publishIncrementalMetric(metricName);
            }
            catch (UnsupportedOperationException e)
            {
                if (logger.isDebugEnabled())
                    logger.debug(publisher.getName()
                        + " publisher doesn't support incremental metric: "
                        + metricName);

            }
            catch (Throwable t)
            {
                logger.error("Error publishing metric \"" + metricName
                             + "\" with publisher: " + publisher.getName(), t);
            }
        }
    }

    /**
     * Method to publish an incremental metric. Metric value with
     * <tt>metricName</tt> will be increased by <tt>metricName</tt>.
     *
     * @param metricName Name of the metric
     * @param increment Value to increase the metric with
     */
    private void publishIncrementalMetric(String metricName, int increment)
    {
        for (MetricServicePublisher publisher : this.publishers)
        {
            try
            {
                publisher.publishIncrementalMetric(metricName, increment);
            }
            catch (UnsupportedOperationException e)
            {
                logger.debug(publisher.getName()
                             + " publisher doesn't support incremental metric: "
                             + metricName);
            }
            catch (Throwable t)
            {
                logger.error("Error publishing metric \"" + metricName
                             + "\" with publisher: " + publisher.getName(), t);
            }
        }
    }

    /**
     * Records the start of a transaction to have the length published later.
     *
     * @param transactionType Type of the transaction (e.g. create conference)
     * @param transactionId Unique id of the transaction (e.g. conference ID)
     */
    private void startMeasuredTransaction(String transactionType,
                                         String transactionId)
    {
        for (MetricServicePublisher publisher : this.publishers)
        {
            try
            {
                publisher.startMeasuredTransaction(transactionType, transactionId);
            }
            catch (UnsupportedOperationException e)
            {
                if (logger.isDebugEnabled())
                    logger.debug(publisher.getName()
                        + " publisher doesn't support measured transaction "
                        + "metric: " + transactionType);
            }
            catch (Throwable t)
            {
                logger.error("Error publishing metric \"" + transactionType
                             + "\" with publisher: " + publisher.getName(), t);
            }
        }
    }

    /**
     * Records the finish of a transaction and publishes length metric
     *
     * @param transactionType Type of the transaction (e.g. create conference)
     * @param transactionId Unique id of the transaction (e.g. conference ID)
     */
    private void endMeasuredTransaction(String transactionType,
                                       String transactionId)
    {
        for (MetricServicePublisher publisher : this.publishers)
        {
            try
            {
                publisher.endMeasuredTransaction(transactionType, transactionId);
            }
            catch (UnsupportedOperationException e)
            {
                logger.debug(publisher.getName()
                    + " publisher doesn't support measured transaction metric: "
                    + transactionType);
            }
            catch (Throwable t)
            {
                logger.error("Error publishing metric \"" + transactionType
                             + "\" with publisher: " + publisher.getName(), t);
            }
        }
    }

    @Override
    public void logEvent(Event event)
    {
        // Ignore.
    }

    @Override
    public void conferenceCreated(Conference conference)
    {
        int sz;
        try
        {
            sz = getConferenceCount(conference);
        }
        catch (Exception e)
        {
            logger.debug("Could not log conference expired event because " +
                "the conference is null.");
            return;
        }

        publishNumericMetric(MetricService.METRIC_CONFERENCES, sz);

        startMeasuredTransaction(MetricService.METRIC_CONFERENCELENGTH,
            conference.getID());
    }

    int getConferenceCount(Conference conference)
    {
        if (conference == null)
        {
            throw new IllegalArgumentException("conference");
        }

        Videobridge videobridge = conference.getVideobridge();
        if (videobridge == null)
        {
            throw new NullPointerException("videobridge");
        }

        // XXX(gp) This method is called after the conference expired field has
        // been set to true but *before* the conference has been removed from
        // the conference list of the videobridge object.
        //
        // In the following loop we accurately calculate the active conference
        // count by checking whether or not the conference expired flag has been
        // set.

        int sz = 0;

        Conference[] conferences = videobridge.getConferences();
        if (conferences != null && conferences.length != 0)
        {
            for (Conference c : conferences)
            {
                if (c != null && !c.isExpired())
                {
                    sz++;
                }
            }
        }

        return sz;
    }

    @Override
    public void conferenceExpired(Conference conference)
    {
        int sz;
        try
        {
            sz =getConferenceCount(conference);
        }
        catch (Exception e)
        {
            logger.debug("Could not log conference expired event because " +
                "the conference is null.");
            return;
        }

        publishNumericMetric(MetricService.METRIC_CONFERENCES, sz);

        endMeasuredTransaction(
                MetricService.METRIC_CONFERENCELENGTH,
                conference.getID());
    }

    @Override
    public void endpointCreated(Endpoint endpoint)
    {

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
        int channelCount = getChannelCount(channel);
        publishNumericMetric(MetricService.METRIC_CHANNELS, channelCount);
    }

    @Override
    public void channelExpired(Channel channel)
    {
        int channelCount = getChannelCount(channel);
        publishNumericMetric(MetricService.METRIC_CHANNELS, channelCount);
    }

    private int getChannelCount(Channel channel)
    {
        int channelCount = 0;
        if (channel == null)
        {
            logger.debug("Could not log channel expired event because " +
                "the channel is null.");
            return channelCount;
        }

        Content content = channel.getContent();
        if (content == null)
        {
            logger.debug("Could not log channel expired event because the " +
                "content is null.");
            return channelCount;
        }

        Conference conference = content.getConference();
        if (conference == null)
        {
            logger.debug("Could not log channel expired event because the " +
                "conference is null.");
            return channelCount;
        }

        Videobridge videobridge = conference.getVideobridge();
        if (videobridge == null)
        {
            logger.debug("Could not log channel expired event because the " +
                "videobridge is null.");
            return channelCount;
        }

        // XXX(gp) This method is called after the conference expired field has
        // been set to true but *before* the conference has been removed from
        // the conference list of the videobridge object.
        //
        // In the following loop we accurately calculate the active conference
        // count by checking whether or not the conference expired flag has been
        // set.

        for (Conference c : videobridge.getConferences())
        {
            if (c == null || c.isExpired())
            {
                continue;
            }

            for (Content cc : conference.getContents())
            {
                if (cc == null || cc.isExpired())
                {
                    continue;
                }

                for (Channel ccc : cc.getChannels())
                {
                    if (ccc == null || ccc.isExpired())
                    {
                        continue;
                    }

                    channelCount++;
                }
            }
        }
        return channelCount;
    }

    @Override
    public void channelStartedStreaming(RtpChannel rtpChannel)
    {
        if (rtpChannel == null)
        {
            logger.debug("Could not log channelStartedStreaming event because " +
                "the rtpChannel is null.");
            return;
        }

        publishStringMetric(
            getClass().getName()
                + MetricService.METRIC_CHANNELSTART_POSTFIX,
            rtpChannel.getStreamTarget().getDataAddress().getHostAddress());
    }
}
