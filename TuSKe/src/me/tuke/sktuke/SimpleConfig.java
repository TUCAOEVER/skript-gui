package me.tuke.sktuke;

import java.io.*;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.bukkit.plugin.java.JavaPlugin;


public class SimpleConfig{

	private JavaPlugin pl;
	private HashMap<String, String> map = new HashMap<>();
	public SimpleConfig(JavaPlugin plugin){
		pl = plugin;
		File f = new File(plugin.getDataFolder(), "config.yml");
		if (!f.exists()){
			f.mkdirs();
		}
	}
	public void loadDefault(){
		setDefault("use_metrics", true,
			"#Use metrics to send anonymous data about your server. The data that",
			"#is sent are:",
			"#",
			"#Players currently online (not max player count)",
			"#Version of the server (the same version you see in /version)",
			"#Version of this plugin",
			"#",
			"#If you don't agree with this, you can set it to false freely.",
			"#These values will be used only for statistic for this plugin.");
		setDefault("updater.check_for_new_update", true,
			"#It will check for new update everytime the server starts or",
			"#when someone use the command /tuske update check");
		setDefault("updater.auto_update", false,
			"#It will auto update the plugin. When there is a new version,",
			"#the plugin will download it and update when the server restarts.",
			"#Warning: I can't guarantee that the plugin is free of bugs that",
			"#can come in newest updates. I don't recommend to use in your main",
			"#server.",
			"#You can still download/update your plugin by command, see more in",
			"#/tuske update");
		setDefault("updater.download_pre_releases", false,
			"#Download pre-releases.",
			"#Note: pre-releases versions shoudln't be used in your main server.",
			"#It's just to test new incomming features only!!");
		//replace the old config with the new one.
		String str = "use-metrics";
		if (pl.getConfig().isBoolean(str)){
			pl.getConfig().set(str.replaceAll("\\-", "_"), pl.getConfig().getBoolean(str));
			pl.getConfig().set(str, null);
		}
		for (String var : new String[]{"check-for-new-update", "auto-update"}){
			if (pl.getConfig().isBoolean(var)){
				pl.getConfig().set("updater." + var.replaceAll("\\-", "_"), pl.getConfig().getBoolean(var));
				pl.getConfig().set(var, null);
			}
			
		}
	}
	private boolean setDefault(String path, Object value, String... comments){
		if (!map.containsKey(path)){
			map.put(path, (map.size() > 0 ? "\n" : "")+ StringUtils.join(comments, "\n"));
		}
		if (!pl.getConfig().isSet(path)){
			pl.getConfig().set(path, value);
			return true;
		}
		return false;
		
	
	}
	public void save(){
		try {			
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(pl.getDataFolder(), "config.yml")));
			String str = saveToString();
			//TuSKe.log("\n\n\t\t\tTeste\n\n" + str + "\n\n\n\n\n");
			bw.write(str);
			bw.flush();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private String saveToString(){
		String toFile = pl.getConfig().saveToString();
		for (String key : map.keySet()){
			int last = key.split("\\.").length -1;
			String comment = map.get(key);
			String space = "";//updater:(.+)update:
			for (int x = 0; x < last; x++){
				space = space + "  ";
			}
			comment = comment.replaceAll("\n", "\n" + space) + "\n";
			String regex = keyToRegex(key);
			TuSKe.debug(key, last, "-" +space+ "-", regex, comment);
			if (!key.equalsIgnoreCase(regex))
				toFile = toFile.replaceFirst("(?s)"+ regex, "$1$2" +  comment + "$3");
			else
				toFile = toFile.replaceFirst(key, comment + key);
		}
		return toFile;
	}
	private String keyToRegex(String key){
		/*if (key.contains(".")){
			String[] array = key.split("\\.");
			return array[0] + ":(.+)" + array[array.length -1] + ":";
		}
		return key.replaceAll("\\.", ":(.+)") + ":";*/
		
		return key.replaceAll("^((\\w+(\\s+|\\-)?)+)(\\.(.+\\.)?)((\\w+(\\s+|\\-)?)+)$", "($1:)(.+)($6:)");
	}
}
