package com.sample.login.idp.filter.config;

import aQute.bnd.annotation.ProviderType;
import aQute.bnd.annotation.metatype.Meta;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.sample.login.idp.filter.constants.LoginIdpFilterKeys;

/**
 * @author Ivan Sánchez
 */
@ProviderType
@ExtendedObjectClassDefinition(
        category = LoginIdpFilterKeys.CONFIGURATION_CATEGORY,
        scope = ExtendedObjectClassDefinition.Scope.SYSTEM
)
@Meta.OCD(
        id = LoginIdpFilterKeys.WIDGET_CONFIGURATION_NAME,
        localization = LoginIdpFilterKeys.CONFIGURATION_LOCALIZATION
)
public interface LoginIdpFilterConfiguration {

    @Meta.AD(
            deflt = "Keycloak",
            required = false
    )
    String idpName();

}