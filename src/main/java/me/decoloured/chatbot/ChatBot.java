package me.decoloured.chatbot;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
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
  private static boolean debug;

  public static ChatBot getInstance() {
    return instance;
  }

  public static void command(MessageType messageType, Text message, UUID senderUuid) {
    //debugInfo(messageType, message, senderUuid);
    Random random = new Random();
    String command = message.getString().substring(message.getString().indexOf(">") + 2);
    String senderName = message.getString().substring(1, message.getString().indexOf(">"));
    String[] commandStrings = command.split(" ");
    List<AbstractClientPlayerEntity> players = client.world.getPlayers();
    Collections.sort(players, new Comparator<AbstractClientPlayerEntity>(){
      @Override
      public int compare(AbstractClientPlayerEntity p1, AbstractClientPlayerEntity p2) {
          return Math.round(p1.distanceTo(client.player) - p2.distanceTo(client.player));
      }
    });
    if (debug) {
      debugInfo(messageType, message, senderUuid, true);
      debug = false;
    }
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
      case "!cancel":
      case "!c":
        p.sendChatMessage(";cancel");
        p.sendChatMessage("cancelled operation");
        break;
      case "!follow":
      case "!f":
        if (players.toString().contains(commandStrings[1])) {
          p.sendChatMessage(";follow player " + commandStrings[1]);
          p.sendChatMessage("following player " + commandStrings[1]);
        } else {
          p.sendChatMessage(commandStrings[1] + " is not in close proximity of " + p.getEntityName());
        }
        break;
      case "!r":
      case "!reply":
      case "!echo":
        try {
          if (commandStrings[1].startsWith("/")) {
            p.sendChatMessage("echo: no access to game commands");
          } else {
            p.sendChatMessage(concatCommand(commandStrings));
          }
        } catch (Exception e) {
          p.sendChatMessage("echo: enter message");
        }
        break;
      case "!location":
      case "!loc":
      case "!l":
        p.sendChatMessage(String.format("xyz: %.1f %.1f %.1f", p.getX(), p.getY(), p.getZ()));
        break;
      case "!debug":
        p.sendChatMessage("next message will be logged.");
        debug = true;
        break;
      case "!item":
      case "!i":
        ItemStack itemStack = p.getMainHandStack();
        if (itemStack.isDamageable()) {
          int itemStackDurability = itemStack.getMaxDamage() - itemStack.getDamage();
          p.sendChatMessage(String.format("%s: %s/%s [%.1f%%]", itemStack.getItem().toString(), itemStackDurability,
              itemStack.getMaxDamage(), (float) (itemStackDurability / itemStack.getMaxDamage()) * 100));
        } else {
          p.sendChatMessage(String.format("%s", itemStack.getItem().toString()));
        }
        break;
      case "!nearby":
      case "near":
      case "n":
        if (players.size() == 1) {
          p.sendChatMessage("nearby: nobody is nearby :(");
        }
        for (AbstractClientPlayerEntity playerEntity : players) {
          if (playerEntity.getEntityName() != p.getEntityName()) {
            p.sendChatMessage(String.format("[%.0f] %s %.0fm", playerEntity.getHealth(), playerEntity.getEntityName(), playerEntity.distanceTo(p)));
          }
        }
        break;
      case "!ping":
      case "!p":
        if (commandStrings.length == 1) {
          p.sendChatMessage(
              senderName + "\'s ping: [" + client.getNetworkHandler().getPlayerListEntry(senderName).getLatency() + "ms]");
        } else {
          try {
            p.sendChatMessage(commandStrings[1] + " ping: ["
                + client.getNetworkHandler().getPlayerListEntry(commandStrings[1]).getLatency() + "ms]");
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
              if (commandStrings[1].startsWith("o")) {
                p.sendChatMessage(String.format("portal nether coordinates: %.0f %s %.0f", Float.parseFloat(commandStrings[2]) / 8,
                    commandStrings[3], Float.parseFloat(commandStrings[2]) / 8));
              } else if (commandStrings[1].startsWith("n")) {
                p.sendChatMessage(String.format("portal overworld coordinates: %.0f %s %.0f", Float.parseFloat(commandStrings[2]) * 8,
                    commandStrings[3], Float.parseFloat(commandStrings[2]) * 8));
              } else {
                p.sendChatMessage("portal: invalid dimension \'" + commandStrings[1] + "\'");
                p.sendChatMessage(commandStrings[1]);
              }
            } catch (Exception e) {
              p.sendChatMessage("portal: please enter coordinates");
            }

        }
        break;
      case "!sus":
        if (commandStrings.length == 1) {
          p.sendChatMessage("amogus");
        } else {
          switch (commandStrings[1].toLowerCase()) {
            case "decoloured":
              p.sendChatMessage("trollge_v5.exe");
              break;
            case "jamaz52":
              p.sendChatMessage("sexy and hot");
              break;
            case "petrine51":
              p.sendChatMessage("tax evader");
              break;
            case "epic_highfive":
              p.sendChatMessage("racist");
              break;
            case "27christy":
              p.sendChatMessage("welcoming of lgbt people");
              break;
            case "conmar2004":
              p.sendChatMessage("gamer");
              break;
            case "moistlettuce8888":
              p.sendChatMessage("cringe");
              break;
            default:
              p.sendChatMessage("sus");
              break;
          }
        }
        break;
      case "!blackjack":
       p.sendChatMessage("you have " + (2 + random.nextInt(19)));
       p.sendChatMessage("dealer has blackjack, dealer wins!");
       break;
      case "!threaten":
        for (AbstractClientPlayerEntity playerEntity : players) {
          if (playerEntity.distanceTo(p) < 16 && p.getEntityName() != playerEntity.getEntityName()) {
            p.sendChatMessage("/msg " + playerEntity.getEntityName() + " ominous");
          }
        }
        break;
      case "!bible":
        p.sendChatMessage("And he called the place Massah and Meribah because the Israelites quarreled and because they tested the Lord saying, \"Is the Lord among us or not?\" (Exodus 17:7)");
        break;
      case "!h":
      case "!help":
        if (commandStrings.length == 1) {
          p.sendChatMessage("commands: !scoreboard - !goto - !echo - !location - !item - !nearby - !ping - !tps - !portal - !sus - !bible - !threaten - !blackjack - !help");
          p.sendChatMessage("use !help [command] for more information");
        } else {
          switch (commandStrings[1].replace("!", "")) {
            case "help":
            case "h":
              p.sendChatMessage("usage: !help [command]");
              p.sendChatMessage("alias: !h");
              p.sendChatMessage("shows all commands or description on command");
              break;
            case "scoreboard":
            case "s":
              p.sendChatMessage("usage: !scoreboard [objective]");
              p.sendChatMessage("alias: !s");
              p.sendChatMessage("changes sidebar objective, hides sidebar if no objective is given");
              break;
            case "goto":
              p.sendChatMessage("usage: !goto [x] [y] [z], !goto [x] [z], !goto [y]");
              p.sendChatMessage("sends " + p.getEntityName() + " to said coordinates");
              break;
            case "follow":
            case "f":
              p.sendChatMessage("usage: !follow [player] [player2...]");
              p.sendChatMessage("alias: !f");
              p.sendChatMessage("sends " + p.getEntityName() + " to said player(s)");
              break;
            case "echo":
            case "reply":
            case "r":
              p.sendChatMessage("usage: !echo [message]");
              p.sendChatMessage("alias: !reply, !r");
              p.sendChatMessage("echos a message, cannot enter commands");
              break;
            case "location":
            case "loc":
            case "l":
              p.sendChatMessage("usage: !location");
              p.sendChatMessage("alias: !loc, !l");
              p.sendChatMessage("returns location of " + p.getEntityName());
              break;
            case "item":
            case "i":
              p.sendChatMessage("usage: !item");
              p.sendChatMessage("alias: !i");
              p.sendChatMessage("returns currently held item information from " + p.getEntityName());
              break;
            case "nearby":
            case "near":
            case "n":
              p.sendChatMessage("usage: !nearby");
              p.sendChatMessage("alias: !near, !n");
              p.sendChatMessage("returns nearby players and their health, name and distance from " + p.getEntityName());
              break;
            case "ping":
            case "p":
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
              break;
            case "sus":
              p.sendChatMessage("usage: !sus [player]");
              p.sendChatMessage("like among us....... in minecraft???");
              break;
            case "bible":
              p.sendChatMessage("usage: !bible");
              p.sendChatMessage("sends a random bible verse in chat");
              break;
            case "threaten":
              p.sendChatMessage("usage: !threaten");
              p.sendChatMessage("go away go away go away go away go away go away go away go away go away go away go away go away go away go away go away ");
              break;
            case "blackjack":
              p.sendChatMessage("usage: !blackjack");
              p.sendChatMessage("starts a fair game of blackjack");
              break;
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

  public static String concatCommand(String[] array) {
    String result = "";
    for (int i = 1; i < array.length; i++) {
      result += array[i] + " ";
    }
    return result;
  }

  public static void debugInfo(MessageType messageType, Text message, UUID senderUuid) {
    debugInfo(messageType, message, senderUuid, false);
  }

  public static void debugInfo(MessageType messageType, Text message, UUID senderUuid, boolean chat) {
    if (chat) {
      p.sendChatMessage("MESSAGETYPE: " + messageType.toString());
      p.sendChatMessage("MESSAGE: " + message.getString());
      p.sendChatMessage("SENDER: " + senderUuid.toString());
    } else {
      System.out.println("MESSAGETYPE: " + messageType.toString());
      System.out.println("MESSAGE: " + message.getString());
      System.out.println("SENDER: " + senderUuid.toString());
    }
  }

  public static void updateTPS(long totalWorldTime) {
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
