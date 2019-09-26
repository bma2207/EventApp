package com.axelor.event.service;

import com.axelor.event.db.EventRegistration;
import com.axelor.meta.db.MetaFile;

public interface EventRegistrationService {
	public void importCsv(MetaFile dataFile , Integer id);
	public void manageTotalEntry(EventRegistration eventRegistration);
}
