package kr.rth.picoserver.menu;

import com.google.gson.Gson;
import kr.rth.picoserver.Database;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.sql.SQLException;
import java.util.*;

import static java.lang.Integer.parseInt;
import static kr.rth.picoserver.util.itemStackSerializer.itemStackDeSerializer;

public class commandListener implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if( args.length == 1 ) {
            ArrayList<Object> q = new ArrayList<>();
            q.add(args[0].trim());
            try {
                if( (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS ( SELECT * FROM menu WHERE id = ? )", q).get(0).values()).get(0) < 1){
                    sender.sendMessage("에러입니다. 관리자에게 문의하여주세요.");
                    return false;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            Map<String, Object> dbRes2;
            try {
                dbRes2 = Database.getInstance().execute("SELECT * FROM menu WHERE id = ?", q).get(0);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            Inventory openingInv= new menuInv(args[0].trim(), (String)dbRes2.get("title"), (Integer) dbRes2.get("size"),false).getInventory();

            Gson gson = new Gson();
            HashMap<String, String> a = new HashMap<>();
            a = gson.fromJson((String) dbRes2.get("invContent"), a.getClass());
            for( var i : a.keySet()) {
                openingInv.setItem(parseInt(i), itemStackDeSerializer(a.get(i)));
            }
            Player p = (Player) sender;
            p.openInventory(openingInv);
            if ((int) dbRes2.get("playSound") == 1) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("PICOSERVER"), new Runnable() {
                    @Override
                    public void run() {
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 5f, 0.79f);
                    }
                }, 0L); //20 Tick (1 Second) delay before run() is called
                Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("PICOSERVER"), new Runnable() {
                    @Override
                    public void run() {
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 5f, 0.89f);
                    }
                }, 2L); //20 Tick (1 Second) delay before run() is called
                Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("PICOSERVER"), new Runnable() {
                    @Override
                    public void run() {
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 5f, 1f);
                    }
                }, 4L); //20 Tick (1 Second) delay before run() is called
                Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("PICOSERVER"), new Runnable() {
                    @Override
                    public void run() {
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 5f, 1.06f);
                    }
                }, 6L); //20 Tick (1 Second) delay before run() is called
                Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("PICOSERVER"), new Runnable() {
                    @Override
                    public void run() {
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 5f, 1.19f);
                    }
                }, 8L); //20 Tick (1 Second) delay before run() is called
            }

        }




        if( !sender.isOp() )  return false; //이 아래는 오피 커맨드입.

        if( args.length == 2 && args[0].equalsIgnoreCase("생성") ) {
            ArrayList<Object> q = new ArrayList<>();
            q.add(args[1].trim());
            try {
                if( (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS ( SELECT * FROM menu WHERE id = ? )", q).get(0).values()).get(0) > 0){
                    sender.sendMessage("이미 그 이름을 가진 메뉴가 있습니다.");
                    return false;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            q.clear();
            q.add(args[1].trim());
            try {
                Database.getInstance().execute("INSERT INTO menu VALUES(?, '{}', '{}', 27, '표시명을 설정해주세요', 1)", q);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            sender.sendMessage("Done.");


        }
        if( args.length == 2 && args[0].equalsIgnoreCase("삭제") ) {
            ArrayList<Object> q = new ArrayList<>();
            q.add(args[1].trim());
            try {
                if( (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS ( SELECT * FROM menu WHERE id = ? )", q).get(0).values()).get(0) < 1){
                    sender.sendMessage("그런 메뉴는 없습니다.");
                    return false;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            q.clear();
            q.add(args[1].trim());
            try {
                Database.getInstance().execute("DELETE FROM menu WHERE id = ?", q);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            sender.sendMessage("Done.");


        }
        if( args.length  == 3 && args[0].equalsIgnoreCase("설정") && args[2].equalsIgnoreCase("아이템설정") ) {
            ArrayList<Object> q = new ArrayList<>();
            q.add(args[1].trim());
            try {
                if( (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS ( SELECT * FROM menu WHERE id = ? )", q).get(0).values()).get(0) < 1){
                    sender.sendMessage("그런 메뉴는 없습니다.");
                    return false;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            Map<String, Object> dbRes2 = null;
            try {
                dbRes2 = Database.getInstance().execute("SELECT * FROM menu WHERE id = ?", q).get(0);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            Inventory openingInv = new menuInv(args[1].trim(), (String)dbRes2.get("title"), (Integer) dbRes2.get("size"), true).getInventory();
            Gson gson = new Gson();
            HashMap<String, String> a = new HashMap<>();
            a = gson.fromJson((String) dbRes2.get("invContent"), a.getClass());
            for( var i : a.keySet()) {
                openingInv.setItem(parseInt(i), itemStackDeSerializer(a.get(i)));
            }
            Player p = (Player) sender;
            p.openInventory(openingInv);


        }
        if( args.length  == 4 && args[0].equalsIgnoreCase("설정") && args[2].equalsIgnoreCase("줄수설정") ) {
            ArrayList<Object> q = new ArrayList<>();

            q.add(args[1].trim());
            try {
                if( (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS ( SELECT * FROM menu WHERE id = ? )", q).get(0).values()).get(0) < 1){
                    sender.sendMessage("그런 메뉴는 없습니다.");
                    return false;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            if(args[1].trim().equals("0")){
                try {
                    Database.getInstance().execute("UPDATE menu SET size = 0 WHERE id = ?", q);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                sender.sendMessage("Done. ");

            }
            q.add(0, parseInt(args[3]) * 9);
            try {
                Database.getInstance().execute("UPDATE menu SET size = ? WHERE id = ?", q);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            sender.sendMessage("Done. ");




        }
        if( args.length > 4 && args[0].equalsIgnoreCase("설정") && args[2].equalsIgnoreCase("명령어설정") ) {
            ArrayList<Object> q = new ArrayList<>();
            q.add(args[1].trim());
            try {
                if( (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS ( SELECT * FROM menu WHERE id = ? )", q).get(0).values()).get(0) < 1){
                    sender.sendMessage("그런 메뉴는 없습니다.");
                    return false;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            Map<String, Object> dbRes2 = null;
            try {
                dbRes2 = Database.getInstance().execute("SELECT * FROM menu WHERE id = ?", q).get(0);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            Map<String, String > a = new HashMap<>();
            Gson gson = new Gson();
            a = gson.fromJson((String) dbRes2.get("commandList"), a.getClass() );
            ArrayList<String> args2 = new ArrayList<>();
            args2.addAll(List.of(args));
            args2.remove(0);
            args2.remove(0);
            args2.remove(0);
            args2.remove(0);

            a.put(Integer.toString(parseInt(args[3]) - 1), String.join(" ", args2));
            q.add(0, gson.toJson(a));
            try {
                Database.getInstance().execute("UPDATE menu SET commandList = ? WHERE id = ?", q);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            sender.sendMessage("done.");
        }
        if( args.length == 4 && args[0].equalsIgnoreCase("설정") && args[2].equalsIgnoreCase("명령어삭제") ) {
            ArrayList<Object> q = new ArrayList<>();
            q.add(args[1].trim());
            try {
                if( (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS ( SELECT * FROM menu WHERE id = ? )", q).get(0).values()).get(0) < 1){
                    sender.sendMessage("그런 메뉴는 없습니다.");
                    return false;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            Map<String, Object> dbRes2 = null;
            try {
                dbRes2 = Database.getInstance().execute("SELECT * FROM menu WHERE id = ?", q).get(0);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            Map<String, String > a = new HashMap<>();
            Gson gson = new Gson();
            a = gson.fromJson((String) dbRes2.get("commandList"), a.getClass() );
            a.remove(Integer.toString(parseInt(args[3]) -1));
            q.add(0, gson.toJson(a));
            try {
                Database.getInstance().execute("UPDATE menu SET commandList = ? WHERE id = ?", q);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            sender.sendMessage("done.");
        }



        if( args.length > 3 && args[0].equalsIgnoreCase("설정") && args[2].equalsIgnoreCase("표시명설정") ) {
            ArrayList<Object> q = new ArrayList<>();
            q.add(args[1].trim());
            try {
                if( (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS ( SELECT * FROM menu WHERE id = ? )", q).get(0).values()).get(0) < 1){
                    sender.sendMessage("그런 메뉴는 없습니다.");
                    return false;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            ArrayList<String> args2 = new ArrayList<>();
            args2.addAll(List.of(args));
            args2.remove(0);
            args2.remove(0);
            args2.remove(0);

            q.add(0, String.join(" ", args2).trim());


            try {
                Database.getInstance().execute("UPDATE menu SET title = ? WHERE id = ?", q);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            sender.sendMessage("Done.");
        }

        if( args.length  == 4 && args[0].equalsIgnoreCase("설정") && args[2].equalsIgnoreCase("소리설정") ) {
            ArrayList<Object> q = new ArrayList<>();

            q.add(args[1].trim());
            try {
                if( (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS ( SELECT * FROM menu WHERE id = ? )", q).get(0).values()).get(0) < 1){
                    sender.sendMessage("그런 메뉴는 없습니다.");
                    return false;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            if (args[3].equalsIgnoreCase("true")) {
                q.add(0, 1);
            } else {
                q.add(0, 0);
            }

            try {
                Database.getInstance().execute("UPDATE menu SET playSound = ? WHERE id = ?", q);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            sender.sendMessage("Done. ");




        }

        return false;
    }
}
