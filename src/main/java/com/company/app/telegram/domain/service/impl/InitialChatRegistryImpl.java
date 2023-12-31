package com.company.app.telegram.domain.service.impl;

import com.company.app.core.tool.json.JsonTool;
import com.company.app.telegram.domain.entity.Chat;
import com.company.app.telegram.domain.service.api.ChatService;
import com.company.app.telegram.domain.service.api.InitialChatRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
public class InitialChatRegistryImpl implements InitialChatRegistry {

    @Value("classpath:telegram/init_chat.json")
    private Resource resource;

    @Autowired
    private JsonTool<Chat> jsonTool;
    @Autowired
    private ChatService chatService;

    @EventListener({ContextRefreshedEvent.class})
    @Override
    public void init() throws TelegramApiException {
        List<Chat> list = jsonTool.toJavaAsList(resource, Chat.class);
        chatService.saveAll(list);
    }

}
