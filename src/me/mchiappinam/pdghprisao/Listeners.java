package me.mchiappinam.pdghprisao;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class Listeners implements Listener {

	private Main plugin;
	public Listeners(Main main) {
		plugin=main;
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onCraft(CraftItemEvent e) {
		Player p = (Player) e.getWhoClicked();
		if(plugin.cache.containsKey(e.getWhoClicked().getName().toLowerCase().trim())) {
			e.setCancelled(true);
			p.closeInventory();
			p.sendMessage("§7[Pris§o]§4 Voc§ est§ preso!");
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		if(plugin.cache.containsKey(e.getPlayer().getName().toLowerCase().trim())) {
			e.setCancelled(true);
			e.getPlayer().sendMessage("§7[Pris§o]§4 Voc§ est§ preso!");
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent e) {
		if(plugin.cache.containsKey(e.getPlayer().getName().toLowerCase().trim())) {
			plugin.tpToPrisao(e.getPlayer());
		}else{
			plugin.join(e.getPlayer());
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		if(plugin.cache.containsKey(e.getPlayer().getName().toLowerCase().trim())) {
	        if(plugin.getConfig().contains("locais")) {
	        	plugin.getServer().getConsoleSender().sendMessage("§3[PDGHPrisao] §2carregando locais...");
				List<String> lista=new ArrayList<String>();
				lista=plugin.getConfig().getStringList("locais");
				plugin.getServer().getConsoleSender().sendMessage("§3[PDGHPrisao] §2locais carregados com sucesso!");
		    	Random r = new Random();
		    	int selecionado = r.nextInt(lista.size());
		    	//String vencedor = (String)lista.get(selecionado);
		    	int etapa=0;
		    	Location spawn;
		    	String zero=null;
		    	String um=null;
		    	String dois=null;
		    	String tres=null;
				for(String lo : lista.get(selecionado).split(";")) {
					if(etapa==0)
						zero=lo;
					else if(etapa==1)
						um=lo;
					else if(etapa==2)
						dois=lo;
					else if(etapa==3)
						tres=lo;
					etapa++;
				}
				spawn = new Location(plugin.getServer().getWorld(zero),Double.parseDouble(um),Double.parseDouble(dois)+1,Double.parseDouble(tres));
		        e.setRespawnLocation(spawn);
	        }
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent e) {
		if(plugin.cache.containsKey(e.getPlayer().getName().toLowerCase().trim())) {
			e.setCancelled(true);
			e.getPlayer().sendMessage("§7[Pris§o]§4 Voc§ est§ preso!");
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onBlockPlace(BlockPlaceEvent e) {
		if(plugin.cache.containsKey(e.getPlayer().getName().toLowerCase().trim())) {
			e.setCancelled(true);
			e.getPlayer().sendMessage("§7[Pris§o]§4 Voc§ est§ preso!");
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	private void onPCmd(PlayerCommandPreprocessEvent e) {
		if(plugin.cache.containsKey(e.getPlayer().getName().toLowerCase().trim())) {
			if(!e.getMessage().toLowerCase().startsWith("/logar")&&!e.getMessage().toLowerCase().startsWith("/login")&&!e.getMessage().toLowerCase().startsWith("/prisao")) {
				e.getPlayer().sendMessage("§7[Pris§o]§4 Voc§ est§ preso!");
				e.getPlayer().sendMessage("§7[Pris§o]§c Os §nicos comandos liberados s§o: §b§l/login§c, §b§l/logar§c e §b§l/prisao§c");
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	private void onDamage(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player)
			if(e.getDamager() instanceof Player||e.getDamager() instanceof Projectile) {
				Player ent = (Player)e.getEntity();
				Player dam = null;
				if(e.getDamager() instanceof Player)
					dam=(Player)e.getDamager();
				else {
					Projectile a = (Projectile) e.getDamager();
					if(a.getShooter() instanceof Player)
						dam=(Player)a.getShooter();
				}
				if(plugin.cache.containsKey(ent.getName().toLowerCase().trim())) {
					e.setCancelled(true);
					if(dam!=null)
						dam.sendMessage("§7[Pris§o]§4 "+ent.getName()+" est§ preso!");
					return;
				}
				if(plugin.cache.containsKey(dam.getName().toLowerCase().trim())) {
					e.setCancelled(true);
					dam.sendMessage("§7[Pris§o]§4 Voc§ est§ preso!");
					return;
				}
			}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	private void onDamageP(PotionSplashEvent e) {
		for(Entity ent2 : e.getAffectedEntities())
			if(ent2 instanceof Player) {
				Player ent = (Player)ent2;
				Player dam = null;
				if(e.getPotion().getShooter() instanceof Player)
					dam=(Player)e.getEntity().getShooter();
				if(plugin.cache.containsKey(ent.getName().toLowerCase().trim())) {
					e.setCancelled(true);
					if(dam!=null)
						dam.sendMessage("§7[Pris§o]§4 "+ent.getName()+" est§ preso!");
					return;
				}
				if(dam!=null)
					if(plugin.cache.containsKey(dam.getName().toLowerCase().trim())) {
						e.setCancelled(true);
						dam.sendMessage("§7[Pris§o]§4 Voc§ est§ preso!");
						return;
					}
			}
	}
}
