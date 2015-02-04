/*
 * Jitsi Videobridge, OpenSource video conferencing.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jitsi.videobridge.influxdb;

import org.jitsi.service.configuration.*;
import org.jitsi.util.*;
import org.json.simple.*;
import org.osgi.service.event.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Allows logging of {@link InfluxDBEvent}s using an
 * <tt>InfluxDB</tt> instance.
 *
 * @author Boris Grozev
 */
public class InfluxDBLoggingHandler
        implements EventHandler
{
    /**
     * The name of the property which specifies whether logging to an
     * <tt>InfluxDB</tt> is enabled.
     */
    public static final String ENABLED_PNAME
        = "org.jitsi.videobridge.log.INFLUX_DB_ENABLED";

    /**
     * The name of the property which specifies the protocol, hostname and
     * port number (in URL format) to use to connect to <tt>InfluxDB</tt>.
     */
    public static final String URL_BASE_PNAME
        = "org.jitsi.videobridge.log.INFLUX_URL_BASE";

    /**
     * The name of the property which specifies the name of the
     * <tt>InfluxDB</tt> database.
     */
    public static final String DATABASE_PNAME
        = "org.jitsi.videobridge.log.INFLUX_DATABASE";

    /**
     * The name of the property which specifies the username to use to connect
     * to <tt>InfluxDB</tt>.
     */
    public static final String USER_PNAME
        = "org.jitsi.videobridge.log.INFLUX_USER";

    /**
     * The name of the property which specifies the password to use to connect
     * to <tt>InfluxDB</tt>.
     */
    public static final String PASS_PNAME
        = "org.jitsi.videobridge.log.INFLUX_PASS";

    /**
     * The <tt>Logger</tt> used by the <tt>InfluxDBLoggingService</tt> class
     * and its instances to print debug information.
     */
    private static final Logger logger
        = Logger.getLogger(InfluxDBLoggingHandler.class);

    /**
     * The <tt>Executor</tt> which is to perform the task of sending data to
     * <tt>InfluxDB</tt>.
     */
    private final Executor executor
        = ExecutorUtils
            .newCachedThreadPool(true, InfluxDBLoggingHandler.class.getName());

    /**
     * The <tt>URL</tt> to be used to POST to <tt>InfluxDB</tt>. Besides the
     * protocol, host and port also encodes the database name, user name and
     * password.
     */
    private final URL url;

    /**
     * Initializes a new <tt>InfluxDBLoggingService</tt> instance, by reading
     * its configuration from <tt>cfg</tt>.
     * @param cfg the <tt>ConfigurationService</tt> to use.
     *
     * @throws Exception if initialization fails
     */
    InfluxDBLoggingHandler(ConfigurationService cfg)
        throws Exception
    {
        if (cfg == null)
            throw new NullPointerException("cfg");

        String s = "Required property not set: ";
        String urlBase = cfg.getString(URL_BASE_PNAME, null);
        if (urlBase == null)
            throw new Exception(s + URL_BASE_PNAME);

        String database = cfg.getString(DATABASE_PNAME, null);
        if (database == null)
            throw new Exception(s + DATABASE_PNAME);

        String user = cfg.getString(USER_PNAME, null);
        if (user == null)
            throw new Exception(s + USER_PNAME);

        String pass = cfg.getString(PASS_PNAME, null);
        if (pass == null)
            throw new Exception(s + PASS_PNAME);

        String urlStr
            = urlBase +  "/db/" + database + "/series?u=" + user +"&p=" +pass;

        url = new URL(urlStr);

        logger.info("Initialized InfluxDBLoggingService for " + urlBase
                            + ", database \"" + database + "\"");
    }

    /**
     * Logs an <tt>Event</tt> to an <tt>InfluxDB</tt> database. This method
     * returns without blocking, the blocking operations are performed by a
     * thread from {@link #executor}.
     *
     * @param event the <tt>Event</tt> to log.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void handleEvent(Event event)
    {
        InfluxDBEvent e = convert(event);
        // The following is a sample JSON message in the format used by InfluxDB
        //  [
        //    {
        //     "name": "series_name",
        //     "columns": ["column1", "column2"],
        //     "points": [
        //           ["value1", 1234],
        //           ["value2", 5678]
        //          ]
        //    }
        //  ]

        boolean useLocalTime = e.useLocalTime();
        long now = System.currentTimeMillis();
        boolean multipoint = false;
        int pointCount = 1;
        JSONArray columns = new JSONArray();
        JSONArray points = new JSONArray();
        Object[] values = e.getValues();

        if (useLocalTime)
            columns.add("time");
        Collections.addAll(columns, e.getColumns());

        if (values[0] instanceof Object[])
        {
            multipoint = true;
            pointCount = values.length;
        }

        if (multipoint)
        {
            for (int i = 0; i < pointCount; i++)
            {
                JSONArray point = new JSONArray();
                if (useLocalTime)
                    point.add(now);
                Collections.addAll(point, values[i]);
                points.add(point);
            }
        }
        else
        {
            JSONArray point = new JSONArray();
            if (useLocalTime)
                point.add(System.currentTimeMillis());
            // Make sure points.length>0
            Collections.addAll(point, values);
            points.add(point);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", e.getName());
        jsonObject.put("columns", columns);
        jsonObject.put("points", points);

        JSONArray jsonArray = new JSONArray();
        jsonArray.add(jsonObject);

        // TODO: this is probably a good place to optimize by grouping multiple
        // events in a single POST message and/or multiple points for events
        // of the same type together).
        final String jsonString = jsonArray.toJSONString();
        executor.execute(new Runnable()
        {
            @Override
            public void run()
            {
                sendPost(jsonString);
            }
        });
    }

    private InfluxDBEvent convert(Event event)
    {
        return null;
    }

    /**
     * Sends the string <tt>s</tt> as the contents of an HTTP POST request to
     * {@link #url}.
     * @param s the content of the POST request.
     */
    private void sendPost(final String s)
    {
        try
        {
            HttpURLConnection connection
                    = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-type",
                                          "application/json");

            connection.setDoOutput(true);
            DataOutputStream outputStream
                = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(s);
            outputStream.flush();
            outputStream.close();

            int responseCode = connection.getResponseCode();
            if (responseCode != 200)
                throw new IOException("HTTP response code: "
                                              + responseCode);
        }
        catch (IOException ioe)
        {
            logger.info("Failed to post to influxdb: " + ioe);
        }
    }
}
