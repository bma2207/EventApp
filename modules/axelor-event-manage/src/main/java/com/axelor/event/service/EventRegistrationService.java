package com.axelor.event.service;

import com.axelor.meta.db.MetaFile;

public interface EventRegistrationService {
	public void importCsv(MetaFile dataFile , Integer id);
}
