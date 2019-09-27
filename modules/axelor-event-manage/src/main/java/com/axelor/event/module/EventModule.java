package com.axelor.event.module;

import com.axelor.app.AxelorModule;
import com.axelor.event.service.EventRegistrationService;
import com.axelor.event.service.EventRegistrationServiceImp;

public class EventModule extends AxelorModule {

	@Override
	protected void configure() {
		bind(EventRegistrationService.class).to(EventRegistrationServiceImp.class);
	}
}
