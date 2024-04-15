package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoteReminderBot extends TelegramLongPollingBot {
    private Map<Long, List<String>> notesMap = new HashMap<>();
    private final Weather weather = new Weather();

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            String text = message.getText();
            long chatId = message.getChatId();
            long userId = message.getFrom().getId();

            if (text.equals("/start")) {
                String response = "Привет! Я бот помощник. Вот мои команды:\n\n" +
                        "Примечание: ОТДЕЛЬНО ССЫЛКИ НЕ РАБОТАЮТ!!!  Нужно писать запрос с ссылкой\n\n" +
                        "/start - Показать список команд.\n" +
                        "/note - Добавить заметку.\n" +
                        "/view - Просмотреть заметки.\n" +
                        "/delete +номер Удалить заметку.\n" +
                        "/deleteall - Удалить все заметки.\n" +
                        "/fullbot - Удалить бота.\n" +
                        "/weather +название города. Узнать погоду в твоем городе!";
                sendMsg(chatId, response);
            } else if (text.startsWith("/note")) {
                String note = text.substring(6).trim();
                List<String> userNotes = notesMap.getOrDefault(userId, new ArrayList<>());
                userNotes.add(note);
                notesMap.put(userId, userNotes);
                sendMsg(chatId, "Заметка сохранена: " + note);
            } else if (text.equals("/view")) {
                List<String> userNotes = notesMap.getOrDefault(userId, new ArrayList<>());
                if (!userNotes.isEmpty()) {
                    StringBuilder notes = new StringBuilder("Ваши заметки:\n");
                    for (int i = 0; i < userNotes.size(); i++) {
                        notes.append(i + 1).append(". ").append(userNotes.get(i)).append("\n");
                    }
                    sendMsg(chatId, notes.toString());
                } else {
                    sendMsg(chatId, "У вас нет заметок.");
                }

            } else if (text.startsWith("/delete")) {
                int noteNumber = 0;
                if (text.equals("/delete all")) {
                    notesMap.remove(userId);
                    sendMsg(chatId, "Все заметки удалены.");
                } else {
                    try {
                        noteNumber = Integer.parseInt(text.split(" ")[1].trim());
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                        sendMsg(chatId, "Некорректный номер заметки для удаления.");
                        return;

                    }
                }

                List<String> userNotes = notesMap.getOrDefault(userId, new ArrayList<>());
                if (noteNumber > 0 && noteNumber <= userNotes.size()) {
                    userNotes.remove(noteNumber - 1);
                    notesMap.put(userId, userNotes);
                    sendMsg(chatId, "Заметка номер " + noteNumber + " удалена.");
                }
            } else if (text.startsWith("/weather")) {
                String city = text.substring(8).trim(); // Удаляем "/weather" из строки и обрезаем пробелы
                String weatherInfo = weather.getWeather(city);
                sendMsg(chatId, weatherInfo);
            } else if (text.startsWith("/fullbot")) {
                sendMsg(chatId, "Ха, бота удалить могу только я.");
            }
        }
    }

    private void sendMsg(long chatId, String text) {
        try {
            execute(new SendMessage(String.valueOf(chatId), text));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    @Override
    public String getBotUsername() {
        return "fgfgffhgjfjhfjgfgjfjhfj_bot";
    }

    @Override
    public String getBotToken() {
        return "7173323660:AAHsIVnQPJ-AAST4TwZb_eg7bWiZiW1F-eE";
    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

        try {
            telegramBotsApi.registerBot(new NoteReminderBot());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}