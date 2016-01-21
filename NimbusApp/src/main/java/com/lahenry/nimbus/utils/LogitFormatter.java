/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.utils;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author henry
 */
public class LogitFormatter extends SimpleFormatter
{
    private static final DateFormat TIMESTAMP = new SimpleDateFormat("HH:mm:ss.SSS ");

    public LogitFormatter()
    {
    }

    @Override
    public synchronized String format(LogRecord record)
    {
        //return super.format(record);

        StringBuilder str = new StringBuilder();

        str.append(TIMESTAMP.format(record.getMillis()));
        str.append("["+record.getLevel()+"] ");
        str.append(record.getSourceClassName());
        str.append(".");
        str.append(record.getSourceMethodName());
        str.append("() ");
        if (record.getParameters() == null)
        {
            str.append(record.getMessage());
        }
        else
        {
            str.append(MessageFormat.format(record.getMessage(), record.getParameters()));
        }
        str.append("\n");

        if (record.getThrown() != null)
        {
            Throwable t = record.getThrown();

            if (t.getMessage() != null)
            {
                str.append("Exception message: " + t.getMessage());
                str.append("\n");
            }

            {
                OutputStream os = new ByteArrayOutputStream();
                PrintStream ps = new PrintStream(os);

                t.printStackTrace(ps);
                ps.flush();

                str.append(os.toString());
            }

            str.append("\n");
        }

        return str.toString();
    }
}
