package com.keevosh.commons.uacompatibility;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.uadetector.OperatingSystemFamily;
import net.sf.uadetector.ReadableDeviceCategory.Category;
import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentFamily;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.VersionNumber;
import net.sf.uadetector.service.UADetectorServiceFactory;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter which adds CSRF information as response headers.
 *
 */
public final class UaCompatibilityFilter extends OncePerRequestFilter {

    private final Logger log = LoggerFactory.getLogger(UaCompatibilityFilter.class);

    @Autowired
    private UaCompatibilityProperties uaCompatibilityProperties;

    UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();

    @PostConstruct
    public void init() {
        if(uaCompatibilityProperties.isUpdateUaStrings()) {
            parser = UADetectorServiceFactory.getCachingAndUpdatingParser();
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String redirectPath = uaCompatibilityProperties.getRedirect();

        if (StringUtils.startsWithIgnoreCase(req.getRequestURI(), redirectPath) || !StringUtils.containsIgnoreCase(req.getHeader("Accept"), "text/html")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String userAgentHeader = req.getHeader("User-Agent");
            ReadableUserAgent agent = parser.parse(userAgentHeader);

            if(agent != null && !isBrowserCompatible(agent)) {
                resp.sendRedirect(redirectPath);
                log.warn("Not compatible agent found: {}, {}, {}, {} [{}]. Redirecting to: {}",
                            agent.getDeviceCategory().getCategory(),
                            agent.getOperatingSystem().getFamily(),
                            agent.getFamily(),
                            agent.getVersionNumber().toVersionString(),
                            userAgentHeader,
                            redirectPath);

                return;
            }
        } catch(IllegalArgumentException ex) {
            log.warn("Could not instantiate the User-Agent detector parser. Ignore compatibility check and continue..", (log.isDebugEnabled() ? ex : null));
        } catch(Exception ex) {
            log.warn("Could not parse the User-Agent header. Ignore compatibility check and continue..", (log.isDebugEnabled() ? ex : null));
        }

        filterChain.doFilter(request, response);
    }

    public boolean isBrowserCompatible(ReadableUserAgent agent) {
        // if we don't have all the information to decide just consider
        // the agent compatible

        List<Map<String,String>> incompatibleAgents = uaCompatibilityProperties.getUaCompatibility();
        if(agent == null || incompatibleAgents == null) return true;

        boolean notCompatible = false;
        for(Map<String,String> ia : incompatibleAgents) {
            boolean deviceCheck = true;
            boolean browserCheck = true;
            boolean osFamilyCheck = true;

            if(ia.get("device") != null) {
                deviceCheck = Category.valueOf(ia.get("device")).equals(agent.getDeviceCategory().getCategory());
            }

            if(ia.get("os") != null) {
                osFamilyCheck = OperatingSystemFamily.valueOf(ia.get("os")).equals(agent.getOperatingSystem().getFamily());
            }

            if(ia.get("browser") != null) {
                browserCheck = UserAgentFamily.valueOf(ia.get("browser")).equals(agent.getFamily());
            }

            if(deviceCheck && osFamilyCheck && browserCheck) {
                notCompatible = agent.getVersionNumber().compareTo(VersionNumber.parseVersion(ia.get("version"))) < 0;

                if(notCompatible) break;
            }
        }

        return !notCompatible;
    }

    public void setUaCompatibilityProperties(UaCompatibilityProperties props) {
        this.uaCompatibilityProperties = props;
    }

}
