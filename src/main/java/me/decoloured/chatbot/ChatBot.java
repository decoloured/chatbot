package me.decoloured.chatbot;

import java.util.UUID;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;

public class ChatBot {
  public static final MinecraftClient client = MinecraftClient.getInstance();
  public static final ClientPlayerEntity player = client.player;
  public boolean logoutPrimed = false;
  private static ChatBot instance;

  public static ChatBot getInstance() {
    return instance;
  }

  public static void command(MessageType messageType, Text message, UUID senderUuid) {
    debugInfo(messageType, message, senderUuid);
    String command = message.getString().substring(message.getString().indexOf(">") + 2);
    String senderName = message.getString().substring(1, message.getString().indexOf(">"));
    String[] commandStrings = command.split(" ");
    switch (commandStrings[0]) {
      case "!s": case "!scoreboard":
        if (commandStrings.length == 1) {
          player.sendChatMessage("/scoreboard objectives setdisplay sidebar");
        } else {
          player.sendChatMessage("/scoreboard objectives setdisplay sidebar " + commandStrings[1]);
        }
        break;
      case "!goto":
        try {
          player.sendChatMessage(";goto " + concatCommand(commandStrings));
        } catch (Exception e) {
          player.sendChatMessage("please enter coordinates");
        }
        break;
      case "!follow": case "!f":
        try {
          player.sendChatMessage(";follow player " + concatCommand(commandStrings));
        } catch (Exception e) {
          player.sendChatMessage("please enter a player name");
        }
        break;
      case "!r": case "!reply": case "!echo":
        try {
          if (commandStrings[1].startsWith("/")) {
            player.sendChatMessage("access denied");
          } else {
            player.sendChatMessage(concatCommand(commandStrings));
          }
        } catch (Exception e) {
          player.sendChatMessage("enter message to echo");
        }
        break;
      case "!loc": case "!l": case "!location":
        player.sendChatMessage(String.format("xyz: %.1f %.1f %.1f", 
          player.getX(), player.getY(), player.getZ()));
        break;
      case "!debug":
        player.sendChatMessage(senderName);
        break;
      case "!item": case "!i":
        ItemStack itemStack = player.getMainHandStack();
        if (itemStack.isDamageable()) {
          int itemStackDurability = itemStack.getMaxDamage() - itemStack.getDamage();
          player.sendChatMessage(String.format("%s: %s/%s (%.1f%%)", itemStack.getItem().toString(), 
            itemStackDurability, itemStack.getMaxDamage(), (float) (itemStackDurability / itemStack.getMaxDamage()) * 100));
        } else {
          player.sendChatMessage(String.format("%s", itemStack.getName().asOrderedText().toString()));
        }
        break;
      case "!h": case "!help":
        if (commandStrings.length == 1) {
          player.sendChatMessage("commands: !scoreboard [objective] - !goto [x] [y] [z] - !echo [text] - !location - !help");
        } else {
          switch (commandStrings[1].replace("!", "")) {
            case "help":
              player.sendChatMessage("usage: !help [command]");
              player.sendChatMessage("shows all commands or description on command");
              break;
            case "scoreboard": case "s":
              player.sendChatMessage("usage: !scoreboard [objective]");
              player.sendChatMessage("alias: !s");
              player.sendChatMessage("changes sidebar objective, hides sidebar if no objective is given");
              break;
            case "goto":
              player.sendChatMessage("usage: !goto [x] [y] [z], !goto [x] [z], !goto [y]");
              player.sendChatMessage("sends " + player.getEntityName() + " to said coordinates");
              break;
            case "follow": case "f":
              player.sendChatMessage("usage: !follow [player] [player2...]");
              player.sendChatMessage("alias: !f");
              player.sendChatMessage("sends " + player.getEntityName() + " to said player(s)");
              break;
            case "echo": case "reply": case "r":
              player.sendChatMessage("usage: !echo [message]");
              player.sendChatMessage("alias: !reply, !r");
              player.sendChatMessage("echos a message, cannot enter commands");
              break;
            case "location": case "loc": case "l":
              player.sendChatMessage("usage: !location");
              player.sendChatMessage("alias: !loc, !l");
              player.sendChatMessage("sends location of " + player.getEntityName());
              break;
            default:
              player.sendChatMessage("command not found: " + commandStrings[1]);
              break;
          }
        }
        break;
      default:
        player.sendChatMessage("command not found: " + commandStrings[0]);
        break;
    }
  }

  public static String concatCommand(String[] array) {
    String result = "";
    for (int i = 1; i < array.length; i++) {
      result += array[i] + " ";
    }
    return result;
  }

  public static void debugInfo(MessageType messageType, Text message, UUID senderUuid) {
    System.out.println("MESSAGETYPE: " + messageType.toString());
    System.out.println("MESSAGE: " + message.getString());
    System.out.println("SENDER: " + senderUuid.toString());
  }
}
