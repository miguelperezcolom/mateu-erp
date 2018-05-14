package io.mateu.common;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class HotelAvailabilityTest
    extends TestCase
{

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        System.out.println("setup");



    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        System.out.println("teardown");
    }

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public HotelAvailabilityTest(String testName )
    {
        super( testName );
        System.out.println("hola");
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        System.out.println("xxx");
        return new TestSuite( HotelAvailabilityTest.class );

    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }

    public void testParo() {
        assertEquals(0, 0);
    }
}
