/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.utils;

import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;


/**
 *
 * @author henry
 */
public class Tools
{
    private static final Logit LOG = Logit.create(Tools.class.getName());

    public static boolean isNullOrEmpty(String str)
    {
        return (str == null) || str.isEmpty();
    }

    public static void wait(Object obj)
    {
        if (obj == null) return;

        try
        {
            synchronized(obj)
            {
                obj.wait();
            }
        }
        catch (InterruptedException ex)
        {
            LOG.throwing("wait", ex);
        }
    }

    public static void wait(Object obj, long timeout)
    {
        if (obj == null) return;

        try
        {
            synchronized(obj)
            {
                obj.wait(timeout);
            }
        }
        catch (InterruptedException ex)
        {
            LOG.throwing("wait", ex);
        }
    }

    public static void notify(Object obj)
    {
        if (obj == null) return;

        try
        {
            synchronized(obj)
            {
                obj.notify();
            }
        }
        catch (IllegalMonitorStateException ex)
        {
            LOG.throwing("notify", ex);
        }
    }

    public static void notifyAll(Object obj)
    {
        if (obj == null) return;

        try
        {
            synchronized(obj)
            {
                obj.notifyAll();
            }
        }
        catch (IllegalMonitorStateException ex)
        {
            LOG.throwing("notifyAll", ex);
        }
    }

    public static String formatTransferMsg(long elapsedNano, long totalBytes)
    {
        final DecimalFormat formatter = new DecimalFormat("#,###,###,###");

        final long elapsedSecs = TimeUnit.SECONDS.convert(elapsedNano, TimeUnit.NANOSECONDS);
        final double avgRate = (double)totalBytes / ((elapsedSecs > 0) ? elapsedSecs : 1);

        final String avgRateStr;// = String.format("%.0fkbps", avgRate/1000.0);
        if (avgRate > 10000000.0) // 10mbps
        {
            avgRateStr = String.format("%.03fmbps", avgRate/1000000.0);
        }
        else if (avgRate > 1000.0) // 1kbps
        {
            avgRateStr = String.format("%.0fkbps", avgRate/1000.0);
        }
        else // bps
        {
            avgRateStr = String.format("%.0fbps", avgRate);
        }

        final String msg = String.format("Transferred %s bytes in %s seconds (%s)",
                                         formatter.format(totalBytes),
                                         formatter.format(elapsedSecs),
                                         avgRateStr);
        return msg;
    }

    public static String xmlToString(Document doc)
    {
        try
        {
            StringWriter sw = new StringWriter();

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");

            transformer.transform(new DOMSource(doc), new StreamResult(sw));

            return sw.toString();
        }
        catch (TransformerException ex)
        {
        }
        return "";
    }

}
