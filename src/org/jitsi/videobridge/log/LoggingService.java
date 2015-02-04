package org.jitsi.videobridge.log;

import org.ice4j.ice.*;
import org.jitsi.videobridge.*;

/**
 * Allows logging of {@link org.jitsi.videobridge.log.Event}s.
 *
 * FIXME(gp) A more long-term solution should be loggers (influx-db, newrelic,
 * etc) to listen for events and react appropriately. Because it would be
 * inconvinient for the observer to observe newly created channels, we could
 * have an <tt>EventManager</tt> that would route events to interested
 * observers. Something like PubSub but for internal usage.
 *
 * @author Boris Grozev
 * @author George Politis
 */
public interface LoggingService
{
    public void logEvent(Event event);

    /**
     * Notifies the <tt>LoggingService</tt> that a new COLIBRI conference has
     * been created.
     *
     * @param conference the newly created COLIBRI conference.
     */
    void conferenceCreated(Conference conference);

    /**
     * Notifies the <tt>LoggingService</tt> about the expiry of a COLIBRI
     * conference.
     *
     * @param conference the expired COLIBRI conference.
     */
    void conferenceExpired(Conference conference);

    /**
     * Notifies the <tt>LoggingService that a COLIBRI endpoint has been created.
     *
     * @param endpoint the newly created COLIBRI endpoint.
     *
     */
    void endpointCreated(Endpoint endpoint);

    /**
     * Notifies the <tt>LoggingService</tt> that an endpoint has changed its
     * display name.
     *
     * @param endpoint the endpoint that changed its display name.
     */
    void endpointDisplayNameChanged(Endpoint endpoint);

    /**
     * Notifies the <tt>LoggingService</tt> that a new COLIBRI content has been
     * created.
     *
     * @param content the newly created COLIBRI content.
     */
    void contentCreated(Content content);

    /**
     * Notifies the <tt>LoggingService</tt> that a COLIBRI content has been
     * expired.
     *
     * @param content the expired COLIBRI content.
     */
    void contentExpired(Content content);

    /**
     * Notifies the <tt>LoggingService</tt> that a COLIBRI channel was added to
     * a Jitsi Videobridge TransportManager.
     *
     * @param channel the added COLIBRI channel.
     */
    void transportChannelAdded(Channel channel);

    /**
     * Notifies the <tt>LoggingService</tt> that a COLIBRI channel was removed
     * from a Jitsi Videobridge TransportManager.
     *
     * @param channel the removed COLIBRI channel.
     */
    void transportChannelRemoved(Channel channel);

    /**
     * Notifies the <tt>LoggingService</tt> that a Jitsi Videobridge
     * TransportManager has changed its state.
     *
     * @param iceUdpTransportManager the <tt>IceUdpTransportManager</tt> whose
     * state has changed.
     * @param oldState the old ICE state.
     * @param newState the new ICE state.
     *
     */
    void transportStateChanged(IceUdpTransportManager iceUdpTransportManager,
                               IceProcessingState oldState,
                               IceProcessingState newState);

    /**
     * Notifies the <tt>LoggingService</tt> that a new Jitsi Videobridge
     * TransportManager has been created.
     *
     * @param iceUdpTransportManager the newly created TransportManager.
     *
     */
    void transportCreated(IceUdpTransportManager iceUdpTransportManager);

    /**
     * Notifies the <tt>LoggingService</tt> that a Jitsi Videobridge
     * TransportManager has changed its state to connected.
     *
     * @param iceUdpTransportManager the TransportManager that changed its state
     * to connected.
     */
    void transportConnected(IceUdpTransportManager iceUdpTransportManager);

    /**
     * Notifies the <tt>LoggingService</tt> that a new COLIBRI channel has been
     * created.
     *
     * @param channel The newly created COLIBRI channel.
     *
     */
    void channelCreated(RtpChannel channel);

    /**
     * Notifies the <tt>LoggingService</tt> that a COLIBRI channel has expired.
     *
     * @param channel The expired COLIBRI channel.
     *
     */
    void channelExpired(Channel channel);

    /**
     * Notifies the <tt>LoggingService</tt> that a COLIBRI channel has started
     * streaming.
     *
     * @param rtpChannel The expired COLIBRI channel.
     *
     */
    void channelStartedStreaming(RtpChannel rtpChannel);
}
