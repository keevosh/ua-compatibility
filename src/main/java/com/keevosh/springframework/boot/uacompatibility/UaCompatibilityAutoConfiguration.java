package com.keevosh.springframework.boot.uacompatibility;

import net.sf.uadetector.service.UADetectorServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({UaCompatibilityFilter.class, UADetectorServiceFactory.class})
@EnableConfigurationProperties(UaCompatibilityProperties.class)
public class UaCompatibilityAutoConfiguration {

    @Autowired
    UaCompatibilityProperties uaCompatibilityProperties;

    @Bean(name = "uaCompatibilityFilterRegistrationBean")
    @ConditionalOnMissingBean(name = "uaCompatibilityFilterRegistrationBean")
    public FilterRegistrationBean uaCompatibilityFilterRegistrationBean() {
        UaCompatibilityFilter filter = new UaCompatibilityFilter();
        filter.setUaCompatibilityProperties(uaCompatibilityProperties);

        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(filter);
        registrationBean.setOrder(uaCompatibilityProperties.getFilterOrder());
        registrationBean.setMatchAfter(uaCompatibilityProperties.isFilterMatchAfter());
        if(uaCompatibilityProperties.getFilterUrlPatterns() != null) {
            registrationBean.setUrlPatterns(uaCompatibilityProperties.getFilterUrlPatterns());
        }
        return registrationBean;
    }

}
