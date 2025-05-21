package kr.rth.picoserver.Display;

//import ch.njol.skript.variables.Variables;
import kr.rth.picoserver.Database;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class looper {
    static looper instance;
    HashMap<String, UUID> DisplayMaps = new HashMap<>();
    HashMap<String, Double> OffsetMaps = new HashMap<>();
    HashMap<String, Particle> EffectMaps = new HashMap<>();

    //생성자를 priavte로 만들어 접근을 막는다
    public int i = 0;
    public double i2 = 0;
    public ArrayList<Double> relativePath = new ArrayList<>();
    private looper(){
        reload();
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugin("PICOSERVER"), new Runnable(){
            public void run(){
                i += 7;

                for (String a1: DisplayMaps.keySet()) {
//                    if(DisplayMaps.get(a1) == null) continue;
                    ArmorStand armorStand = (ArmorStand)Bukkit.getEntity(DisplayMaps.get(a1));
                    if(armorStand == null) {
                        continue;
                    }
                    (armorStand).setRotation(i, 0);
                }
        }}, 20L, 1L);

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugin("PICOSERVER"), new Runnable(){

            public void run(){
                i2 += 0.25;

                relativePath.clear();
                relativePath.add( Math.sin(i2) * 0.8 );
                relativePath.add(Math.cos(i2) * 0.8  );

                for (String a1: DisplayMaps.keySet()) {
//                    if(DisplayMaps.get(a1) == null) continue;
                    if( !( EffectMaps.containsKey(a1) && OffsetMaps.containsKey(a1) ) ){
                        continue;
                    }
                    ArmorStand armorStand = (ArmorStand)Bukkit.getEntity(DisplayMaps.get(a1));
                    if(armorStand == null) {
                        continue;
                    }
                    Location a = armorStand.getLocation();

                    a.setX(a.getX() +  relativePath.get(0));
                    a.setY(a.getY() + OffsetMaps.get(a1));
                    a.setZ(a.getZ() + relativePath.get(1) );
                    a.getWorld().spawnParticle(EffectMaps.get(a1), a, 0,0,0,0, 3);
                }
            }}, 20L, 1L);


    }
    public void reload() {
        DisplayMaps.clear();
        OffsetMaps.clear();
        EffectMaps.clear();
        try {
            for(Map<String, Object> i : Database.getInstance().execute("SELECT * FROM displayList", null) ) {

                DisplayMaps.put((String) i.get("id"), UUID.fromString((String) i.get("data") ) );

                if(i.get("offset") != null ) {
                    OffsetMaps.put((String) i.get("id"), (Double) i.get("offset"));
                }

                if(i.get("particle") != null ) {
                    EffectMaps.put((String) i.get("id"), Particle.valueOf((String) i.get("particle")));
                }

            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void addElement(String key, ArmorStand value) {
//        DisplayMaps.put(key, value);
    }

    //getInstance 메소드를 통해 한번만 생성된 객체를 가져온다.
    public static looper getInstance(){
        if(instance == null){ //최초 한번만 new 연산자를 통하여 메모리에 할당한다.
            instance = new looper();
        }
        return instance;
    }
}
