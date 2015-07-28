package com.keevosh.commons.uacompatibility;

import java.util.Arrays;
import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Aggelos Karalias &lt;aggelos.karalias at gmail.com&gt;
 */
public class UaCompatibilityFilterTest {

    private static UaCompatibilityProperties browserConf;

    public UaCompatibilityFilterTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        browserConf = new UaCompatibilityProperties();
        browserConf.setVersions(Arrays.asList(new String[]{
            ",,IE,10",
            ",ANDROID,UNKNOWN,4.4",
            ",,ANDROID_WEBKIT,4.4",
            ",,SAFARI,6"
        }));
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of doFilterInternal method, of class BrowserCompatibilityFilter.
     */
    @Test
    public void testIsBrowserCompatible() {
        System.out.println("isBrowserCompatible");

        UaCompatibilityFilter instance = new UaCompatibilityFilter();
        instance.setUaCompatibilityProperties(browserConf);

        UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();
        Arrays.asList(new String[]{
            "Mozilla/5.0 (Windows; U; MSIE 9.0; WIndows NT 9.0; en-US)",
            "Mozilla/5.0 (Linux; U; Android 4.0.3; ko-kr; LG-L160L Build/IML74K) AppleWebkit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30",
            "Mozilla/5.0 (Linux; U; Android 4.3.0; ko-kr; LG-L160L Build/IML74K) AppleWebkit/534.30 (KHTML, like Gecko) Version/4.3.1 Mobile Safari/534.30",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/537.13+ (KHTML, like Gecko) Version/5.1.7 Safari/534.57.2"
        }).forEach(ua -> {
            ReadableUserAgent agent = parser.parse(ua);
            if(instance.isBrowserCompatible(agent)) {
                fail("User agent should not be compatible: ["
                        + agent.getDeviceCategory().getCategory() + ", "
                        +  agent.getOperatingSystem().getFamily() + ", "
                        +  agent.getFamily() + ", "
                        +  agent.getVersionNumber().toVersionString()
                        + "]");
            }
        });
    }

}
