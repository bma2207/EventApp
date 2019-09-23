package com.axelor.event.service;

import java.util.List;

import com.axelor.event.db.Discount;
import com.axelor.event.db.Event;
import com.axelor.event.db.EventRegistration;

public interface EventSevice {
	public EventRegistration amountCalculation(EventRegistration eventRegistration);
	public Event discountCalculation(Event event);
	public Discount discountAmount(Event event,Discount discount);
	public List<EventRegistration>  eventRegListCalculationOnimport(Event event);
}
