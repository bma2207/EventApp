package com.axelor.event.web;

import com.axelor.apps.message.db.EmailAddress;
import com.axelor.apps.message.db.Message;
import com.axelor.apps.message.db.repo.EmailAccountRepository;
import com.axelor.apps.message.service.MessageService;
import com.axelor.event.db.Discount;
import com.axelor.event.db.Event;
import com.axelor.event.db.EventRegistration;
import com.axelor.event.service.EventRegistrationService;
import com.axelor.event.service.EventServiceImp;
import com.axelor.inject.Beans;
import com.axelor.meta.db.MetaFile;
import com.axelor.meta.db.repo.MetaFileRepository;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class EventController {
	@Inject
	private MessageService messageService;
	@Inject
	private EventServiceImp service;

	public void computeEventRegistartionAmount(ActionRequest request, ActionResponse response) {

		EventRegistration enEventRegistration = request.getContext().asType(EventRegistration.class);
		Event event=request.getContext().getParent().asType(Event.class);
		enEventRegistration = service.amountCalculation(enEventRegistration,event);
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

	public void eventRegistrationListCalculationOnImport(ActionRequest request, ActionResponse response) {
		Event event = request.getContext().asType(Event.class);
		if (event.getEventRegistrationList() != null) {

			List<EventRegistration> enventRegistration = service.eventRegListCalculationOnimport(event);
			response.setValue("eventRegistrationList", enventRegistration);
		}
	}

	public void sendEmailEventRegistration(ActionRequest request, ActionResponse response) {

		Event event = request.getContext().asType(Event.class);
		List<EventRegistration> eventRegstrationsList = event.getEventRegistrationList();
		Set<EmailAddress> emailAddressSet = new HashSet<EmailAddress>();
		if (eventRegstrationsList != null) {

			for (EventRegistration eventRegistered : eventRegstrationsList) {

				if (eventRegistered.getEmail() != null && !eventRegistered.getIsEmailSent()) {
					EmailAddress emailAddress = new EmailAddress();
					emailAddress.setAddress(eventRegistered.getEmail());
					emailAddressSet.add(emailAddress);
					if (!emailAddressSet.isEmpty()) {
						System.out.println(emailAddressSet);
						Message message = new Message();
						message.setMailAccount(Beans.get(EmailAccountRepository.class).all().fetchOne());
						message.setToEmailAddressSet(emailAddressSet);
						message.setSubject("Regarding event Registration");
						message.setContent("Hello Dear " + eventRegistered.getName() + " .You are take part in "
								+ event.getReference() + " Event.the event fees is " + event.getEventFees()
								+ " Rs. we are happy to inform you. Your Event has been registered successfully  "
								+ "Thanks");
						try {
							messageService.sendByEmail(message);
						} catch (Exception e) {
							
						}
						response.setFlash("Email Sent successfully");
						eventRegistered.setIsEmailSent(true);
					}
					

				}

			}

		}

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
}
