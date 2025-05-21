package kr.rth.picoserver.Money;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

import static kr.rth.picoserver.PICOSERVER.getEcon;

public class Money {
    public static int get(OfflinePlayer p ) {
        Economy econ = getEcon();

        return Double.valueOf(econ.getBalance(p)).intValue();
    }


    public static void set(OfflinePlayer p, Integer value ) {
        Economy econ = getEcon();

        double currentBalance = econ.getBalance(p);
        double difference = value - currentBalance;

        if (difference > 0) {
            // 추가
            econ.depositPlayer(p, difference);
        } else if (difference < 0) {
            // 차감
            EconomyResponse response = econ.withdrawPlayer(p, -difference);

        }
    }

    public static void add(OfflinePlayer p, Integer value ) {
        Economy econ = getEcon();
//        Essentials essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");

        econ.depositPlayer(p, value);
//        essentials.getUser(p).giveMoney(BigDecimal.valueOf(value), new CommandSource(Bukkit.getConsoleSender()));

    }
    public static void sub(OfflinePlayer p, Integer value ) {
        Economy econ = getEcon();
        econ.withdrawPlayer(p, value);

    }



}
