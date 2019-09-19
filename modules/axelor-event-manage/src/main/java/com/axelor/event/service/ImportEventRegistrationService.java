package com.axelor.event.service;

import com.axelor.meta.db.MetaFile;

public interface ImportEventRegistrationService {
	public void importCsv(MetaFile dataFile , Integer id);
}
