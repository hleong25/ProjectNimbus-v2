/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author henry
 */
public class OutputToInputStreamTest
{
    private String m_file;
    private InputStream m_istream;

    public OutputToInputStreamTest()
    {
        m_file = "";
        m_istream = null;
    }

    @BeforeClass
    public static void setUpClass()
    {
    }

    @AfterClass
    public static void tearDownClass()
    {
    }

    @Before
    public void setUp()
    {
        m_file = "/home/henry/Videos/SampleVideo_720x480_20mb.mkv";
        File file = new File(m_file);

        try
        {
            m_istream = new FileInputStream(file);
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(OutputToInputStreamTest.class.getName()).log(Level.SEVERE, null, ex);
            fail(ex.toString());
        }
    }

    @After
    public void tearDown()
    {
    }

    /**
     * Test of close method, of class OutputToInputStream.
     */
    //@Test
    //public void testClose() throws Exception
    //{
    //    System.out.println("close");
    //    OutputToInputStreamTemp instance = null;
    //    instance.close();
    //    // TODO review the generated test code and remove the default call to fail.
    //    fail("The test case is a prototype.");
    //}

    /**
     * Test of startReading method, of class OutputToInputStreamTemp.
     */
    @Test
    public void testStartReading()
    {
        return;
        /*
        System.out.println("startReading");
        OutputToInputStreamTemp instance;
        try
        {
            instance = getInputStream();
            instance.startReading();

            readdata();

        }
        catch (IOException ex)
        {
            Logger.getLogger(OutputToInputStreamTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("Cannot create instance of OutputToInputStreamTemp");
        }
        */
    }

    private OutputToInputStreamTemp getInputStream() throws IOException
    {
        final int BUFFER_SIZE = 256*1024;
        OutputToInputStreamTemp inputstream = new OutputToInputStreamTemp(BUFFER_SIZE, m_istream);
        return inputstream;
    }

    private void readdata()
    {
        final int BUFFER_SIZE = 128*1024;
        byte[] buffer = new byte[BUFFER_SIZE];

        long total = 0;
        int bytesRead = 0;

        int streamscnt = 0;
        int idxcnt = 0;

        try
        {
            while ((bytesRead = m_istream.read(buffer)) >= 0)
            {
                ++idxcnt;

                total += bytesRead;
                System.out.println("Read:"+bytesRead+" Total:"+total+" Data:"+Arrays.toString(Arrays.copyOf(buffer, 16)));

                if ((idxcnt > 10) && (streamscnt < 5))
                {
                    ++streamscnt;
                    System.out.println("Streams:"+streamscnt+" Iteration:"+idxcnt);

                    m_istream.close();
                    m_istream = getInputStream();
                }
            }
        }
        catch (IOException ex)
        {
            Logger.getLogger(OutputToInputStreamTest.class.getName()).log(Level.SEVERE, null, ex);
            fail(ex.toString());
        }
    }

    /**
     * Test of finalize method, of class OutputToInputStream.
     */
    //@Test
    //public void testFinalize() throws Exception
    //{
    //    System.out.println("finalize");
    //    OutputToInputStreamTemp instance = null;
    //    instance.finalize();
    //    // TODO review the generated test code and remove the default call to fail.
    //    fail("The test case is a prototype.");
    //}

    /**
     * Test of canRead method, of class OutputToInputStream.
     */
    //@Test
    //public void testCanRead()
    //{
    //    System.out.println("canRead");
    //    OutputToInputStreamTemp instance = null;
    //    boolean expResult = false;
    //    boolean result = instance.canRead();
    //    assertEquals(expResult, result);
    //    // TODO review the generated test code and remove the default call to fail.
    //    fail("The test case is a prototype.");
    //}

}
