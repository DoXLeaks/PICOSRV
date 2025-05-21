package kr.rth.picoserver.LoreSystem;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import kr.rth.picoserver.PICOSERVER;
import kr.rth.picoserver.Stat.PlayerStat;
import kr.rth.picoserver.Stat.Stat;
import kr.rth.picoserver.Team.teams;
import kr.rth.picoserver.util.ParseLore;
import kr.rth.picoserver.util.RegionChecker;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashMap;

import static kr.rth.picoserver.util.ParseLore.parseLore;
import static kr.rth.picoserver.util.random.generateRandomNumber;

public class EventListener implements Listener {

    public HashMap<LivingEntity, Long> vampireCooldown = new HashMap<>();
    public HashMap<LivingEntity, Long> regenerationCooldown = new HashMap<>();
    public HashMap<LivingEntity, Long> potionCooldown = new HashMap<>();
    public HashMap<Player, Long> lighingCool = new HashMap<>();

    @EventHandler
    public void onWear(PlayerArmorChangeEvent e) {
        Player p = e.getPlayer();
        p.setMaxHealth(20 + (parseLore(Stat.HEALTH.getLabel(), p, false)));
        Bukkit.getScheduler().runTaskAsynchronously(PICOSERVER.getInstance(), () -> ParseLore.initCache(e.getPlayer()));
    }

    public void ApplyUserEffect(LivingEntity attacker, LivingEntity victim, ItemStack item) {
        if (potionCooldown.get(attacker) == null) {
            potionCooldown.put(attacker, (long) 0.0);
        }

        if (potionCooldown.get(attacker) > System.currentTimeMillis()) {
            return;
        }

        for (String i : item.getItemMeta().getLore()) {
            i = ChatColor.stripColor(i);

            if (i.contains("실명1")) {
                if (!(generateRandomNumber(0, 100) < Double.valueOf(Arrays.asList(i.split("/")).get(1).replace("%", "").trim()))) {
                    continue;
                }

                victim.removePotionEffect(PotionEffectType.BLINDNESS);
                victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, 0));
                potionCooldown.put(attacker, System.currentTimeMillis() + (3 * 1000));
            }
            if (i.contains("신속1")) {
                if (!(generateRandomNumber(0, 100) < Double.valueOf(Arrays.asList(i.split("/")).get(1).replace("%", "").trim()))) {
                    continue;
                }
                attacker.removePotionEffect(PotionEffectType.SPEED);
                attacker.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5 * 20, 0));
                potionCooldown.put(attacker, System.currentTimeMillis() + (5 * 1000));
//                victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_VEX_AMBIENT, 1f, 2f);
            }
            if (i.contains("힘1")) {
                if (!(generateRandomNumber(0, 100) < Double.valueOf(Arrays.asList(i.split("/")).get(1).replace("%", "").trim()))) {
                    continue;
                }
                attacker.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                attacker.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 5 * 20, 0));
                potionCooldown.put(attacker, System.currentTimeMillis() + (5 * 1000));
            }
            if (i.contains("광란1")) {
                if (!(generateRandomNumber(0, 100) < Double.valueOf(Arrays.asList(i.split("/")).get(1).replace("%", "").trim()))) {
                    continue;
                }
                attacker.removePotionEffect(PotionEffectType.REGENERATION);
                attacker.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5 * 20, 0));

                attacker.removePotionEffect(PotionEffectType.SPEED);
                attacker.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5 * 20, 1));
                potionCooldown.put(attacker, System.currentTimeMillis() + (5 * 1000));
//                attacker.getWorld().playSound(attacker.getLocation(), Sound.ENTITY_VEX_DEATH, 1f, 1f);1
            }
            if (i.contains("감속1")) {
                if (!(generateRandomNumber(0, 100) < Double.valueOf(Arrays.asList(i.split("/")).get(1).replace("%", "").trim()))) {
                    continue;
                }
                victim.removePotionEffect(PotionEffectType.SLOW);
                victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5 * 20, 0));
                potionCooldown.put(attacker, System.currentTimeMillis() + (5 * 1000));
//                victim.getWorld().playSound(victim.getLocation(), Sound.BLOCK_CHAIN_PLACE, 1f, 0.5f);
            }
            if (i.contains("흡수1")) {
                if (!(generateRandomNumber(0, 100) < Double.valueOf(Arrays.asList(i.split("/")).get(1).replace("%", "").trim()))) {
                    continue;
                }
                attacker.removePotionEffect(PotionEffectType.ABSORPTION);
                attacker.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 5 * 20, 0));
                potionCooldown.put(attacker, System.currentTimeMillis() + (5 * 1000));

