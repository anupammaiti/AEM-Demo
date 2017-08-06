package com.aem.community.core.services;

import com.aem.community.core.objects.User;

public interface LoginValidatorService {
	User validateCredentials(String username, String password);
}
