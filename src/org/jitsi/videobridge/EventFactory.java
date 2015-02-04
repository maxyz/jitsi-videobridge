/*
 * Jitsi Videobridge, OpenSource video conferencing.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jitsi.videobridge;

import org.osgi.service.event.*;

import java.util.*;

/**
 * A utility class with static methods which initialize <tt>Event</tt> instances
 * with pre-determined fields.
 *
 * @author Boris Grozev
 */
public class EventFactory
{
    /**
     * The names of the columns of a "conference created" event.
     */
    private static final String[] CONFERENCE_CREATED_COLUMNS
        = new String[] {"conference_id", "focus"};

    /**
     * The names of the columns of a "conference expired" event.
     */
    private static final String[] CONFERENCE_EXPIRED_COLUMNS
        = new String[] {"conference_id"};

    /**
     * The names of the columns of a "content created" event.
     */
    private static final String[] CONTENT_CREATED_COLUMNS
        = new String[] {"name", "conference_id"};

    /**
     * The names of the columns of a "content expired" event.
     */
    private static final String[] CONTENT_EXPIRED_COLUMNS
            = CONTENT_CREATED_COLUMNS;

    /**
     * The names of the columns of a "channel created" event.
     */
    private static final String[] CHANNEL_CREATED_COLUMNS
        = new String[]
            {
                    "channel_id",
                    "content_name",
                    "conference_id",
                    "endpoint_id",
                    "lastn"
            };

    /**
     * The names of the columns of a "channel expired" event.
     */
    private static final String[] CHANNEL_EXPIRED_COLUMNS
        = CHANNEL_CREATED_COLUMNS;

    /**
     * The names of the columns of a "transport created" event.
     */
    private static final String[] TRANSPORT_CREATED_COLUMNS
        = new String[]
            {
                    "hash_code",
                    "conference_id",
                    "num_components",
                    "ufrag",
                    "is_controlling"
            };

    /**
     * The names of the columns of a "transport manager channel added" event.
     */
    private static final String[] TRANSPORT_CHANNEL_ADDED_COLUMNS
        = new String[]
            {
                    "hash_code",
                    "conference_id",
                    "channel_id",
            };

    /**
     * The names of the columns of a "transport manager channel removed" event.
     */
    private static final String[] TRANSPORT_CHANNEL_REMOVED_COLUMNS
        = TRANSPORT_CHANNEL_ADDED_COLUMNS;

    /**
     * The names of the columns of a "transport manager connected" event.
     */
    private static final String[] TRANSPORT_CONNECTED_COLUMNS
        = new String[]
            {
                    "hash_code",
                    "conference_id",
                    "selected_pairs"
            };

    /**
     * The names of the columns of a "transport manager connected" event.
     */
    private static final String[] TRANSPORT_STATE_CHANGED_COLUMNS
        = new String[]
            {
                    "hash_code",
                    "conference_id",
                    "old_state",
                    "new_state"
            };

    /**
     * The names of the columns of an "endpoint created" event.
     */
    private static final String[] ENDPOINT_CREATED_COLUMNS
        = new String[]
            {
                    "conference_id",
                    "endpoint_id",
            };

    /**
     * The names of the columns of a "focus created" event.
     */
    private static final String[] FOCUS_CREATED_COLUMNS
        = new String[]
            {
                    "room_jid"
            };

    /**
     * The names of the columns of a "conference room" event.
     */
    private static final String[] CONFERENCE_ROOM_COLUMNS
            = new String[]
            {
                    "conference_id",
                    "room_jid"
            };

    /**
     * The names of the columns of an "endpoint display name" event.
     */
    private static final String[] ENDPOINT_DISPLAY_NAME_COLUMNS
            = new String[]
            {
                    "conference_id",
                    "endpoint_id",
                    "display_name"
            };

    /**
     * Creates a new "conference created" <tt>Event</tt>, which indicates the
     * creation of a new COLIBRI conference.
     * @param id the ID of the COLIBRI conference.
     * @param focus the JID which requested the creation of the COLIBRI
     * conference, if any.
     *
     * @return the <tt>Event</tt> which was created.
     */
    public static Event conferenceCreated(String id, String focus)
    {
        Dictionary properties = new Hashtable();
        properties.put("conference_id", id);
        properties.put("focus" , focus != null ? focus : "null");

        return
            new Event("org/jitsi/videobridge/Conference/CREATED", properties);
    }

    /**
     * Creates a new "content created" <tt>Event</tt>, which indicates the
     * creation of a new COLIBRI content.
     * @param name the name of the COLIBRI content.
     * @param conferenceId the ID of the COLIBRI content's parent conference.
     *
     * @return the <tt>Event</tt> which was created.
     */
    public static Event contentCreated(String name, String conferenceId)
    {
        Dictionary properties = new Hashtable();
        properties.put("name", name);
        properties.put("conference_id", conferenceId);

        return
            new Event("org/jitsi/videobridge/Content/CREATED", properties);
    }

