package ru.proshik.applepriceparcer.bot;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.proshik.applepriceparcer.provider.ProviderFactory;
import ru.proshik.applepriceparcer.provider.ProviderUtils;
import ru.proshik.applepriceparcer.storage.Database;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ApplePricePriceBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final String botToken;

    private final Database db;

    public ApplePricePriceBot(Database db, String botUsername, String botToken) {
        this.db = db;
        this.botUsername = botUsername;
        this.botToken = botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {

            SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                    .setChatId(update.getMessage().getChatId());

            if (update.getMessage().isCommand()) {
                switch (update.getMessage().getText().split(" ")[0]) {
                    case "/start":
                        message.setText(commandInfo());
                        break;
                    case "/shops":
                        String shopsText = ProviderFactory.list().stream()
                                .map(s -> "Title: " + s.supplier().getTitle() + " - URL: " + s.supplier().getUrl())
                                .collect(Collectors.joining("", "", "\n"));

                        message.setText(shopsText);
                        break;
                    case "/read":
                        String readText = Stream.of(update.getMessage().getText().split(" "))
                                .skip(1)
                                .findFirst()
                                .map(title -> ProviderFactory.findByTitle(title)
                                        .map(screener -> ProviderUtils.buildAssortmentString(screener.screening()))
                                        .orElse("Title unrecognized."))
                                .orElse("Need set a argument for command. It is a title of shop.");

                        message.setText(readText);
                        break;
                    default:
                        message.setText(commandInfo());
                }
            } else {
                message.setText(commandInfo());
            }
            try {
                execute(message); // Call method to send the message
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Update not contains message");
        }
    }

    private String commandInfo() {
        StringBuilder startText = new StringBuilder("Bot for show info about prices on Apple products from several online shop in SpB\n");
        startText.append("/shops").append(" - ").append("list of shops for parsing").append("\n");
        startText.append("/read <shop_title>").append(" - ")
                .append("show current prices on assortment in this shop. Argument <shop_title> it is a title of show from result /shops command.")
                .append("\n");
        return startText.toString();
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onClosing() {
        System.out.println("Bot was closing");
    }
//
//
//    public InlineKeyboardMarkup keyboardAnswer(Update update, ClsQuest quest) {
//        final InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
//        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
//        for (ClsAnswer clsAnswer : quest.getClsAnswerCollection()) {
//            keyboard.add(Arrays.asList(buttonAnswer(clsAnswer)));
//        }
//        markup.setKeyboard(keyboard);
//        return markup;
//    }
//
//    public InlineKeyboardButton buttonAnswer(ClsAnswer clsAnswer) {
//        InlineKeyboardButton button = new InlineKeyboardButtonBuilder()
//                .setText(clsAnswer.getAnswerText())
//                .setCallbackData(new ActionBuilder(marshaller)
//                        .setName(GET_ANSWER)
//                        .setValue(clsAnswer.getId().toString())
//                        .asString())
//                .build();
//        return button;
//    }
}
