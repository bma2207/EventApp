package com.axelor.event.web;

import com.axelor.apps.message.db.EmailAddress;
import com.axelor.apps.message.db.Message;
import com.axelor.apps.message.db.repo.EmailAccountRepository;
import com.axelor.apps.message.service.MessageService;
import com.axelor.event.db.Discount;
import com.axelor.event.db.Event;
import com.axelor.event.db.EventRegistration;
import com.axelor.event.service.EventServiceImp;
import com.axelor.exception.AxelorException;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;

public class EventController {
	@Inject
	private MessageService messageService;
	@Inject
	private EventServiceImp service;

	public void compute(ActionRequest request, ActionResponse response) {

		EventRegistration enEventRegistration = request.getContext().asType(EventRegistration.class);

		enEventRegistration = service.calculation(enEventRegistration);
		response.setValue("amount", enEventRegistration.getAmount());
	}

	public void totalDiscount(ActionRequest request, ActionResponse response) {

		Event event = request.getContext().asType(Event.class);

		event = service.discountCalculation(event);
		response.setValue("amountCollected", event.getAmountCollected());
		response.setValue("totalDiscount", event.getTotalDiscount());
	}

	public void eventSet(ActionRequest request, ActionResponse response) {
		EventRegistration registration = request.getContext().asType(EventRegistration.class);
		Event event = request.getContext().getParent().asType(Event.class);
		registration.setEvent(event);
		response.setValue("event", registration.getEvent());
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
	public void eventRegistrationListCalculationOnImport(ActionRequest request, ActionResponse response)
	{
		Event event=request.getContext().asType(Event.class);
		if(event.getEventRegistrationList() != null)
		{
			
			List<EventRegistration> enventRegistration=service.eventRegListCalculationOnimport(event);
			response.setValue("eventRegistrationList", enventRegistration);
		}
	}

	public void sendEmail(ActionRequest request, ActionResponse response) {

		Event event = request.getContext().asType(Event.class);
		List<EventRegistration> eventRegstrationsList = event.getEventRegistrationList();
		Set<EmailAddress> emailAddressSet = new HashSet<EmailAddress>();
		if (eventRegstrationsList != null) {

			for (EventRegistration eventRegistered : eventRegstrationsList) {

				if (eventRegistered.getEmail() != null && !eventRegistered.getIsEmailSent()) {
					EmailAddress emailAddress = new EmailAddress();
					emailAddress.setAddress(eventRegistered.getEmail());
					emailAddressSet.add(emailAddress);

					eventRegistered.setIsEmailSent(true);

				}

			}
			if (!emailAddressSet.isEmpty()) {
				System.out.println(emailAddressSet);
				Message message = new Message();
				message.setMailAccount(Beans.get(EmailAccountRepository.class).all().fetchOne());
				message.setToEmailAddressSet(emailAddressSet);
				message.setSubject("Regarding event Registration");
				message.setContent("Your Event has been registered successfully ");
				try {
					messageService.sendByEmail(message);
				} catch (MessagingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (AxelorException e) {
					e.printStackTrace();
				}

				response.setFlash("Email Sent successfully");
			}
		}

	}
}