    /**
     * Creates a new "channel created" <tt>Event</tt>, which indicates the
     * creation of a new COLIBRI channel.
     * @param id the ID of the COLIBRI channel.
     * @param contentName the name of the COLIBRI channel's parent content.
     * @param conferenceId the ID of the COLIBRI channel's parent conference.
     * @param endpointId the ID of the channel's endpoint.
     * @param lastN the last-n value for the channel.
     *
     * @return the <tt>Event</tt> which was created.
     */
    public static Event channelCreated(
            String id,
            String contentName,
            String conferenceId,
            String endpointId,
            int lastN)
    {
        Dictionary properties = new Hashtable();
        properties.put("channel_id", id);
        properties.put("content_name", contentName);
        properties.put("conference_id", conferenceId);
        properties.put("endpoint_id", endpointId);
        properties.put("lastn", lastN);

        return
            new Event("org/jitsi/videobridge/Channel/CREATED", properties);
    }

    /**
     * Creates a new "conference expired" <tt>Event</tt>, which indicates the
     * expiry of a COLIBRI conference.
     * @param id the ID of the COLIBRI conference.
     *
     * @return the <tt>Event</tt> which was created.
     */
    public static Event conferenceExpired(String id)
    {
        Dictionary properties = new Hashtable();
        properties.put("conference_id", id);

        return
            new Event("org/jitsi/videobridge/Conference/EXPIRED", properties);
    }

    /**
     * Creates a new "content expired" <tt>Event</tt>, which indicates the
     * expiry of a COLIBRI content.
     * @param name the name of the COLIBRI content.
     * @param conferenceId the ID of the COLIBRI content's parent conference.
     *
     * @return the <tt>Event</tt> which was created.
     */
    public static Event contentExpired(String name, String conferenceId)
    {
        Dictionary properties = new Hashtable();
        properties.put("name", name);
        properties.put("conference_id", conferenceId);

        return new Event("org/jitsi/videobridge/Content/EXPIRED", properties);
    }

    /**
     * Creates a new "channel expired" <tt>Event</tt>, which indicates the
     * expiry of a COLIBRI channel.
     * @param id the ID of the COLIBRI channel.
     * @param contentName the name of the COLIBRI channel's parent content.
     * @param conferenceId the ID of the COLIBRI channel's parent conference.
     *
     * @return the <tt>Event</tt> which was created.
     */
    public static Event channelExpired(
            String id,
            String contentName,
            String conferenceId)
    {
        Dictionary properties = new Hashtable();
        properties.put("channel_id", id);
        properties.put("content_name", contentName);
        properties.put("conference_id", conferenceId);

        return new Event("org/jitsi/videobridge/Channel/EXPIRED", properties);
    }

    /**
     * Creates a new "transport created" <tt>Event</tt>, which indicates the
     * creation of a new Jitsi Videobridge TransportManager.
     * @param hashCode the hash code of the transport manager object.
     * @param conferenceId the ID of the transport manager's parent conference.
     * @param numComponents the number of ICE components.
     * @param ufrag the local ufrag.
     * @param isControlling whether the ICE agent has the controlling or
     * controlled role.
     *
     * @return the <tt>Event</tt> which was created.
     */
    public static Event transportCreated(
            int hashCode,
            String conferenceId,
            int numComponents,
            String ufrag,
            boolean isControlling)
    {
        Dictionary properties = new Hashtable();
        properties.put("hash_code", String.valueOf(hashCode));
        properties.put("conference_id", conferenceId);
        properties.put("num_components", numComponents);
        properties.put("ufrag", ufrag);
        properties.put("is_controlling",
            Boolean.valueOf(isControlling).toString());

        return new Event(
            "org/jitsi/videobridge/IceUdpTransportManager/CREATED", properties);
    }

    /**
     * Creates a new "transport channel added" <tt>Event</tt>, which indicates
     * that a COLIBRI channel was added to a Jitsi Videobridge TransportManager.
     * @param hashCode the hash code of the transport manager object.
     * @param conferenceId the ID of the transport manager's parent conference.
     * @param channelId the ID of the channel which was added.
     *
     * @return the <tt>Event</tt> which was created.
     */
    public static Event transportChannelAdded(
            int hashCode,
            String conferenceId,
            String channelId)
    {
        Dictionary properties = new Hashtable();
        properties.put("hash_code", String.valueOf(hashCode));
        properties.put("conference_id", conferenceId);
        properties.put("channel_id", channelId);

        return new Event(
            "org/jitsi/videobridge/IceUdpTransportManager/TRANSPORT_CHANNEL_ADDED", properties);
    }

