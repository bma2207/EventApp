package com.axelor.event.module;

import com.axelor.app.AxelorModule;
import com.axelor.event.service.EventRegistrationService;
import com.axelor.event.service.EventRegistrationServiceImp;

public class EventModule extends AxelorModule{

	@Override
	protected void configure() {
		// TODO Auto-generated method stub
		bind(EventRegistrationService.class).to(EventRegistrationServiceImp.class);
	}

	
}
