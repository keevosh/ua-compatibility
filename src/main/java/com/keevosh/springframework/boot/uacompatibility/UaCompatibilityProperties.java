package com.keevosh.springframework.boot.uacompatibility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ua-compatibility")
public class UaCompatibilityProperties {

    public static final String DEFAULT_REDIRECT_PATH = "/i13y";
    
    public static final int DEFAULT_FILTER_ORDER = 1;

    private String redirect = DEFAULT_REDIRECT_PATH;

    private boolean updateUaStrings = false;

    private List<String> versions;

    private List<Map<String,String>> uaCompatibility;

    private int filterOrder = DEFAULT_FILTER_ORDER;

    private boolean filterMatchAfter = false;

    private List<String> filterUrlPatterns;

    public List<Map<String, String>> getUaCompatibility() {
        if(uaCompatibility != null) {
            return uaCompatibility;
        }

        if(versions == null) {
            return null;
        }

        uaCompatibility = new ArrayList<>();
        versions.forEach(agent -> {
            String[] agentProps = agent.split(",");
            Map<String,String> agentMap = new HashMap<>();
            agentMap.put("device", StringUtils.isBlank(agentProps[0]) ? null : agentProps[0]);
            agentMap.put("os", StringUtils.isBlank(agentProps[1]) ? null : agentProps[1]);
            agentMap.put("browser", StringUtils.isBlank(agentProps[2]) ? null : agentProps[2]);
            agentMap.put("version", StringUtils.isBlank(agentProps[3]) ? null : agentProps[3]);

            uaCompatibility.add(agentMap);
        });
        return uaCompatibility;
    }

    public List<String> getVersions() {
        return versions;
    }

    public void setVersions(List<String> versions) {
        this.versions = versions;
    }

    public void addVersion(String version) {
        if(versions == null) {
            versions = new ArrayList<>();
        }
        versions.add(version);
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public int getFilterOrder() {
        return filterOrder;
    }

    public void setFilterOrder(int filterOrder) {
        this.filterOrder = filterOrder;
    }

    public boolean isFilterMatchAfter() {
        return filterMatchAfter;
    }

    public void setFilterMatchAfter(boolean filterMatchAfter) {
        this.filterMatchAfter = filterMatchAfter;
    }

    public List<String> getFilterUrlPatterns() {
        return filterUrlPatterns;
    }

    public void setFilterUrlPatterns(List<String> filterUrlPatterns) {
        this.filterUrlPatterns = filterUrlPatterns;
    }

    public boolean isUpdateUaStrings() {
        return updateUaStrings;
    }

    public void setUpdateUaStrings(boolean updateUaStrings) {
        this.updateUaStrings = updateUaStrings;
    }

}
