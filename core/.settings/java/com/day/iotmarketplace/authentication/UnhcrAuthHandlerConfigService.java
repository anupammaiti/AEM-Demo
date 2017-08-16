package org.unhcr.cq5.auth;

/**
 * @author mike.pfaff@adobe.com
 */
public interface UnhcrAuthHandlerConfigService {

    String getLoginFormURL();
    String getLoginActionURL();
    String getForgotFormURL();
    String getTermsFormURL();
    String getTermsActionURL();
    String getLogoutURL();

}
