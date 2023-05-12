package com.company.app.telegram.component.binder.impl;

import com.company.app.exchangerate.controller.ExchangeRateController;
import com.company.app.exchangerate.domain.entity.ExchangeRate;
import com.company.app.telegram.component.TelegramFacade;
import com.company.app.telegram.component.binder.BinderContainer;
import com.company.app.telegram.component.binder.api.ExchangeRateBinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExchangeRateBinderImpl implements ExchangeRateBinder {

	private static final String TYPE = "EX";

	@Autowired
	ExchangeRateController exchangeRateController;
	@Autowired
	TelegramFacade telegramFacade;

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public void bind(BinderContainer binderContainer) {
		ExchangeRate last = exchangeRateController.getLast().getBody();
		telegramFacade.writeToTargetChat(binderContainer.getChat().getChatId(), last.getAliexpressExchangeRate());
	}
}