package com.axelor.event.service;

import com.axelor.event.db.Discount;
import com.axelor.event.db.Event;
import com.axelor.event.db.EventRegistration;

public interface EventSevice {
	public EventRegistration calculation(EventRegistration eventRegistration);
	public Event discountCalculation(Event event);
	public Discount discountAmount(Event event,Discount discount);
}
