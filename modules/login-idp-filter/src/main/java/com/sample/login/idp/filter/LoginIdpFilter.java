package com.sample.login.idp.filter;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.security.sso.openid.connect.OpenIdConnectProviderRegistry;
import com.liferay.portal.security.sso.openid.connect.OpenIdConnectServiceHandler;
import com.sample.login.idp.filter.config.LoginIdpFilterConfiguration;
import org.osgi.service.component.annotations.*;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component(
        immediate = true,
        property = {
                "servlet-context-name=",
                "servlet-filter-name=Login IDP Filter",
                "url-pattern=/c/portal/login"
        },
        service = Filter.class
)
public class LoginIdpFilter implements Filter {

    @Reference(policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    private volatile OpenIdConnectProviderRegistry _openIdConnectProviderRegistry;

    @Reference(policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    private volatile OpenIdConnectServiceHandler _openIdConnectServiceHandler;

    private static final Log _log = LogFactoryUtil.getLog(LoginIdpFilter.class);
    private volatile LoginIdpFilterConfiguration _configuration;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        try {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;

            //Get OpenId provider specified in the OSGI system configuration
            String openIdConnectProviderName = String.valueOf(_openIdConnectProviderRegistry.getOpenIdConnectProviderNames()
                                                        .stream()
                                                        .filter(openIdConnectProviderName1 -> _openIdConnectProviderRegistry.getOpenIdConnectProviderNames().contains(_configuration.idpName()))
                                                        .findAny()
                                                        .orElse(null));

            if (openIdConnectProviderName == null || openIdConnectProviderName.isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            }

            _log.debug("openIdConnectProviderName: " + openIdConnectProviderName);

            // Request Provider's authentication
            _openIdConnectServiceHandler.requestAuthentication(openIdConnectProviderName, request, response);

        } catch (Exception exception) {
            _log.error("Error in LoginIdpFilter: " + exception.getMessage(), exception);
        } finally {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Activate
    @Modified
    protected void active(Map<String, Object> properties) {
        _configuration = ConfigurableUtil.createConfigurable(LoginIdpFilterConfiguration.class, properties);
    }

}