package me.mchiappinam.pdghprisao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Comando implements CommandExecutor {

	private Main plugin;
	public Comando(Main main) {
		plugin=main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("prender")) {
			if(!sender.hasPermission("pdgh.moderador")) {
				sender.sendMessage("§cSem permiss§es");
				return true;
			}
			if(args.length < 2) {
				sender.sendMessage("§cUse §l/prender <nick> <tempo> [motivo]");
				return true;
			}
			try{
				if (StringUtils.isNumeric(args[1])) {
					int valor=Integer.parseInt(args[1]);
					if((valor>0)&&(valor<=15000)) {
						
						if(args.length==2) {
							plugin.add(sender.getName(), args[0], valor, "Sem informa§§es");
						}else {

							StringBuilder sb = new StringBuilder();
				            sb.append(args[2]);
				            for (int i = 3; i < args.length; i++) {
				              sb.append(" ");
				              sb.append(args[i]);
				            }
							plugin.add(sender.getName(), args[0], valor, ""+sb);
							
						}
						
					}else{
						sender.sendMessage("§7[Prisão]§c Apenas n§meros de 1 § 15000.");
						return true;
					}
				}else{
					sender.sendMessage("§7[Pris§o]§c Apenas n§meros de 1 § 15000.");
					return true;
				}
			}catch (NumberFormatException nfe){
				sender.sendMessage("§7[Pris§o]§c Apenas n§meros de 1 § 15000.");
				return true;
			}
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("soltar")) {
			if(!sender.hasPermission("pdgh.moderador")) {
				sender.sendMessage("§cSem permiss§es");
				return true;
			}
			if(args.length < 1&&args.length>1) {
				sender.sendMessage("§cUse §l/soltar <nick>");
				return true;
			}
			if(plugin.existe(args[0])) {
				plugin.cache.put(args[0].toLowerCase().trim(), 1);
				sender.sendMessage("§7[Pris§o]§a "+args[0]+" §cser§ solto em 1 segundo!");
			}else
				sender.sendMessage("§7[Pris§o]§a "+args[0]+" §cn§o est§ preso!");
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("prisao")) {
			if(!sender.hasPermission("pdgh.op")) {
				if(!plugin.existe(sender.getName().toLowerCase().trim())) {
					sender.sendMessage("§7[Pris§o]§a Voc§ n§o est§ preso =D");
					return true;
				}
				sender.sendMessage("§7[Pris§o]§c Voc§ est§ preso por §a"+plugin.getTempo(sender.getName().toLowerCase().trim())+"§c segundos.");
				return true;
			}

			if(args[0].equalsIgnoreCase("add")) {
				if(args.length<1) {
					sender.sendMessage("§c/prisao add -§4- Adiciona uma sela.");
					return true;
				}
				List<String> lista=new ArrayList<String>();
				lista=plugin.getConfig().getStringList("locais");
				Player p=(Player)sender;
				lista.add(p.getLocation().getWorld().getName()+";"+p.getLocation().getBlockX()+";"+p.getLocation().getBlockY()+";"+p.getLocation().getBlockZ());
				plugin.getConfig().set("locais", lista);
				plugin.saveConfig();
				plugin.reloadConfig();
				lista.clear();
				sender.sendMessage("§7[Pris§o]§c Local adicionado!");
				return true;
			}
			return true;
		}
		return false;
	}
}
