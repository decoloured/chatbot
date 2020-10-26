package me.decoloured.chatbot.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.decoloured.chatbot.ChatBot;
import net.minecraft.client.network.ClientPlayNetworkHandler;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
  @Inject(method = "onWorldTimeUpdate", at = @At("RETURN"))
  private void onTimeUpdate(net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket packetIn, CallbackInfo ci) {
    ChatBot.getInstance().updateTPS(packetIn.getTime());
  }
}