//                attacker.getWorld().playSound(attacker.getLocation(), Sound.ENTITY_BLAZE_HURT, 1f, 1f);
            }
            if (i.contains("저항1")) {
                if (!(generateRandomNumber(0, 100) < Double.valueOf(Arrays.asList(i.split("/")).get(1).replace("%", "").trim()))) {
                    continue;
                }
                attacker.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                attacker.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5 * 20, 0));
                potionCooldown.put(attacker, System.currentTimeMillis() + (5 * 1000));
//                attacker.getWorld().playSound(attacker.getLocation(), Sound.ENTITY_TURTLE_EGG_BREAK, 1f, 1f);
            }
            if (i.contains("독1")) {
                if (!(generateRandomNumber(0, 100) < Double.valueOf(Arrays.asList(i.split("/")).get(1).replace("%", "").trim()))) {
                    continue;
                }
                victim.removePotionEffect(PotionEffectType.POISON);
                victim.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 5 * 20, 0));
                potionCooldown.put(attacker, System.currentTimeMillis() + (5 * 1000));
//                attacker.getWorld().playSound(attacker.getLocation(), Sound.ENTITY_PARROT_IMITATE_SPIDER, 1f, 0.5f);
            }
        }
    }

    @EventHandler
    public void onHeldChange(PlayerItemHeldEvent event) {
        Inventory inventory = event.getPlayer().getInventory();
        if (inventory.getItem(event.getPreviousSlot()) == null && inventory.getItem(event.getNewSlot()) == null) return;
        Bukkit.getScheduler().runTaskAsynchronously(PICOSERVER.getInstance(), () -> ParseLore.initCache(event.getPlayer()));
    }

    @EventHandler(ignoreCancelled = true)
    @SuppressWarnings("all")
    public void onDamage(EntityDamageByEntityEvent e) {
//        Bukkit.getLogger().info(String.valueOf(e.getCause()));
//        Bukkit.getLogger().info(String.valueOf( e.getHandlerList() ));
        try {
            LivingEntity attacker = (LivingEntity) e.getDamager();
            LivingEntity victim = (LivingEntity) e.getEntity();
            if (!(victim.getWorld().getName().equals("dungeon") ||victim.getWorld().getName().equals("newlobby") || victim.getWorld().getName().equals("pvp") || victim.getWorld().getName().equals("spawn") || victim.getWorld().getName().equals("parkour"))) {
                return;
            }
            if(( attacker instanceof Player) && (victim instanceof Player) ){
                if (!teams.isAttackable((Player) attacker, (Player) victim)) {
                    boolean isEspecial = false;
                    try {
                        
                        if (attacker.getEquipment().getItemInMainHand() != null) {
                            if (attacker.getEquipment().getItemInMainHand().getType() != null) {
                                if (attacker.getEquipment().getItemInMainHand().getType().equals(Material.BRUSH)) {
                                    isEspecial = true;
                                }
                            }
                        }

                    } catch (Exception ea) {

                    }

                    if (!isEspecial) {
                        e.setCancelled(true);
                        return;
                    }
                }
            }
            if( victim instanceof  Player) {
                try {
                    if (RegionChecker.isPvpDisabledInPlayerRegion((Player) victim)) return;
                } catch (Exception ea) {
                }
            }


            String thoseWhoWillGonnaDecompilethis = "stacktrace를 확인하여 stacktrace 중 com.nisovin.magicspells으로 시작하는 패키지명이 있다면 매펠에서 보낸 데미지로 인식하고 차단하는 방법입니다.";
            String thoseWhoWillGonnaDecompilethis2 = "당연히 이 플러그인 원 제작자가 고안해낸건 아닙니다.";
            //boolean isMagicSpell = Arrays.stream(Thread.currentThread().getStackTrace()).anyMatch(it -> it.getClassName().startsWith("com.nisovin.magicspells"));
            boolean isMagicSpell = false; //위 구문을 활성화한다면 이걸 비활성화하세요

            double defaultDam = e.getDamage();

            PlayerStat attackerStat = ParseLore.parseStatFromLore(attacker, true);
            PlayerStat victimStat = ParseLore.parseStatFromLore(victim, true);

            if (isMagicSpell) {
                defaultDam = Math.floor(defaultDam * 100) / 100;

                if (defaultDam < 0) {
                    defaultDam = 0;
                }
                //if( parseLore("회피력", victim , true) > generateRandomNumber(0,100)) {
                //    attacker.sendTitle("", ChatColor.translateAlternateColorCodes('&',"&7                                          &7&o- &f&o&m[d](+0.0)&c &oMiss".replace("[d]", String.valueOf(defaultDam)) ), 0, 10, 2);
//          //  e.setCancelled(true);
                //    e.setDamage(0);
//          //  attacker.playSound( attacker.getLocation(), Sound.ITEM_SHIELD_BREAK, 5f, 5f );
//          //  victim.playSound( victim.getLocation(), Sound.ITEM_SHIELD_BREAK, 5f, 5f );
                //    return;
                //}
                //attacker.sendTitle("", ChatColor.translateAlternateColorCodes('&',"&7                                          &7&o+ &f&o[d](+0.0) &8&oDamage".replace("[d]", String.valueOf(defaultDam)) ), 0, 10, 2);
            } else {
                double defencdDam = victimStat.data().get(Stat.DEFENCE) * 0.4;
                double PenetrateDam = attackerStat.data().get(Stat.PENETRATE) * 0.4;
                defencdDam -= (defencdDam * PenetrateDam) / 100;

                defaultDam += attackerStat.data().get(Stat.ATTACK);

                defaultDam -= defencdDam;

                double critiDam = 0.0;

                if (attackerStat.data().get(Stat.CRIT_PER) > generateRandomNumber(0, 100)) {
                    critiDam = (defaultDam * generateRandomNumber(10, 80)) / 100;
                }

                defaultDam = Math.floor(defaultDam * 100) / 100;
                critiDam = Math.floor(critiDam * 100) / 100;

                if (defaultDam < 0) {
                    defaultDam = 0;
                }
                if (critiDam < 0.0001) {
                    critiDam = +0;
                }

                if ( victimStat.data().get(Stat.DODGE) > generateRandomNumber(0, 100) ) {
                    if(attacker instanceof Player) {
                        ((Player) attacker).sendTitle("", ChatColor.translateAlternateColorCodes('&', "&7                                              &7&o- &f&m&o[d]&x&F&2&C&6&C&6&o&m(+[d1])&r &7&oCancel".replace("[d]", String.valueOf(defaultDam)).replace("[d1]", String.valueOf(critiDam))), 0, 10, 2);

                    }
//            e.setCancelled(true);
                    e.setDamage(0);
//            attacker.playSound( attacker.getLocation(), Sound.ITEM_SHIELD_BREAK, 5f, 5f );
//            victim.playSound( victim.getLocation(), Sound.ITEM_SHIELD_BREAK, 5f, 5f );
                    return;
                }
                try {
                    String display = ChatColor.stripColor(attacker.getEquipment().getItemInHand().getItemMeta().getDisplayName()).trim();
                    if (display.contains("천둥번개 묠니르")) {
                        if (7 > generateRandomNumber(0, 100)) {
                            if (potionCooldown.get(attacker) == null) {
                                potionCooldown.put(attacker, (long) 0.0);
                            }
                            if (!(potionCooldown.get(attacker) > System.currentTimeMillis())) {
                                potionCooldown.put(attacker, System.currentTimeMillis() + (
                                        3 * 1000
                                ));
                                defaultDam += 1;
                                victim.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 1 * 20, 0));

                                Bukkit.getScheduler().scheduleSyncDelayedTask(PICOSERVER.getInstance(), () -> {
                                    victim.getWorld().playSound(victim.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1f, 1f);
                                    victim.getWorld().strikeLightningEffect(victim.getLocation());
                                    victim.damage(2);
                                }, 20L);
                            }

                        }
                    }
                    if (display.contains("불사의 칼날")) {
                        if (15 > generateRandomNumber(0, 100)) {
                            if (potionCooldown.get(attacker) == null) {
                                potionCooldown.put(attacker, (long) 0.0);
                            }
                            if (!(potionCooldown.get(attacker) > System.currentTimeMillis())) {
                                potionCooldown.put(attacker, System.currentTimeMillis() + (
                                        2 * 1000
                                ));
                                defaultDam += 1;
                                attacker.removePotionEffect(PotionEffectType.ABSORPTION);
                                attacker.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 3 * 20, 0));

                            attacker.getWorld().playSound(attacker.getLocation(), Sound.ENTITY_BLAZE_HURT, 1f, 0.5f);

                            }

                        }
                    }
                    if (display.contains("피의 갈망")) {
                        if (7 > generateRandomNumber(0, 100)) {
                            if (potionCooldown.get(attacker) == null) {
                                potionCooldown.put(attacker, (long) 0.0);
                            }
                            if (!(potionCooldown.get(attacker) > System.currentTimeMillis())) {
                                potionCooldown.put(attacker, System.currentTimeMillis() + (
                                        3 * 1000
                                ));
//                     defaultDam += 1;
                                attacker.removePotionEffect(PotionEffectType.ABSORPTION);
                                attacker.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 8 * 20, 0));
                                attacker.getWorld().playSound(attacker.getLocation(), Sound.ENTITY_BLAZE_HURT, 1f, 0.5f);
                            }

                        }
                    }
                    if (display.contains("바다를 품은 해파리")) {
                        if (13 > generateRandomNumber(0, 100)) {
                            if (potionCooldown.get(attacker) == null) {
                                potionCooldown.put(attacker, 0L);
                            }
                            if (!(potionCooldown.get(attacker) > System.currentTimeMillis())) {
                                potionCooldown.put(attacker, System.currentTimeMillis() + (
                                        9 * 1000
                                ));
                                victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 1f, 1f);
                                victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30, 9));
                            }
                        }
                    }
                    if (display.contains("죽음의 도끼")) {
                        if (5 > generateRandomNumber(0, 100)) {
                            if (potionCooldown.get(attacker) == null) {
                                potionCooldown.put(attacker, 0L);
                            }
                            if (!(potionCooldown.get(attacker) > System.currentTimeMillis())) {
                                potionCooldown.put(attacker, System.currentTimeMillis() + (
                                        4 * 1000
                                ));
                                victim.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 1 * 20, 1));
                                victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_DEATH, 1f, 1f);
                                Bukkit.getScheduler().runTaskLater(PICOSERVER.getInstance(), () -> {
                                    victim.addPotionEffect(new PotionEffect(PotionEffectType.HARM, 10, 0));
                                }, 20L);
                            }

                        }
                    }
                } catch (Exception ee) {
                }
                if(attacker instanceof Player) {
                    ((Player) attacker).sendTitle("", ChatColor.translateAlternateColorCodes('&', "&7                                             &7&o+ &f&o[d]&x&F&2&C&6&C&6&o(+[d1]) &7&oDamage".replace("[d]", String.valueOf(defaultDam)).replace("[d1]", String.valueOf(critiDam))), 0, 10, 2);

                }

                e.setDamage(defaultDam + critiDam);
                if (defaultDam + critiDam < 0.5) {
                    e.setDamage(0.001);
                }
            }

            Location loc = attacker.getLocation();
            loc.setY(loc.getY() + 2.5);
