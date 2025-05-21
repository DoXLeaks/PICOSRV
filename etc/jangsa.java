package kr.rth.picoserver.etc;

import kr.rth.picoserver.Money.Money;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

import static kr.rth.picoserver.util.transText.transText;

public class jangsa implements CommandExecutor {
    HashMap<Player, Long> cooldown = new HashMap<>();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Player p = (Player) sender;
        if( cooldown.get( p ) == null ) {
            cooldown.put(p, 0L);
        }
        if(cooldown.get( p ) > System.currentTimeMillis() ) {
            p.sendMessage(transText("&f  &f장사글 쿨타임이 아직 남았습니다 §x§F§C§D§0§5§C([n]분)".replace("[n]",
                    Integer.toString((Long.valueOf((cooldown.get( p ) -  System.currentTimeMillis()) / 1000 / 60).intValue()))
            )));
            return false;
        }
        if(args.length < 1) {
            sender.sendMessage(transText("&f  &f장사글 매시지를 입력해주세요"));
            return false;
        }
        if( String.join(" ", args).length() > 30  ) {
            sender.sendMessage(transText("&f  &f장사글은 §x§F§C§D§0§5§C최대 30자 &f내로 입력 가능합니다"));
            return false;
        }
        Integer price = 20_000;

        if( Money.get(p) < price ) {
            sender.sendMessage(transText("&f  &f장사글에 필요한 §x§F§C§D§0§5§C골드&f가 부족합니다"));
            return false;
        }
//            Database.getInstance().execute("UPDATE money SET balance = balance - ? WHERE uuid = ?",q );
        Money.sub(p, price);
        cooldown.put(p, System.currentTimeMillis() + 1000 * 300 );

        TextComponent compo = new TextComponent(transText(" §2[거래신청]\n&f"));
        compo.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/trade [p]".replace("[p]", p.getName())));
        compo.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("\n§f  \uE11B §f클릭시 거래를 신청 합니다\n§f")));
        String res = transText("\n&f  &f/장사글 §x§F§C§D§0§5§C[p]§x§F§A§E§D§C§B ".replace("[p]", p.getName()) + String.join("§x§F§A§E§D§C§B ", args));
        Bukkit.spigot().broadcast( new TextComponent(new TextComponent(""), new TextComponent(TextComponent.fromLegacyText(res)), compo),new TextComponent(transText("")));

        return false;
    }
}
