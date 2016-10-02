package io.mateu.erp.caval;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import travel.caval._20091127.hotelbooking.CavalHotelAvailabilityRQ;
import travel.caval._20091127.hotelbooking.CavalHotelAvailabilityRS;
import travel.caval._20091127.hotelbooking.HotelBookingServicePortImpl;

/**
 * Created by miguel on 1/10/16.
 */
public class HotelAvailabilityTest extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public HotelAvailabilityTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( HotelAvailabilityTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }


    /**
     * first availability test. It can not return null
     */
    public void testDoesNotReturnNull() {
        CavalHotelAvailabilityRQ rq = new CavalHotelAvailabilityRQ();
        CavalHotelAvailabilityRS rs = new HotelBookingServicePortImpl().getAvailableHotels(rq);

        assertNotNull(rs);
    }

}