//        attacker.getWorld().spawnParticle(Particle.HEART, loc, 10,1);

            if ((vampireCooldown.get(attacker) == null)) {
                vampireCooldown.put(attacker, (long) 0);
            }
            if (!(vampireCooldown.get(attacker) > System.currentTimeMillis())) {
                if (defaultDam > 0) {
                    if (attackerStat.data().get(Stat.DRAIN_PER) * 0.5 > generateRandomNumber(0, 100)) {
                        //            attacker.playSound(attacker.getLocation(), Sound.ENTITY_GHAST_HURT, 5f, 1f);
                        if (attacker.getHealth() + attackerStat.data().get(Stat.DRAIN) > attacker.getMaxHealth()) {
                            try {
                                attacker.setHealth(attacker.getMaxHealth());
                            } catch (Exception ignored) {};
                        } else {
                            try {
                                attacker.setHealth(attacker.getHealth() + attackerStat.data().get(Stat.DRAIN));
                            } catch (Exception ignored) {};
                        }

                        Location loc2 = attacker.getLocation();
                        loc2.setY(loc2.getY() + 2);
                        attacker.getLocation().getWorld().spawnParticle(Particle.HEART, loc2, 0, 0, 0, 0, 3);
//                    attacker.playSound(attacker.getLocation(), Sound.ENTITY_PHANTOM_HURT, 2, 1);
                        vampireCooldown.put(attacker, System.currentTimeMillis() + (5 * 1000));
                    }
                }
            }

            ApplyUserEffect(attacker, victim, attacker.getEquipment().getItemInHand());

        } catch (Exception a) {
        }
    }
}
