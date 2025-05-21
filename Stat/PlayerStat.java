package kr.rth.picoserver.Stat;

import org.bukkit.entity.LivingEntity;

import java.util.HashMap;

public record PlayerStat(LivingEntity player, HashMap<Stat, Double> data) {
}