package me.mchiappinam.pdghprisao;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	public static String mysql_url = "";
    public static String mysql_user = "";
    public static String mysql_pass = "";
    public static String tabela = "";
    public HashMap<String,Integer> cache = new HashMap<String,Integer>();
    //             getKey,getValue
    //             

	public void onEnable() {
		getServer().getConsoleSender().sendMessage("§3[PDGHPrisao] §2iniciando...");
		File file = new File(getDataFolder(),"config.yml");
		getServer().getConsoleSender().sendMessage("§3[PDGHPrisao] §2verificando se a config existe...");
		if(!file.exists()) {
			try {
				getServer().getConsoleSender().sendMessage("§3[PDGHPrisao] §2config inexistente, criando config...");
				saveResource("config_template.yml",false);
				File file2 = new File(getDataFolder(),"config_template.yml");
				file2.renameTo(new File(getDataFolder(),"config.yml"));
				getServer().getConsoleSender().sendMessage("§3[PDGHPrisao] §2config criada");
			}catch(Exception e) {getServer().getConsoleSender().sendMessage("§c[PDGHPrisao] §cERRO AO CRIAR CONFIG");}
		}
		mysql_url="jdbc:mysql://"+getConfig().getString("mySQL.ip")+":"+getConfig().getString("mySQL.porta")+"/"+getConfig().getString("mySQL.db");
		mysql_user=getConfig().getString("mySQL.usuario");
		mysql_pass=getConfig().getString("mySQL.senha");
		tabela=getConfig().getString("mySQL.tabela");
		getServer().getConsoleSender().sendMessage("§3[PDGHPrisao] §2conectando no MySQL...");
		try {
			Connection con = DriverManager.getConnection(mysql_url,mysql_user,mysql_pass);
			if (con == null) {
				getLogger().warning("ERRO: Conexao ao banco de dados MySQL falhou!");
				getServer().getPluginManager().disablePlugin(this);
			}else{
				Statement st = con.createStatement();
				st.execute("CREATE TABLE IF NOT EXISTS `"+tabela+"` (`nick` text, `tempo` INT(255))");
				st.close();
				getServer().getConsoleSender().sendMessage("§3[PDGHPrisao] §2Conectado ao banco de dados MySQL!");
			}
			con.close();
		}catch (SQLException e) {
			getLogger().warning("ERRO: Conexao ao banco de dados MySQL falhou!");
			getLogger().warning("ERRO: "+e.toString());
			getServer().getPluginManager().disablePlugin(this);
		}
		if (getServer().getPluginManager().getPlugin("Legendchat") == null) {
			getLogger().warning("ERRO: Legendchat nao encontrado!");
		}else{
			getServer().getConsoleSender().sendMessage("§3[PDGHPrisao] §2Sucesso: Legendchat encontrado.");
			getServer().getPluginManager().registerEvents(new ListenerLegendchat(this), this);
		}
		getServer().getConsoleSender().sendMessage("§3[PDGHPrisao] §2registrando eventos...");
		getServer().getPluginManager().registerEvents(new Listeners(this), this);
		getServer().getConsoleSender().sendMessage("§3[PDGHPrisao] §2eventos registrados");
		getServer().getConsoleSender().sendMessage("§3[PDGHPrisao] §2definindo comandos...");
	    getServer().getPluginCommand("prisao").setExecutor(new Comando(this));
	    getServer().getPluginCommand("prender").setExecutor(new Comando(this));
	    getServer().getPluginCommand("soltar").setExecutor(new Comando(this));
		getServer().getConsoleSender().sendMessage("§3[PDGHPrisao] §2comandos definidos");
		cacheToDB();
		contagemPrisao();
		int online = getServer().getOnlinePlayers().size();
		if(online > 0) {
	  		for(Player p : getServer().getOnlinePlayers())
	  			if(p!=null)
	  				join(p);
		}
		getServer().getConsoleSender().sendMessage("§3[PDGHPrisao] §2ativado - Developed by mchiappinam");
		getServer().getConsoleSender().sendMessage("§3[PDGHPrisao] §2Acesse: http://pdgh.com.br/");
		getServer().getConsoleSender().sendMessage("§3[PDGHPrisao] §2Acesse: https://hostload.com.br/");
	}
	
	public void add(String sender, String p, int tempo, String motivo) {
		if(existe(p)) {
			if(getServer().getPlayerExact(p)!=null) {
				getServer().broadcastMessage("§7[Pris§o-"+sender+"]§4 "+getServer().getPlayerExact(p).getName()+" teve sua pena aumentada por mais "+tempo+" segundos. ("+motivo+")");
				getServer().getPlayerExact(p).sendMessage("§c§l"+tempo+" segundos foram adicionados em sua pena! >:)");
				getServer().getPlayerExact(p).playSound(getServer().getPlayerExact(p).getLocation(), Sound.ENDERDRAGON_DEATH, 1.0F, 1.0F);
			}else {
				getServer().broadcastMessage("§7[Pris§o-"+sender+"]§4 "+p+" teve sua pena aumentada por mais "+tempo+" segundos. ("+motivo+")");
			}
		}else{
			if(getServer().getPlayerExact(p)!=null) {
				tpToPrisao(getServer().getPlayerExact(p));
				getServer().broadcastMessage("§7[Pris§o-"+sender+"]§4 "+getServer().getPlayerExact(p).getName()+" foi preso por "+tempo+" segundos. ("+motivo+")");
				getServer().getPlayerExact(p).playSound(getServer().getPlayerExact(p).getLocation(), Sound.ENDERDRAGON_DEATH, 1.0F, 1.0F);
				getServer().getPlayerExact(p).sendMessage("§c§lVoc§ foi preso por "+tempo+" segundos! >:)");
				getServer().getPlayerExact(p).sendMessage("§fHey");
				getServer().getPlayerExact(p).sendMessage("§fEu sou o dolinho seu amiguinho e quero lhe ajudar!");
				getServer().getPlayerExact(p).sendMessage("§fQue tal conferir as regras? http://pdgh.com.br/regras/");
				getServer().getPlayerExact(p).sendMessage("§fN§o seja mais um(a) menino(a) mal ein! Boa divers§o =D");
			}else {
				getServer().broadcastMessage("§7[Pris§o-"+sender+"]§4 "+p+" foi preso por "+tempo+" segundos. ("+motivo+")");
			}
		}
		if(getServer().getPlayerExact(p)!=null)
			tpToPrisao(getServer().getPlayerExact(p));
		cache.put(p.toLowerCase().trim(), getTempo(p)+tempo);
	}
	
    public void tpToPrisao(Player p) {
		if(getConfig().contains("locais")) {
			getServer().getConsoleSender().sendMessage("§3[PDGHPrisao] §2carregando locais...");
			List<String> lista=new ArrayList<String>();
			lista=getConfig().getStringList("locais");
			getServer().getConsoleSender().sendMessage("§3[PDGHPrisao] §2locais carregados com sucesso!");
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
			spawn = new Location(getServer().getWorld(zero),Double.parseDouble(um)+0.5,Double.parseDouble(dois)+1,Double.parseDouble(tres)+0.5);
			p.closeInventory();
			if(existe(p.getName().toLowerCase().trim())) {
				int tempo=getTempo(p.getName().toLowerCase().trim());
				cache.remove(p.getName().toLowerCase().trim());
				p.teleport(spawn);
				cache.put(p.getName().toLowerCase().trim(), tempo);
				return;
			}
			p.teleport(spawn);
		}
    }
	
	public void contagemPrisao() {
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
	  		public void run() {
	  			menosUmPrisao();
	  		}
	  	}, 0, 1*20);
	}
	
	public void menosUmPrisao() {
		for(String nick : cache.keySet()) {
			if(getServer().getPlayerExact(nick)!=null) {
				int tempo=getTempo(nick)-1;
				if(tempo<=0) {
					cache.remove(nick);
					remove(getServer().getPlayerExact(nick));
			        World w = getServer().getWorld(getConfig().getString("mundoPadrao"));
			        if (w != null)
			        	getServer().getPlayerExact(nick).teleport(w.getSpawnLocation());
			        else
			        	getServer().getPlayerExact(nick).sendMessage("§cOcorreu um erro. Notifique algu§m da STAFF.");
			        getServer().getPlayerExact(nick).sendMessage("§7[Pris§o]§a Voc§ est§ solto!");
					return;
				}
				cache.put(nick, tempo);
			}
		}
	}
	
	public boolean existe(String p) {
		if(cache.containsKey(p.toLowerCase().trim()))
			return true;
		return false;
	}
	
	public int getTempo(String p) {
		if(existe(p))
			return cache.get(p.toLowerCase().trim());
		return 0;
	}
	    
	public void onDisable() {
		for(String nick : cache.keySet()) {
			try {
				Connection con = DriverManager.getConnection(Main.mysql_url,Main.mysql_user,Main.mysql_pass);
				//Prepared statement
				PreparedStatement pst = con.prepareStatement("SELECT `nick`,`tempo` FROM `"+Main.tabela+"` WHERE( `nick`='"+nick.toLowerCase().trim()+"');");
				ResultSet rs = pst.executeQuery();
				boolean existe=false;
				while(rs.next()) {
					existe=true;
					rs.close();
					pst.close();
					PreparedStatement pst1 = con.prepareStatement("UPDATE `"+Main.tabela+"` SET `tempo`="+getTempo(nick)+" WHERE( `nick`='"+nick.toLowerCase().trim()+"');");
					pst1.executeUpdate();
					pst1.close();
					con.close();
					break;
				}
				if(!existe) {
					//Prepared statement
					PreparedStatement pst1 = con.prepareStatement("INSERT INTO `"+Main.tabela+"`(nick, tempo) VALUES(?, ?)");
					//Values
					pst1.setString(1, nick.toLowerCase().trim());
					pst1.setInt(2, getTempo(nick));
					//Do the MySQL query
					pst1.executeUpdate();
					pst1.close();
					con.close();
					break;
				}
			}catch (SQLException ex) {
				System.out.print(ex);
				break;
			}
		}
		getServer().getConsoleSender().sendMessage("§3[PDGHPrisao] §2desativado - Developed by mchiappinam");
		getServer().getConsoleSender().sendMessage("§3[PDGHPrisao] §2Acesse: http://pdgh.com.br/");
		getServer().getConsoleSender().sendMessage("§3[PDGHPrisao] §2Acesse: https://hostload.com.br/");
	}
	
	public void cacheToDB() {
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
	  		public void run() {
	  			saveAllCache();
	  		}
	  	}, 0, 120*20);
	}
	
	public void saveAllCache() {
		Threads t = new Threads(this,"saveAllCache");
		t.start();
	}
	
	public void join(Player p) {
		Threads t = new Threads(this,"join",p);
		t.start();
	}
	
	public void remove(Player p) {
		Threads t = new Threads(this,"remove",p);
		t.start();
	}
}
