package com.axelor.event.web;

import com.axelor.event.db.Discount;
import com.axelor.event.db.Event;
import com.axelor.event.db.EventRegistration;
import com.axelor.event.service.EventRegistrationService;
import com.axelor.event.service.EventRegistrationServiceImp;
import com.axelor.event.service.EventServiceImp;
import com.axelor.inject.Beans;
import com.axelor.meta.db.MetaFile;
import com.axelor.meta.db.repo.MetaFileRepository;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class EventController {

	@Inject
	private EventServiceImp service;
	@Inject
	EventRegistrationServiceImp registrationService;

	public void computeEventRegistartionAmount(ActionRequest request, ActionResponse response) {

		EventRegistration enEventRegistration = request.getContext().asType(EventRegistration.class);
		enEventRegistration = service.eventRegListCalculationOnimport(enEventRegistration,
				enEventRegistration.getEvent());
		response.setValue("amount", enEventRegistration.getAmount());
	}

	public void calculateEventTotalDiscount(ActionRequest request, ActionResponse response) {

		Event event = request.getContext().asType(Event.class);
		event = service.discountCalculation(event);
		response.setValue("amountCollected", event.getAmountCollected());
		response.setValue("totalDiscount", event.getTotalDiscount());
	}

	public void amountDiscount(ActionRequest request, ActionResponse response) {
		Discount discount = request.getContext().asType(Discount.class);
		Event event = request.getContext().getParent().asType(Event.class);
		Discount amount = service.discountAmount(event, discount);
		response.setValue("discountAmount", amount.getDiscountAmount());
	}

	public void eventRegistartionList(ActionRequest request, ActionResponse response) {

		if (request.getContext().getParent() != null) {
			response.setValue("event", request.getContext().getParent().asType(Event.class));
			response.setAttr("event", "hidden", true);
		}

	}

	public void registrationListCalculationOnImport(ActionRequest request, ActionResponse response) {
		Event event = request.getContext().asType(Event.class);
		List<EventRegistration> eventRegstrationsList = event.getEventRegistrationList();
		List<EventRegistration> updatedList = new ArrayList<>();
		if (event.getEventRegistrationList() != null) {
			for (EventRegistration eventRegistered : eventRegstrationsList) {
				EventRegistration enventRegistration = service.eventRegListCalculationOnimport(eventRegistered, event);
				updatedList.add(enventRegistration);

			}
			response.setValue("eventRegistrationList", updatedList);
		}
	}

	public void sendEmailEventRegistration(ActionRequest request, ActionResponse response) {

		Event event = request.getContext().asType(Event.class);
		List<EventRegistration> eventRegstrationsList = service.registrationListMail(event);
		response.setValue("eventRegistrationList", eventRegstrationsList);
		response.setFlash("Email Sent successfully");

	}

	@Inject
	EventRegistrationService importEventRegistrationService;

	public void importRegistration(ActionRequest request, ActionResponse response) {

		@SuppressWarnings("unchecked")
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

	@Transactional
	public void manageTotalEntry(ActionRequest request, ActionResponse response) {
		EventRegistration eventRegistration = request.getContext().asType(EventRegistration.class);
		registrationService.manageTotalEntry(eventRegistration);
	}

	// this method help to import only under capacity record not much more

	public void csvImportCapacity(ActionRequest request, ActionResponse response) {
		Event event = request.getContext().asType(Event.class);
		List<EventRegistration> registrationList = event.getEventRegistrationList();
		List<EventRegistration> updateList=new ArrayList<>();
		if (event.getCapacity() < registrationList.size()) {
			int start = event.getCapacity();
			for (int i = 0; i < start; i++) {
				updateList.add(registrationList.get(i));
				
			}
			response.setValue("eventRegistrationList", updateList);
		}
		
	}

}
