package com.axelor.event.service;

import com.axelor.event.db.Discount;
import com.axelor.event.db.Event;
import com.axelor.event.db.EventRegistration;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class EventServiceImp implements EventSevice {

	@Override
	public EventRegistration amountCalculation(EventRegistration eventRegistration, Event event) {
		
		BigDecimal fees = BigDecimal.ZERO;
		BigDecimal discountPer = BigDecimal.ZERO;
		BigDecimal feesAmount = BigDecimal.ZERO;
		BigDecimal amounts = BigDecimal.ZERO;
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
				if (days >= durations) {
					discountPer = discount.getDiscountPercent();
					value = feesAmount.add(discountPer.multiply(fees)).divide(new BigDecimal(100));
					amounts = fees.subtract(value);
					break;
				} else {
					amounts = fees;
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
	public List<EventRegistration> eventRegListCalculationOnimport(Event event) {
		BigDecimal fees = BigDecimal.ZERO;
		BigDecimal discountPer = BigDecimal.ZERO;
		BigDecimal feesAmount = BigDecimal.ZERO;
		BigDecimal amounts = BigDecimal.ZERO;
		BigDecimal value = BigDecimal.ZERO;
		Integer days = 0;
		List<EventRegistration> eventRegistrations = new ArrayList<EventRegistration>();

		for (EventRegistration eventRegistration : event.getEventRegistrationList()) {
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
					if (days >= durations) {
						discountPer = discount.getDiscountPercent();
						value = feesAmount.add(discountPer.multiply(fees)).divide(new BigDecimal(100));
						amounts = fees.subtract(value);
						break;

					} else {
						amounts = fees;
					}
				}
			}
			eventRegistration.setAmount(amounts);
			eventRegistrations.add(eventRegistration);
		}
		return eventRegistrations;
	}

}
