package net.gumpdev.everymobiscreeper;

import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public final class EveryMobIsCreeper extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {

    }

    HashMap<Entity, Boolean> isExploding = new HashMap<>();
    float range = 3.5f;

    @EventHandler
    public void onMobMove(EntityMoveEvent event){
        if(event.getEntity() instanceof ExperienceOrb) return;
        if(event.getEntity() instanceof Item) return;
        if(isExploding.containsKey(event.getEntity()) && isExploding.get((event.getEntity())))
            return;

        for(Player pl : Bukkit.getOnlinePlayers()){
            if(pl == event.getEntity()) continue;;
            if(pl.getWorld() != event.getEntity().getWorld()) continue;
            if(pl.getLocation().distance(event.getEntity().getLocation()) < 5)
                explodeEntity(event.getEntity());
        }
    }

    public void explodeEntity(Entity entity){
        isExploding.put(entity,true);
        for(Player pl : Bukkit.getOnlinePlayers())
            pl.playSound(entity.getLocation(), Sound.ENTITY_TNT_PRIMED,1,1);
        new BukkitRunnable() {
            @Override
            public void run() {
                if(entity.isDead()) return;
                for(Player pl : Bukkit.getOnlinePlayers()){
                    if(pl == entity) continue;
                    if(pl.getWorld() != entity.getWorld()) continue;
                    if(pl.getLocation().distance(entity.getLocation()) < 5) {
                        entity.getLocation().getWorld().createExplosion(entity.getLocation(), 5, false, true, entity);
                        if(entity instanceof Player)
                            ((Player)entity).setHealth(0.0D);
                        else
                            entity.remove();
                        break;
                    }
                }
            }
        }.runTaskLater(this,20 * 2);
        new BukkitRunnable() {
            @Override
            public void run() {
                isExploding.remove(entity,isExploding.get(entity));
            }
        }.runTaskLater(this, 20 * 3);
    }
}
