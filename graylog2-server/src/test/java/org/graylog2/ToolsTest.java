/**
 * Copyright 2010 Lennart Koopmann <lennart@scopeport.org>
 *
 * This file is part of Graylog2.
 *
 * Graylog2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog2.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

/**
 * ToolsTest.java: Lennart Koopmann <lennart@scopeport.org> | Aug 5, 2010 6:49:52 PM
 */

package org.graylog2;

import org.graylog2.plugin.Tools;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;

import static org.junit.Assert.*;

/**
 * @author lennart
 */
public class ToolsTest {

    @Test
    public void testGetPID() {
        String result = Tools.getPID();
        assertTrue(Integer.parseInt(result) > 0);
    }

    @Test
    public void testGetUTCTimestamp() {

        assertTrue(Tools.getUTCTimestamp() > 0);
    }

    @Test
    public void testGetUTCTimestampWithMilliseconds() {

        assertTrue(Tools.getUTCTimestampWithMilliseconds() > 0.0d);
        assertTrue(Tools.getUTCTimestampWithMilliseconds(Calendar.getInstance().getTimeInMillis()) > 0.0d);
    }

    @Test
    public void testGetLocalHostname() {

        String hostname = Tools.getLocalHostname();

        assertFalse(hostname.isEmpty());
    }

    @Test
    public void testSyslogLevelToReadable() {
        assertEquals(Tools.syslogLevelToReadable(1337), "Invalid");
        assertEquals(Tools.syslogLevelToReadable(0), "Emergency");
        assertEquals(Tools.syslogLevelToReadable(2), "Critical");
        assertEquals(Tools.syslogLevelToReadable(6), "Informational");
    }

    @Test
    public void testSyslogFacilityToReadable() {
        assertEquals(Tools.syslogFacilityToReadable(9001), "Unknown");
        assertEquals(Tools.syslogFacilityToReadable(0), "kernel");
        assertEquals(Tools.syslogFacilityToReadable(11), "FTP");
        assertEquals(Tools.syslogFacilityToReadable(22), "local6");
    }

    @Test
    public void testGetSystemInformation() {
        String result = Tools.getSystemInformation();
        assertTrue(result.trim().length() > 0);
    }

    @Test
    public void testDecompressZlib() throws IOException {

        String testString = "Teststring 123";
        byte[] buffer = new byte[100];
        Deflater deflater = new Deflater();

        deflater.setInput(testString.getBytes());
        deflater.finish();
        deflater.deflate(buffer);
        deflater.end();

        assertEquals(testString, Tools.decompressZlib(buffer));
    }

    @Test
    public void testDecompressGzip() throws IOException {

        String testString = "Teststring 123";

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(testString.getBytes());
        gzip.close();

        byte[] buffer = out.toByteArray();

        assertEquals(testString, Tools.decompressGzip(buffer));
    }

    @Test(expected = EOFException.class)
    public void testDecompressGzipEmptyInput() throws IOException {

        Tools.decompressGzip(new byte[0]);
    }

    /**
     * ruby-1.9.2-p136 :001 > [Time.now.to_i, 2.days.ago.to_i]
     *  => [1322063329, 1321890529]
     */
    @Test
    public void testGetTimestampDaysAgo() {
        assertEquals(1321890529, Tools.getTimestampDaysAgo(1322063329, 2));
    }

    @Test
    public void testEncodeBase64() {
        assertEquals("bG9sd2F0LmVuY29kZWQ=", Tools.encodeBase64("lolwat.encoded"));
    }

    @Test
    public void testDecodeBase64() {
        assertEquals("lolwat.encoded", Tools.decodeBase64("bG9sd2F0LmVuY29kZWQ="));
    }
    
    @Test
    public void testGenerateServerId() {
        String id = Tools.generateServerId();
        
        assertTrue(id.startsWith(Tools.getLocalHostname() + "-"));
        assertTrue(id.length() > (Tools.getLocalHostname() + "-").length());
    }
    
    @Test
    public void testAsSortedList() {
        List<Integer> sortMe = Lists.newArrayList();
        sortMe.add(0);
        sortMe.add(2);
        sortMe.add(6);
        sortMe.add(1);
        sortMe.add(10);
        sortMe.add(25);
        sortMe.add(11);
        
        List<Integer> expected = Lists.newArrayList();
        expected.add(0);
        expected.add(1);
        expected.add(2);
        expected.add(6);
        expected.add(10);
        expected.add(11);
        expected.add(25);
        
        assertEquals(expected, Tools.asSortedList(sortMe));
    }
}