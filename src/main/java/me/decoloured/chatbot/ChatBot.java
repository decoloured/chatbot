package me.decoloured.chatbot;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;

public class ChatBot {
  public static final MinecraftClient client = MinecraftClient.getInstance();
  public static final ClientPlayerEntity p = client.player;
  private static ChatBot instance;
  public static double serverTPS;
  public static double serverMSPT;
  private static long lastServerTick;
  private static long lastServerTimeUpdate;

  public static ChatBot getInstance() {
    return instance;
  }

  public void command(MessageType messageType, Text message, UUID senderUuid) {
    debugInfo(messageType, message, senderUuid);
    String command = message.getString().substring(message.getString().indexOf(">") + 2);
    String senderName = message.getString().substring(1, message.getString().indexOf(">"));
    String[] commandStrings = command.split(" ");
    List<AbstractClientPlayerEntity> players = client.world.getPlayers();
    Collections.sort(players, new Comparator<AbstractClientPlayerEntity>(){
      @Override
      public int compare(AbstractClientPlayerEntity p1, AbstractClientPlayerEntity p2) {
          return Math.round(p2.distanceTo(client.player) - p1.distanceTo(client.player));
      }
    });
    switch (commandStrings[0]) {
      case "!s": case "!scoreboard":
        if (commandStrings.length == 1) {
          p.sendChatMessage("/scoreboard objectives setdisplay sidebar");
        } else {
          p.sendChatMessage("/scoreboard objectives setdisplay sidebar " + commandStrings[1]);
        }
        break;
      case "!goto":
        try {
          p.sendChatMessage(";goto " + concatCommand(commandStrings));
        } catch (Exception e) {
          p.sendChatMessage("goto: please enter coordinates");
        }
        break;
      case "!follow": case "!f":
        if (players.toString().contains(commandStrings[1])) {
          p.sendChatMessage(";follow player " + commandStrings[1]);
          p.sendChatMessage("following player " + commandStrings[1]);
        } else {
          p.sendChatMessage(commandStrings[1] + " is not in close proximity of " + p.getEntityName());
        }
        break;
      case "!r": case "!reply": case "!echo":
        try {
          if (commandStrings[1].startsWith("/")) {
            p.sendChatMessage("echo: access denied");
          } else {
            p.sendChatMessage(concatCommand(commandStrings));
          }
        } catch (Exception e) {
          p.sendChatMessage("echo: enter message");
        }
        break;
      case "!loc": case "!l": case "!location":
        p.sendChatMessage(String.format("xyz: %.1f %.1f %.1f", p.getX(), p.getY(), p.getZ()));
        break;
      case "!debug":
        p.sendChatMessage(senderName);
        break;
      case "!item": case "!i":
        ItemStack itemStack = p.getMainHandStack();
        if (itemStack.isDamageable()) {
          int itemStackDurability = itemStack.getMaxDamage() - itemStack.getDamage();
          p.sendChatMessage(String.format("%s: %s/%s [%.1f%%]", itemStack.getItem().toString(), 
            itemStackDurability, itemStack.getMaxDamage(), (float) (itemStackDurability / itemStack.getMaxDamage()) * 100));
        } else {
          p.sendChatMessage(String.format("%s", itemStack.getName().asOrderedText().toString()));
        }
        break;
      case "!nearby":
        if (players.size() == 1) {
          p.sendChatMessage("nearby: nobody is nearby :(");
        }
        for (AbstractClientPlayerEntity playerEntity : players) {
          if (playerEntity.getEntityName() != p.getEntityName()) {
            p.sendChatMessage(String.format("[%.0f] %s %.0fm", p.getHealth(), p.getEntityName(), p.distanceTo(p)));
          }
        }
        break;
      case "!ping": case "!p":
        if (commandStrings.length == 1) {
          p.sendChatMessage(senderName + " ping: [" + client.getNetworkHandler().getPlayerListEntry(senderName).getLatency() + "ms]");
        } else {
          try {
            p.sendChatMessage(commandStrings[1] + " ping: [" + client.getNetworkHandler().getPlayerListEntry(commandStrings[1]).getLatency() + "ms]");
          } catch (Exception e) {
            p.sendChatMessage("ping: player not found \'" + commandStrings[1] + "\'");
          }
        }
        break;
      case "!tps":
        p.sendChatMessage(String.format("server tps: [%.2f]", serverTPS));
        break;
      case "!portal":
        switch (commandStrings.length) {
          case 1:
            p.sendChatMessage("portal: please enter dimension");
            break;
          default:
            try {
              if (commandStrings[1].toLowerCase() == "innether") {
                p.sendChatMessage(String.format("%.0f %s %.0f", 
                  Float.parseFloat(commandStrings[2]) / 8, 
                  commandStrings[3], 
                  Float.parseFloat(commandStrings[2]) / 8));
              } else if (commandStrings[1].toLowerCase() == "inoverworld") {
                p.sendChatMessage(String.format("%.0f %s %.0f", 
                  Float.parseFloat(commandStrings[2]) * 8, 
                  commandStrings[3], 
                  Float.parseFloat(commandStrings[2]) * 8));
              } else {
                p.sendChatMessage("portal: invalid dimension \'" + commandStrings[1] + "\'");
              }
            } catch (Exception e) {
              p.sendChatMessage("portal: please enter coordinates");
            }
            
        }
        break;
      case "!h": case "!help":
        if (commandStrings.length == 1) {
          p.sendChatMessage("commands: !scoreboard - !goto - !echo - !location - !item - !near - !ping - !tps - !portal - !help");
        } else {
          switch (commandStrings[1].replace("!", "")) {
            case "help": case "h":
              p.sendChatMessage("usage: !help [command]");
              p.sendChatMessage("alias: !h");
              p.sendChatMessage("shows all commands or description on command");
              break;
            case "scoreboard": case "s":
              p.sendChatMessage("usage: !scoreboard [objective]");
              p.sendChatMessage("alias: !s");
              p.sendChatMessage("changes sidebar objective, hides sidebar if no objective is given");
              break;
            case "goto":
              p.sendChatMessage("usage: !goto [x] [y] [z], !goto [x] [z], !goto [y]");
              p.sendChatMessage("sends " + p.getEntityName() + " to said coordinates");
              break;
            case "follow": case "f":
              p.sendChatMessage("usage: !follow [player] [player2...]");
              p.sendChatMessage("alias: !f");
              p.sendChatMessage("sends " + p.getEntityName() + " to said player(s)");
              break;
            case "echo": case "reply": case "r":
              p.sendChatMessage("usage: !echo [message]");
              p.sendChatMessage("alias: !reply, !r");
              p.sendChatMessage("echos a message, cannot enter commands");
              break;
            case "location": case "loc": case "l":
              p.sendChatMessage("usage: !location");
              p.sendChatMessage("alias: !loc, !l");
              p.sendChatMessage("returns location of " + p.getEntityName());
              break;
            case "item": case "i":
              p.sendChatMessage("usage: !item");
              p.sendChatMessage("alias: !i");
              p.sendChatMessage("returns currently held item information from " + p.getEntityName());
              break;
            case "!nearby": 
              p.sendChatMessage("usage: !nearby");
              p.sendChatMessage("returns nearby players and their health, name and distance from " + p.getEntityName());
              break;
            case "!ping": case "!p":
              p.sendChatMessage("usage: !ping [player]");
              p.sendChatMessage("alias: !p");
              p.sendChatMessage("returns ping of player");
              break;
            case "tps":
              p.sendChatMessage("usage: !tps");
              p.sendChatMessage("returns server's current tps (ticks per second)");
              break;
            case "portal":
              p.sendChatMessage("usage: !portal [dimension] [x] [y] [z]");
              p.sendChatMessage("returns portal coordinates for portal linking");
            default:
              p.sendChatMessage("help: command not found: " + commandStrings[1]);
              break;
          }
        }
        break;
      default:
        p.sendChatMessage("command not found: " + commandStrings[0]);
        break;
    }
  }

  public String concatCommand(String[] array) {
    String result = "";
    for (int i = 1; i < array.length; i++) {
      result += array[i] + " ";
    }
    return result;
  }

  public void debugInfo(MessageType messageType, Text message, UUID senderUuid) {
    System.out.println("MESSAGETYPE: " + messageType.toString());
    System.out.println("MESSAGE: " + message.getString());
    System.out.println("SENDER: " + senderUuid.toString());
  }

  public void updateTPS(long totalWorldTime) {
    long currentTime = System.nanoTime();
    long elapsedTicks = totalWorldTime - ChatBot.lastServerTick;
    if (elapsedTicks > 0) {
        ChatBot.serverMSPT = ((double) (currentTime - ChatBot.lastServerTimeUpdate) / (double) elapsedTicks) / 1000000D;
        ChatBot.serverTPS = ChatBot.serverMSPT <= 50 ? 20D : (1000D / ChatBot.serverMSPT);
    }
    ChatBot.lastServerTick = totalWorldTime;
    ChatBot.lastServerTimeUpdate = currentTime;
}
}
