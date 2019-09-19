package com.axelor.event.service;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.axelor.data.ImportException;
import com.axelor.data.ImportTask;
import com.axelor.data.Importer;
import com.axelor.data.Listener;
import com.axelor.data.csv.CSVImporter;
import com.axelor.db.Model;
import com.axelor.meta.MetaFiles;
import com.axelor.meta.db.MetaFile;
import com.google.common.io.Files;
public class ImportEventRegistrationServiceImpl implements ImportEventRegistrationService{
	private Logger log = LoggerFactory.getLogger(getClass());
    
	@Override
	public void importCsv(MetaFile dataFile , Integer id) {

		
		File configXmlFile = this.getConfigXmlFile();
		File dataCsvFile = this.getDataCsvFile(dataFile);
		
		
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("_event_id", id);
		Importer importer = new CSVImporter(configXmlFile.getAbsolutePath(), "/home/axelor/Bhushan/AbsModule/axelor-event/modules/axelor-event-manage/src/main/resources/data/input/");
	      
	      
	      importer.setContext(context);
	      importer.run();
	   

	  }
	  
	private File getDataCsvFile(MetaFile dataFile) {

		File csvFile = null;
		try {
			File tempDir = Files.createTempDir();
			csvFile = new File(tempDir, "registrations.csv");

			Files.copy(MetaFiles.getPath(dataFile).toFile(), csvFile);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return csvFile;
	}

	private File getConfigXmlFile() {

		File configFile = null;
		try {
			configFile = File.createTempFile("input-config", ".xml");

			InputStream bindFileInputStream = this.getClass()
					.getResourceAsStream("/data/" +"input-config.xml");

			if (bindFileInputStream == null) {
				//throw new AxelorException();
			}

			FileOutputStream outputStream = new FileOutputStream(configFile);

			IOUtils.copy(bindFileInputStream, outputStream);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return configFile;
	}

}
