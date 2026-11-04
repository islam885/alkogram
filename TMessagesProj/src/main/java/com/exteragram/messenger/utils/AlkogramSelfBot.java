package com.exteragram.messenger.utils;

import android.text.TextUtils;
import com.exteragram.messenger.ExteraConfig;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;

public class AlkogramSelfBot {

    public static final String ALKO_DESCRIPTION =
            "🍷 **AlkoGram** — `v1.0`\n" +
            "__The Next-Gen Telegram Experience__\n\n" +
            "🛡️ **Ghost Mode**\n" +
            " ├ `Скрывать прочтение` — чтение без галочек\n" +
            " └ `Скрывать набор` — без статуса «печатает...»\n\n" +
            "🔓 **Bypass Restrictions**\n" +
            " ├ `Скриншоты везде` — обход FLAG_SECURE\n" +
            " ├ `Обход noforwards` — копирование & пересылка\n" +
            " └ `Вечные медиа` — без таймера самоуничтожения\n\n" +
            "⚡ **Core & Safety**\n" +
            " ├ `Anti-Delete` — удалённые сообщения не пропадут\n" +
            " └ `Built-in Userbot` — мгновенные локальные команды\n\n" +
            "🔗 **GitHub:** [islam885/alkogram](https://github.com/islam885/alkogram)";

    /**
     * Checks whether the outgoing text is a self-bot command.
     * If handled, parses markdown entities and sends the formatted message directly.
     */
    public static boolean handleOutgoingCommand(
            int currentAccount,
            CharSequence text,
            long dialogId,
            MessageObject replyingMessageObject,
            MessageObject threadMessage,
            boolean notify,
            int scheduleDate) {

        if (TextUtils.isEmpty(text) || !ExteraConfig.alkoSelfBotEnabled) {
            return false;
        }

        String cmd = text.toString().trim();
        if (".alko".equalsIgnoreCase(cmd)) {
            AccountInstance accountInstance = AccountInstance.getInstance(currentAccount);

            CharSequence[] messageHolder = new CharSequence[]{ ALKO_DESCRIPTION };
            ArrayList<TLRPC.MessageEntity> entities = MediaDataController.getInstance(currentAccount).getEntities(messageHolder, true);

            accountInstance.getSendMessagesHelper().sendMessage(
                    messageHolder[0].toString(),
                    dialogId,
                    replyingMessageObject,
                    threadMessage,
                    null,
                    false,
                    entities,
                    null,
                    null,
                    notify,
                    scheduleDate,
                    null,
                    false
            );
            return true;
        }

        return false;
    }
}
