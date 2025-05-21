package kr.rth.picoserver.Display;

//import ch.njol.skript.variables.Variables;
import com.google.gson.Gson;
import kr.rth.picoserver.Database;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;

import java.sql.SQLException;
import java.util.*;

import static kr.rth.picoserver.util.itemStackSerializer.itemStackDeSerializer;

public class commandListener implements CommandExecutor {
    public int i = 0;
    public int i2  = 0;
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(!commandSender.isOp()) {
            return false;
        }
        Player p = (Player) commandSender;
        if( args.length == 4 ) {
            if(args[2].equalsIgnoreCase("줄수설정") ) {
                if(  Integer.parseInt(args[3]) < 1 || Integer.parseInt(args[3]) > 6 ) {
                    p.sendMessage("줄 수 범위는 0초과 6이하 여야합니다.");

                    return false;
                }
                ArrayList<Object> q = new ArrayList<>();
                q.add( Integer.parseInt(args[3])  *  9);
                q.add( args[1]);
                try {
                    Database.getInstance().execute("UPDATE displayList set invSize=? WHERE id=?", q);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if( args.length == 4  && args[0].equalsIgnoreCase("설정") && args[2].equalsIgnoreCase("파티클설정") &&p.isOp() ) {
            Particle particle;
            try{
                particle = Particle.valueOf(args[3]);
            }catch (Exception eaaa) {
                if(!args[3].trim().equalsIgnoreCase("none")) {
                    p.sendMessage("실패");
                    return false;
                }
                particle = null;
            }


            ArrayList<Object >q = new ArrayList<>();
            if(particle == null ){
                q.add( null );
            }else {
                q.add( particle.toString() );
            }
            q.add(args[1]);
            try {
                Database.getInstance().execute("UPDATE displayList SET particle = ? WHERE id = ?", q);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            p.sendMessage("Done.");
            looper.getInstance().reload();


        }
        if( args.length >= 3 ) {
            if(args[0].equalsIgnoreCase("설정") ) {

                if(args[2].equalsIgnoreCase("장비설정") ) {
                    ArrayList<Object> q2 = new ArrayList<>();
                    q2.add(args[1]);

                    try {
                        if( (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS(SELECT * from displayList WHERE id=?)", q2).get(0).values()).get(0) < 1) {
                            p.sendMessage("그 이름을 가진 전시가 없습니다.");
                            return false;
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    ArrayList<Map<String, Object>> dbRes2 = null;
                    try {
                        dbRes2 = Database.getInstance().execute("SELECT * from displayList WHERE id=?", q2);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }



                    if( args[3].equalsIgnoreCase("머리") ) {
                        ((ArmorStand) Bukkit.getEntity( UUID.fromString((String) dbRes2.get(0).get("data")))).setHelmet(p.getInventory().getItemInMainHand());
                        return false;
                    }
                    if( args[3].equalsIgnoreCase("몸통") ) {
                        ((ArmorStand) Bukkit.getEntity( UUID.fromString((String) dbRes2.get(0).get("data")))).setChestplate(p.getInventory().getItemInMainHand());
                        return false;
                    }
                    if( args[3].equalsIgnoreCase("바지") ) {
                        ((ArmorStand) Bukkit.getEntity( UUID.fromString((String) dbRes2.get(0).get("data")))).setLeggings(p.getInventory().getItemInMainHand());
                        return false;
                    }
                    if( args[3].equalsIgnoreCase("바지") ) {
                        ((ArmorStand) Bukkit.getEntity( UUID.fromString((String) dbRes2.get(0).get("data")))).setBoots(p.getInventory().getItemInMainHand());
                        return false;
                    }


                }
                if(args[2].equalsIgnoreCase("아이템설정") ) {
                    ArrayList<Object> q2 = new ArrayList<>();
                    q2.add(args[1]);

                    try {
                        if( (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS(SELECT * from displayList WHERE id=?)", q2).get(0).values()).get(0)< 1) {
                            p.sendMessage("그 이름을 가진 전시가 없습니다.");
                            return false;

                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    ArrayList<Map<String, Object>> dbRes2 = null;
                    try {
                        dbRes2 = Database.getInstance().execute("SELECT * from displayList WHERE id=? ", q2);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }


                    Gson gson = new Gson();
                    HashMap<String, String> a = new HashMap<>();
                    a = gson.fromJson((String) dbRes2.get(0).get("invContent"), a.getClass() );
                    Inventory inv = Bukkit.createInventory(null, (int) dbRes2.get(0).get("invSize"), "수정모드 | " + dbRes2.get(0).get("id"));

                    for( String i : a.keySet() ) {
                        inv.setItem(Integer.parseInt(i), itemStackDeSerializer(a.get(i) ));
                    }

                    p.openInventory(inv);




                }
                if(args[2].equalsIgnoreCase("이름설정") ) {
                    ArrayList<Object> q2 = new ArrayList<>();
                    q2.add(args[1]);

                    try {
                        if( (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS(SELECT * from displayList WHERE id=?)", q2).get(0).values()).get(0) < 1) {
                            p.sendMessage("그 이름을 가진 전시가 없습니다.");
                            return false;

                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    ArrayList<Object> b = new ArrayList<>();

                    ArrayList<String> args2 =  new ArrayList<>();
                    args2.addAll( Arrays.asList(args) );
                    args2.remove(0);
                    args2.remove(0);
                    args2.remove(0);
//                    ;
                    b.add( String.join(" ", args2));
                    b.add(args[1]);

                    try {
                        Database.getInstance().execute("UPDATE displayList SET title=? WHERE id=? ", b)
                        ;
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                }
            }

        }
        if( args.length== 2 ) {
            if( args[0].equalsIgnoreCase("삭제") ) {
                ArrayList<Object> q2 = new ArrayList<>();
                q2.add(args[1]);
                ArrayList<Map<String, Object>> dbRes1 = null;

                try {
                    if( (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS(SELECT * from displayList WHERE id=?)", q2).get(0).values()).get(0) < 1) {
                        p.sendMessage("그 이름을 가진 전시가 없습니다.");
                        return false;

                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                ArrayList<Map<String, Object>> dbRes2 = null;
                try {
                    dbRes2 = Database.getInstance().execute("SELECT * from displayList WHERE id=?", q2);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                Bukkit.getEntity(UUID.fromString((String) dbRes2.get(0).get("data"))).remove();
                try {
                    Database.getInstance().execute("DELETE FROM displayList WHERE id=?", q2)
                    ;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                looper.getInstance().reload();
            }
            if( args[0].equalsIgnoreCase("생성") ) {
                ArrayList<Object> q2 = new ArrayList<>();
                q2.add(args[1]);


                try {
                    if( (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS(SELECT * from displayList WHERE id=?)", q2).get(0).values()).get(0) > 0) {
                        p.sendMessage("이미 그 이름을 가진 전시가 있습니다.");
                        return false;

                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }


                ArmorStand as = (ArmorStand) p.getWorld().spawnEntity(p.getLocation(), EntityType.ARMOR_STAND);

                as.setHelmet(p.getInventory().getItemInMainHand());
                as.setVisible(false);
                as.setCollidable(false);
                as.setGravity(false);
                as.setCustomName("전시품|" + args[1]);
                as.setCustomNameVisible(false);
                as.setInvulnerable(true);
                Gson gson = new Gson();
                ArrayList<Object> q = new ArrayList<>();
                q.add(args[1]);
                q.add(as.getUniqueId().toString());
                q.add( 27 );
                q.add( "{}");
                q.add("타이틀을 설정 해 주세요." );


                try {
                    Database.getInstance().execute("INSERT INTO displayList VALUES(?,?,?,?,?, null, 1.25)", q);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                looper.getInstance().reload();


                return false;
            }
            if( args[0].equalsIgnoreCase("이동") ) {
                ArrayList<Object> q2 = new ArrayList<>();
                q2.add(args[1]);
                ArrayList<Map<String, Object>> dbRes1 = null;

                try {
                    if( (Integer) new ArrayList<>(Database.getInstance().execute("SELECT EXISTS(SELECT * from displayList WHERE id=?)", q2).get(0).values()).get(0) < 1) {
                        p.sendMessage("그 이름을 가진 전시가 없습니다.");
                        return false;

                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                ArrayList<Map<String, Object>> dbRes2 = null;
                try {
                    dbRes2 = Database.getInstance().execute("SELECT * from displayList WHERE id=?", q2);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                Bukkit.getEntity(UUID.fromString( (String) dbRes2.get(0).get("data"))).teleport(p.getLocation());




                return false;
            }




        }
        if( args.length == 1 ) {
            if(args[0].equalsIgnoreCase("리로드")) {
                looper.getInstance().reload();

                return false;

            }
            if(args[0].equalsIgnoreCase("전체삭제")) {


                return false;

            }

        }
        return false;
    }
}
