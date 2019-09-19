package com.axelor.event.module;

import com.axelor.app.AxelorModule;
import com.axelor.event.service.ImportEventRegistrationService;
import com.axelor.event.service.ImportEventRegistrationServiceImpl;

public class EventSaveModule extends AxelorModule{

	@Override
	protected void configure() {
		// TODO Auto-generated method stub
		bind(ImportEventRegistrationService.class).to(ImportEventRegistrationServiceImpl.class);
	}

	
}