    /**
     * Creates a new "transport channel removed" <tt>Event</tt>, which indicates
     * that a COLIBRI channel was removed from a Jitsi Videobridge
     * TransportManager.
     * @param hashCode the hash code of the transport manager object.
     * @param conferenceId the ID of the transport manager's parent conference.
     * @param channelId the ID of the channel which was added.
     *
     * @return the <tt>Event</tt> which was created.
     */
    public static Event transportChannelRemoved(
            int hashCode,
            String conferenceId,
            String channelId)
    {
        Dictionary properties = new Hashtable();
        properties.put("hash_code", String.valueOf(hashCode));
        properties.put("conference_id", conferenceId);
        properties.put("channel_id", channelId);

        return new Event(
            "org/jitsi/videobridge/IceUdpTransportManager/TRANSPORT_CHANNEL_REMOVED", properties);
    }

    /**
     * Creates a new "transport connected" <tt>Event</tt>, which indicates
     * that a Jitsi Videobridge TransportManager has changed its state to
     * connected.
     * @param hashCode the hash code of the transport manager object
     * @param conferenceId the ID of the transport manager's parent conference.
     * @param selectedPairs a <tt>String</tt> representation of the ICE pairs
     * which were selected for each ICE component.
     *
     * @return the <tt>Event</tt> which was created.
     */
    public static Event transportConnected(
            int hashCode,
            String conferenceId,
            String selectedPairs)
    {
        Dictionary properties = new Hashtable();
        properties.put("hash_code", String.valueOf(hashCode));
        properties.put("conference_id", conferenceId);
        properties.put("selected_pairs", selectedPairs);

        return new Event(
            "org/jitsi/videobridge/IceUdpTransportManager/TRANSPORT_CHANNEL_CONNECTED", properties);
    }

    /**
     * Creates a new "transport manager state changed" <tt>Event</tt>, which
     * indicates that a Jitsi Videobridge TransportManager has changed its
     * state.
     * @param hashCode the hash code of the transport manager object
     * @param conferenceId the ID of the transport manager's parent conference.
     * @param oldState the old ICE state.
     * @param newState the new ICE state.
     *
     * @return the <tt>Event</tt> which was created.
     */
    public static Event transportStateChanged(
            int hashCode,
            String conferenceId,
            String oldState,
            String newState)
    {
        Dictionary properties = new Hashtable();
        properties.put("hash_code", String.valueOf(hashCode));
        properties.put("old_state", oldState);
        properties.put("new_state", newState);

        return new Event(
            "org/jitsi/videobridge/IceUdpTransportManager/TRANSPORT_CHANGED", properties);
    }

    /**
     * Creates a new "endpoint created" <tt>Event</tt>, which indicates that
     * a COLIBRI endpoint was created.
     * @param conferenceId the ID of the endpoint's parent conference.
     * @param endpointId the ID of the endpoint
     *
     * @return the <tt>Event</tt> which was created.
     */
    public static Event endpointCreated(
            String conferenceId,
            String endpointId)
    {
        Dictionary properties = new Hashtable();
        properties.put("conference_id", conferenceId);
        properties.put("endpoint_id", endpointId);

        return new Event(
            "org/jitsi/videobridge/Endpoint/CREATED", properties);
    }

    /**
     * Creates a new "focus created" <tt>Event</tt>.
     * @param roomJid the JID of the MUC for which the focus was created.
     *
     * @return the <tt>Event</tt> which was created.
     */
    public static Event focusCreated(
            String roomJid)
    {
        Dictionary properties = new Hashtable();
        properties.put("room_jid", roomJid);

        return new Event(
            "org/jitsi/jicofo/Focus/CREATED", properties);
    }

    /**
     * Creates a new "room conference" <tt>Event</tt> which binds a COLIBRI
     * conference ID to the JID of the associated MUC.
     *
     * @param conferenceId the ID of the COLIBRI conference.
     * @param roomJid the JID of the MUC for which the focus was created.
     *
     * @return the <tt>Event</tt> which was created.
     */
    public static Event conferenceRoom(
            String conferenceId,
            String roomJid)
    {
        Dictionary properties = new Hashtable();
        properties.put("room_jid", roomJid);
        properties.put("conference_id", conferenceId);


        return new Event(
            "org/jitsi/jicofo/Conference/Room/CREATED", properties);
    }

    /**
     * Creates a new "endpoint display name changed" <tt>Event</tt>, which
     * conference ID to the JID of the associated MUC.
     *
     * @param conferenceId the ID of the COLIBRI conference.
     * @param endpointId the ID of the COLIBRI endpoint.
     * @param displayName the new display name.
     *
     * @return the <tt>Event</tt> which was created.
     */
    public static Event endpointDisplayNameChanged(
            String conferenceId,
            String endpointId,
            String displayName)
    {
        Dictionary properties = new Hashtable();
        properties.put("endpoint_id", endpointId);
        properties.put("conference_id", conferenceId);
        properties.put("display_name", displayName);


        return new Event(
            "org/jitsi/videobridge/Endpoint/NAME_CHANGED", properties);
    }
}
