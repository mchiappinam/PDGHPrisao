package me.mchiappinam.pdghprisao;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import br.com.devpaulo.legendchat.api.events.ChatMessageEvent;

public class ListenerLegendchat implements Listener {
	private Main plugin;
	public ListenerLegendchat(Main main) {
		plugin=main;
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	private void onChat(ChatMessageEvent e) {
		if(plugin.cache.containsKey(e.getSender().getName().toLowerCase().trim())) {
			e.setCancelled(true);
			e.getSender().sendMessage("§7[Pris§o]§4 Voc§ est§ preso!");
		}
	}
}
