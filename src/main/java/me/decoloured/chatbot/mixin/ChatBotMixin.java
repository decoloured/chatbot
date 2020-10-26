package me.decoloured.chatbot.mixin;

import net.minecraft.client.gui.hud.ChatHudListener;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.decoloured.chatbot.ChatBot;

@Mixin(ChatHudListener.class)
public class ChatBotMixin {
	@Inject(method = "onChatMessage", at = @At("HEAD"))
	private void message(MessageType messageType, Text message, UUID senderUuid, CallbackInfo info) {
		if (message.getString().substring(message.getString().indexOf(">") + 2).startsWith("!")) {
			ChatBot.getInstance().command(messageType, message, senderUuid);
		} else {
			ChatBot.getInstance().debugInfo(messageType, message, senderUuid);
		}
	}
}
