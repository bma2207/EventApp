package com.axelor.event.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import com.axelor.data.Importer;
import com.axelor.data.csv.CSVImporter;
import com.axelor.event.db.Event;
import com.axelor.event.db.EventRegistration;
import com.axelor.event.db.repo.EventRepository;
import com.axelor.inject.Beans;
import com.axelor.meta.MetaFiles;
import com.axelor.meta.db.MetaFile;
import com.google.inject.Inject;

public class EventRegistrationServiceImp implements EventRegistrationService {
	@Inject
	EventRepository eventRepository;

	@Override
	public void importCsv(MetaFile dataFile, Integer id) {
		
		File configXmlFile = this.getConfigXmlFile();
		File CsvFile = MetaFiles.getPath(dataFile).toFile();

		
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("_event_id", id);
		Importer importer = new CSVImporter(configXmlFile.getAbsolutePath(), CsvFile.getParent().toString());

		importer.setContext(context);
		importer.run();
	}

	

	private File getConfigXmlFile() {

		File configFile = null;
		try {
			configFile = File.createTempFile("input-config", ".xml");

			InputStream bindFileInputStream = this.getClass().getResourceAsStream("/data/" + "input-config.xml");

			if (bindFileInputStream == null) {

			}

			FileOutputStream outputStream = new FileOutputStream(configFile);

			IOUtils.copy(bindFileInputStream, outputStream);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return configFile;
	}

	@Override
	public void manageTotalEntry(EventRegistration eventRegistration) {
		Event event = (Beans.get(EventRepository.class).all().filter("self.id=?", eventRegistration.getEvent())
				.fetchOne());
		Integer totalEntry = event.getTotalEntry() + 1;
		event.setTotalEntry(totalEntry);
		eventRepository.save(event);

	}

}
