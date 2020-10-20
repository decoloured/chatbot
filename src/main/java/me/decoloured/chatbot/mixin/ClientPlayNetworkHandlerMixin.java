package me.decoloured.chatbot.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.decoloured.chatbot.ChatBotConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.text.Text;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
  private ChatBotConfig config = new ChatBotConfig();
  private final MinecraftClient client = MinecraftClient.getInstance();
  private float lastHealth;

  @Environment(EnvType.CLIENT)
  @Inject(method = "onHealthUpdate", at = @At("TAIL"))
  private void onPlayerHealthUpdate(HealthUpdateS2CPacket packet, CallbackInfo ci) {
    if (packet.getHealth() < this.lastHealth && packet.getHealth() < this.config.logoutHealth()) {
      this.client.getNetworkHandler().getConnection().disconnect(Text.of(String.format("HealthDisconnect - %.1f health remaining", packet.getHealth())));
    }
    lastHealth = packet.getHealth();
  }
}
