package com.axelor.event.web;


import java.util.LinkedHashMap;

import javax.inject.Inject;

import com.axelor.event.service.ImportEventRegistrationService;
import com.axelor.inject.Beans;
import com.axelor.meta.db.MetaFile;
import com.axelor.meta.db.repo.MetaFileRepository;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;


public class ImpostCsv {
	
	@Inject
	ImportEventRegistrationService importEventRegistrationService;

	public void importRegistration(ActionRequest request, ActionResponse response) {

		LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) request.getContext().get("metaFile");

		MetaFile dataFile = Beans.get(MetaFileRepository.class).find(((Integer) map.get("id")).longValue());
		
		if (!dataFile.getFileType().equals("text/csv")) {
			response.setError("Please upload a CSV file");
		} else {
			Integer id = (Integer) request.getContext().get("_id");
			importEventRegistrationService.importCsv(dataFile, id);
			response.setFlash("Registrations imported  successfully!!");
		}

		
	}
}
