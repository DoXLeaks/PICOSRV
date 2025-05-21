package kr.rth.picoserver.Money;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.api.HologramData;
import kr.rth.picoserver.Database;
import kr.rth.picoserver.PICOSERVER;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.*;

import static java.lang.Integer.parseInt;
import static kr.rth.picoserver.util.numberWithComma.numberWithComma;

public class CommandListener implements CommandExecutor {

    public CommandListener() {
//        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(PICOSERVER.getInstance(), () -> {
//            HashMap<OfflinePlayer, Integer> hash = new HashMap<>();
//            for( var i : SuperiorSkyblockAPI.getGrid().getIslands()) {
//                hash.put(i.getOwner().asOfflinePlayer(), i.getWorth().intValue());
//            }
//            HashMap<OfflinePlayer, Integer> sortedhash = new HashMap<>();
//
//            List<Map.Entry<OfflinePlayer, Integer>> list = new ArrayList<>(hash.entrySet());
//            list.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));
//
//            ArrayList<String> q = new ArrayList<>();
//            q.add("");
//            q.add("");
//            q.add("");
//            Integer idx = 1;
//            for (Map.Entry<OfflinePlayer, Integer> entry : list) {
//                if(idx == 11) {
////                    return;
//                    break;
//                }
//                String colorCode = "<#9ADC6B>";
//                if(idx == 1) {
//                    colorCode = "<#DC3223>";
//                }
//                if(idx == 2) {
//                    colorCode = "<#FF940A>";
//                }
//                if(idx == 3) {
//                    colorCode = "<#DCC221>";
//                }
//
//                q.add("<reset>{cc}<i>#<b>{n}<reset><white><i><b> {n2} <gray>● <#d2ee45><i><b>{a} <reset>"
//                        .replace("{cc}", colorCode)
//                        .replace("{n}", idx.toString())
//                        .replace("{n2}", entry.getKey().getName())
//                        .replace("{a}", numberWithComma(entry.getValue().intValue()))
//                );
//                idx += 1;
//            }
//
//
//
//
//            q.add("");
//            Hologram holo = FancyHologramsPlugin.get().getHologramManager().getHologram("tja1").get();
//            HologramData holoData = holo.getData();
//            holoData.setText(q);
//            holo.refreshHologram(Bukkit.getOnlinePlayers());
//
//
//        }, 0, 20 * 180);


        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(PICOSERVER.getInstance(), () -> {
            ArrayList<Map<String, Object>> dbres1 = null;
            try {
                dbres1 = Database.getInstance().execute("SELECT * FROM playTime", null);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            HashMap<OfflinePlayer, Integer> hash = new HashMap<>();

            for (var i : dbres1) {
                try {
                    OfflinePlayer p = Bukkit.getOfflinePlayer(UUID.fromString((String) i.get("uuid")));
                    if (p.isOp()) {
                        continue;
                    }
                    hash.put(p, Money.get(p));
                } catch (Exception ea) {}
            }
//            Bukkit.getLogger().info(String.valueOf(hash));


            HashMap<OfflinePlayer, Integer> sortedhash = new HashMap<>();

            List<Map.Entry<OfflinePlayer, Integer>> list = new ArrayList<>(hash.entrySet());
            list.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

            ArrayList<String> q = new ArrayList<>();
            q.add("");
            q.add("");
            q.add("");
            Integer idx = 1;
            for (Map.Entry<OfflinePlayer, Integer> entry : list) {
                if (idx == 11) {
//                    return;
                    break;
                }
                String colorCode = "<#9ADC6B>";
                if (idx == 1) {
                    colorCode = "<#DC3223>";
                }
                if (idx == 2) {
                    colorCode = "<#FF940A>";
                }
                if (idx == 3) {
                    colorCode = "<#DCC221>";
                }

                q.add("<reset>{cc}<i>#<b>{n}<reset><white><i><b> {n2} <gray>● <#F2C744><i><b>{a} <reset>"
                        .replace("{cc}", colorCode)
                        .replace("{n}", idx.toString())
                        .replace("{n2}", entry.getKey().getName())
                        .replace("{a}", numberWithComma(entry.getValue().intValue()))
                );
                idx += 1;
            }

            q.add("");
            Hologram holo = FancyHologramsPlugin.get().getHologramManager().getHologram("test").get();
            HologramData holoData = holo.getData();
            holoData.setText(q);
            holo.refreshHologram(Bukkit.getOnlinePlayers());
        }, 0, 20 * 60 * 10);
    }

    public String getHelpWithBalance(int i, boolean isOp) {

        ArrayList<String> res = new ArrayList<>();
        res.add("&f");
        res.add("&f \uE143 &f보유중인 골드: &x&F&A&E&D&C&B[balance] &f\uE142".replace("[balance]", numberWithComma(i)));
        res.add("&f");
//        res.add(" ▸ /돈 확인 [닉네임]");
        res.add("&f \uE143 &x&F&A&E&D&C&B/돈 보내기 (닉네임) (금액)");
//        res.add(" ▸ /돈 순위");
        res.add("&f");
        if (isOp) {
            res.add("&f \uE143 &f/돈 [지급/차감/확인] (닉네임) (금액)");
        }

        return ChatColor.translateAlternateColorCodes('&', String.join("\n", res));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        //if (!(sender instanceof Player)) {
        //    sender.sendMessage("&f \uE143 &f플레이어가 아닌 사람은 명령어 실행이 불가능합니다");
        //    return false;
        //}

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("확인")) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                if (!offlinePlayer.hasPlayedBefore() && !offlinePlayer.isOnline()) {
                    sender.sendMessage("&f \uE143 &f존재하지 않는 플레이어 닉네임이 입력 되었습니다");
                    return false;
                }

                ArrayList<String> res = new ArrayList<>();
                res.add("&f");
                res.add("&f \uE143 &f[nick] 님의 골드 잔액: &x&F&A&E&D&C&B[balance]&f \uE142"
                        .replace("[balance]", numberWithComma(Money.get(Bukkit.getOfflinePlayer(args[1]))))
                        .replace("[nick]", args[1]));
                res.add("&f");

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.join("\n", res)));

                return false;
            }
            if (args[0].equalsIgnoreCase("보내기")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f \uE143 &f명령어를 다시 한번 확인해주세요"));
            }
        }
        if (args.length == 3) {
            if (parseInt(args[2]) < 1) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f \uE143 &f명령어를 다시 한번 확인해주세요"));
                return false;
            }
            if (args[0].equalsIgnoreCase("지급") && sender.isOp()) {
                OfflinePlayer reciver = Bukkit.getOfflinePlayer(args[1]);
                ArrayList<Object> q3 = new ArrayList<>();

                Money.add((reciver), parseInt(args[2]));

                if (reciver.isOnline()) {
                    (reciver.getPlayer()).sendMessage(ChatColor.translateAlternateColorCodes('&', "&f \uE143 &f골드 충전이 완료 되었습니다 &x&F&A&E&D&C&B+[amount] &f\uE142").replace("[nickname]", reciver.getName()).replace("[amount]", numberWithComma(parseInt(args[2]))));
                }
                sender.sendMessage("done");
                return false;
            }
            if (args[0].equalsIgnoreCase("차감") && sender.isOp()) {
                OfflinePlayer receiver = Bukkit.getOfflinePlayer(args[1]);
                ArrayList<Object> q3 = new ArrayList<>();
                q3.add(parseInt(args[2]));
                q3.add(receiver.getUniqueId().toString());

                Money.sub((receiver), parseInt(args[2]));

                if (receiver.isOnline()) {
                    (receiver.getPlayer()).sendMessage(ChatColor.translateAlternateColorCodes('&', "&f \uE143 &f관리자에 의해 골드가 회수 되었습니다 &x&E&8&B&3&B&3-[amount] &f\uE142").replace("[nickname]", receiver.getName()).replace("[amount]", numberWithComma(parseInt(args[2]))));
                }
                sender.sendMessage("done");
                return false;
            }
            if (args[0].equalsIgnoreCase("보내기")) {
                Player receiver = Bukkit.getPlayer(args[1]);
                if (!(sender instanceof Player)) {
                    sender.sendMessage("&f \uE143 &f플레이어가 아닌 사람은 명령어 실행이 불가능합니다");
                    return true;
                }
                if (receiver == null) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f \uE143 &f해당 플레이어는 접속 중이지 않습니다"));
                    return true;
                }
                try {
                    parseInt(args[2]);
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f \uE143 &f보내실 금액이 정확하지 않습니다"));
                    return false;
                }

                if (parseInt(args[2]) > Money.get((Player) sender)) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f \uE143 &f돈이 부족해서 해당 금액을 보낼 수 없습니다"));
                    return false;
                }

                Money.sub((Player) sender, parseInt(args[2]));


                Money.add(receiver, parseInt(args[2]));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f \uE143 플레이어 &x&F&A&E&D&C&B[nickname] &f님에게 &x&F&A&E&D&C&B[amount] &f\uE142 을 보냈습니다").replace("[nickname]", receiver.getName()).replace("[amount]", numberWithComma(parseInt(args[2]))));
                receiver.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f \uE143 &f플레이어 &x&F&A&E&D&C&B[nickname] &f님에게 &x&F&A&E&D&C&B[amount] &f\uE142 을 받았습니다").replace("[nickname]", sender.getName()).replace("[amount]", numberWithComma(parseInt(args[2]))));
                return false;
            }
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage("&f \uE143 &f/돈 [지급/차감/확인] (닉네임) (금액)");
        } else {
            sender.sendMessage(getHelpWithBalance(Money.get((Player) sender), sender.isOp()));
        }
        return true;
    }
}
