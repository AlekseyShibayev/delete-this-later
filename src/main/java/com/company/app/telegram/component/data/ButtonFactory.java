package com.company.app.telegram.component.data;

import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ButtonFactory {

    private static final InlineKeyboardButton TG_OFF_BUTTON = new InlineKeyboardButton("Отключить уведомления");
    private static final InlineKeyboardButton TG_ON_BUTTON = new InlineKeyboardButton("Включить уведомления");
    private static final InlineKeyboardButton WB_BUTTON = new InlineKeyboardButton("Покажи что нашел на wildberries");
    private static final InlineKeyboardButton ER_BUTTON = new InlineKeyboardButton("Покажи последний найденный курс");
    private static final InlineKeyboardButton WB_SEARCH_BUTTON = new InlineKeyboardButton("В разработке: WB_SEARCH");

    public static InlineKeyboardMarkup inlineMarkup() {
        TG_OFF_BUTTON.setCallbackData("TG_OFF");
        TG_ON_BUTTON.setCallbackData("TG_ON");
        WB_BUTTON.setCallbackData("WB");
        ER_BUTTON.setCallbackData("EX");
        WB_SEARCH_BUTTON.setCallbackData("WB_SEARCH");

        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        rowsInLine.add(List.of(TG_OFF_BUTTON));
        rowsInLine.add(List.of(TG_ON_BUTTON));
        rowsInLine.add(List.of(WB_BUTTON));
        rowsInLine.add(List.of(ER_BUTTON));
        rowsInLine.add(List.of(WB_SEARCH_BUTTON));

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(rowsInLine);

        return markupInline;
    }
}
