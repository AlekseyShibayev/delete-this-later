package com.company.app.springboottest.application;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

class ApplicationTest extends SpringBootApplicationTestContext {

	@Autowired
	ApplicationContext applicationContext;

	@Test
	void contextMustBe() {
		Assertions.assertNotNull(applicationContext);
	}
}