package kr.rth.picoserver.Stat;

import kr.rth.picoserver.util.ParseLore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static kr.rth.picoserver.util.transText.transText;

public class commandListener implements CommandExecutor {

    public String booledString(Double i) {
        i = (double) (Double.valueOf(i * 1000).intValue() / 1000);
        if ((i % 1) != 0) {
            return String.valueOf(i);
        } else {
            return String.valueOf(i.intValue());
        }

    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage("플레이어만 사용 가능한 명령어입니다.");
            return true;
        }
        PlayerStat playerStat = ParseLore.parseStatFromLore(player, true);
        commandSender.sendMessage(transText(("\n &f嚿 &f공격력 &x&c&c&c&c&c&c&l&o(+[v])&f &f亸 &f방어력 &x&c&c&c&c&c&c&l&o(+[v])&f &f嚿 &f생명력 &x&c&c&c&c&c&c&l&o([v]/[v])&f" +
                        "\n&d 嚿 &f재생력 &x&c&c&c&c&c&c&l&o(+[v])&f &f熋 &f회피력 &x&c&c&c&c&c&c&l&o(+[v]%)&f &f漡 &f치명타 확률 &x&c&c&c&c&c&c&l&o(+[v]%)&f" +
                        "\n&f 嚽 &f흡혈력 &x&c&c&c&c&c&c&l&o(+[v])&f &f觷 &f흡혈 확률 &x&c&c&c&c&c&c&l&o(+[v]%)&f &f嚱 &f방어구 관통력 &x&c&c&c&c&c&c&l&o(+[v]%)&f" +
                        "\n&f")
                        .replaceFirst("\\[v\\]", booledString(playerStat.data().get(Stat.ATTACK)))
                        .replaceFirst("\\[v\\]", booledString(playerStat.data().get(Stat.DEFENCE)))
//                    .replaceFirst("\\[v\\]",booledString(parseLore("생명력",(Player) commandSender, true)))
                        .replaceFirst("\\[v\\]", booledString(player.getHealth()))
                        .replaceFirst("\\[v\\]", booledString(player.getMaxHealth()))

                        .replaceFirst("\\[v\\]", booledString(playerStat.data().get(Stat.REGEN)))
                        .replaceFirst("\\[v\\]", booledString(playerStat.data().get(Stat.DODGE)))
                        .replaceFirst("\\[v\\]", booledString(playerStat.data().get(Stat.CRIT_PER)))

                        .replaceFirst("\\[v\\]", booledString(playerStat.data().get(Stat.DRAIN)))
                        .replaceFirst("\\[v\\]", booledString(playerStat.data().get(Stat.DRAIN_PER)))
                        .replaceFirst("\\[v\\]", booledString(playerStat.data().get(Stat.PENETRATE)))
        ));
        return false;
    }
}
