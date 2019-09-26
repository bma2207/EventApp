package com.axelor.event.csv.script;

import java.util.Map;

import com.axelor.event.db.Event;
import com.axelor.event.db.EventRegistration;
import com.google.inject.persist.Transactional;

public class ImportScripat {
	@Transactional
	public Object ImportEvent(Object bean, Map<String, Object> values) {
		assert bean instanceof EventRegistration;
		EventRegistration eventreg = (EventRegistration) bean;
		int importdata = 0;
		Event event = eventreg.getEvent();
		int totalEntry = event.getTotalEntry();
		if (event.getTotalEntry() != event.getCapacity()) {
			importdata++;
			totalEntry++;
		}
		event.setTotalEntry(totalEntry);
		if (importdata > 0) {
			return eventreg;
		}
		return null;
	}

}
