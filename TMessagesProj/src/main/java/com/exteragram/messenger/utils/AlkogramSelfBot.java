package com.exteragram.messenger.utils;

import android.text.TextUtils;
import com.exteragram.messenger.ExteraConfig;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;

public class AlkogramSelfBot {

    public static final String ALKO_DESCRIPTION =
            "🍷 **Alkogram Client**\n" +
            "Мощный модифицированный клиент Telegram.\n" +
            "• Ghost Mode (невидимка & анти-набор)\n" +
            "• Bypass Restrictions (скриншоты & сохранение)\n" +
            "• Anti-Delete сообщений\n" +
            "• Встроенный движок селф-бота";

    /**
     * Checks whether the outgoing text is a self-bot command.
     * If handled, executes the command action and returns true (preventing normal message sending).
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

            // Send description message directly
            accountInstance.getSendMessagesHelper().sendMessage(
                    ALKO_DESCRIPTION,
                    dialogId,
                    replyingMessageObject,
                    threadMessage,
                    null,
                    false,
                    null,
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

