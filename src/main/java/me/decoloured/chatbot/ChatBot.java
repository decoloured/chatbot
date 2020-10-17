package me.decoloured.chatbot;

import java.util.UUID;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;

public class ChatBot {
  public final MinecraftClient client = MinecraftClient.getInstance();
  public final PlayerEntity player = client.player;
  private static ChatBot instance;

  public static ChatBot getInstance() {
    return instance;
  }

  public static void debug(MessageType messageType, Text message, UUID senderUuid) {
    String command = message.getString().substring(message.getString().indexOf(">") + 2);
    switch (command.substring(0, 2)) {
      case "!s":
        String[] scoreboardStrings = command.split(" ");
        MinecraftClient.getInstance().player.sendChatMessage("/scoreboard objectives setdisplay sidebar " + scoreboardStrings[1]);
        break;
      case "!b":
        MinecraftClient.getInstance().player.sendChatMessage(";" + command.substring(3));
      case "!r":
        MinecraftClient.getInstance().player.sendChatMessage(command.substring(3));
      case "!l":
      MinecraftClient.getInstance().player.sendChatMessage(String.format("XYZ: %.1f %.1f %.1f", MinecraftClient.getInstance().player.getX(), MinecraftClient.getInstance().player.getY(), MinecraftClient.getInstance().player.getZ()));
      MinecraftClient.getInstance().player.sendChatMessage(String.format("Health: %.1f", MinecraftClient.getInstance().player.getHealth()));
      default:
        break;
    }
  }

  public static void debugInfo(MessageType messageType, Text message, UUID senderUuid) {
    System.out.println("MESSAGETYPE: " + messageType.toString());
    System.out.println("MESSAGE: " + message.getString().substring(message.getString().indexOf(">") + 2));
    System.out.println("SENDER: " + senderUuid.toString() + " " + message.getString().substring(1, message.getString().indexOf(">")));
  }
}
