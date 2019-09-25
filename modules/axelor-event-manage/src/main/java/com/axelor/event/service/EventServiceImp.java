package com.axelor.event.service;

import com.axelor.apps.message.db.EmailAddress;
import com.axelor.apps.message.db.Message;
import com.axelor.apps.message.db.repo.EmailAccountRepository;
import com.axelor.apps.message.service.MessageService;
import com.axelor.event.db.Discount;
import com.axelor.event.db.Event;
import com.axelor.event.db.EventRegistration;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventServiceImp implements EventSevice {
	@Inject
	private MessageService messageService;

	@Override
	public EventRegistration eventRegListCalculationOnimport(EventRegistration eventRegistration, Event event) {

		BigDecimal fees = BigDecimal.ZERO;
		BigDecimal discountPer = BigDecimal.ZERO;
		BigDecimal feesAmount = BigDecimal.ZERO;
		BigDecimal amounts = event.getEventFees();
		BigDecimal value = BigDecimal.ZERO;
		Integer days = 0;
		fees = event.getEventFees();
		Integer durations = 0;
		LocalDate dateTo = event.getRegistrationCloseDate();
		Date date = Date.from(eventRegistration.getRegistrationDateT().atZone(ZoneId.systemDefault()).toInstant());
		LocalDate regdate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		Period registrationDate = Period.between(regdate, dateTo);
		durations = registrationDate.getDays();

		List<Discount> discountList = event.getDiscountList();
		if (event.getEventFees() == null) {
			amounts = fees;
		} else if (discountList.isEmpty()) {
			amounts = fees;
		} else {
			discountList.sort(Comparator.comparing(Discount::getBeforeDays));
			for (Discount discount : discountList) {
				days = discount.getBeforeDays();
				if (days <= durations) {
					discountPer = discount.getDiscountPercent();
					value = feesAmount.add(discountPer.multiply(fees)).divide(new BigDecimal(100));
					amounts = fees.subtract(value);

				}
			}
		}
		eventRegistration.setAmount(amounts);

		return eventRegistration;
	}

	@Override
	public Event discountCalculation(Event event) {

		BigDecimal totalAmount = BigDecimal.ZERO;
		BigDecimal totalDiscount = BigDecimal.ZERO;
		List<EventRegistration> eventRegistrationslist = event.getEventRegistrationList();
		if (event.getEventRegistrationList() != null) {

			for (EventRegistration eventRegistration : eventRegistrationslist) {
				totalAmount = totalAmount.add(eventRegistration.getAmount());
				if (eventRegistration.getAmount().equals(BigDecimal.ZERO)) {

				} else {
					totalDiscount = totalDiscount.add(event.getEventFees().subtract(eventRegistration.getAmount()));
				}
			}
			event.setAmountCollected(totalAmount);
			event.setTotalDiscount(totalDiscount);
		}
		return event;
	}

	@Override
	public Discount discountAmount(Event event, Discount discount) {

		BigDecimal fees = BigDecimal.ZERO;
		BigDecimal feesAmount = BigDecimal.ZERO;
		fees = event.getEventFees();
		BigDecimal percent = discount.getDiscountPercent();
		feesAmount = feesAmount.add(percent.multiply(fees)).divide(new BigDecimal(100));
		discount.setDiscountAmount(feesAmount);
		return discount;
	}

	@Override
	public List<EventRegistration> registrationListMail(Event event) {

		List<EventRegistration> eventRegistrations = new ArrayList<EventRegistration>();
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
							System.out.println("Something went wrong.");
						}
						eventRegistered.setIsEmailSent(true);
						eventRegistrations.add(eventRegistered);
					}
				}
			}
		}
		return eventRegistrations;
	}

}
