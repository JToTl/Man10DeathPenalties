package ltotj.minecraft.man10deathpenalties

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.plugin.java.JavaPlugin

class Man10DeathPenalties : JavaPlugin(),Listener {


    var enable=false
    var respawnHealth=20.0
    var respawnFoodLevel=20
    val exceptedWorlds=ArrayList<String>()
    val respawnMessages=ArrayList<String>()
    val respawnCommands=ArrayList<String>()



    override fun onEnable() {
        // Plugin startup logic
        saveDefaultConfig()
        loadConfig()
        getCommand("mdeathpenalties")!!.setExecutor(this)
        server.pluginManager.registerEvents(this,this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    fun loadConfig(){
        enable=config.getBoolean("enabled")
        respawnHealth=config.getDouble("respawnHealth")
        respawnFoodLevel=config.getInt("respawnFoodLevel")
        exceptedWorlds.clear()
        exceptedWorlds.addAll(config.getStringList("exceptedWorlds"))
        respawnMessages.clear()
        respawnMessages.addAll(config.getStringList("respawnMessages"))
        respawnCommands.clear()
        respawnCommands.addAll(config.getStringList("respawnCommands"))
    }


    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if(!sender.hasPermission("mdeathpenalties.op"))return true
        if(args.isEmpty()||args[0]=="help"){
            sender.sendMessage("/mdeathpenalties on/off/addWorld/removeWorld/reload")
            sender.sendMessage("詳しくはgithubへ!!!!!!!!")
        }
        when(args[0]){
            "reload"->{
                loadConfig()
            }
            "on"->{
                config.set("enabled",true)
                enable=true
                sender.sendMessage("onにしました")
            }
            "off"->{
                config.set("enabled",false)
                enable=false
                sender.sendMessage("offにしました")
            }
            "addWorld"->{
                if(args.size<2){
                    sender.sendMessage("ワールド名を入れてください")
                    return true
                }
                val world=server.getWorld(args[1])
                if(world==null){
                    sender.sendMessage("そんなワールドないよ!!!")
                    return true
                }
                exceptedWorlds.add(world.name)
                config.set("exceptedWorlds",exceptedWorlds)
                sender.sendMessage("追加完了")
            }
            "removeWorld"->{
                if(args.size<2){
                    sender.sendMessage("ワールド名を入れてください")
                    return true
                }
                val world=server.getWorld(args[1])
                if(world==null){
                    sender.sendMessage("そんなワールドないよ!!!")
                    return true
                }
                exceptedWorlds.remove(world.name)
                config.set("exceptedWorlds",exceptedWorlds)
                sender.sendMessage("削除完了")
            }
        }
        return true
    }


    @EventHandler
    fun respawn(e:PlayerRespawnEvent){
        if(!enable||exceptedWorlds.contains(e.player.world.name)||(e.player.hasPermission("mdeathpenalties.op")&&e.player.gameMode!=GameMode.CREATIVE&&e.player.gameMode!=GameMode.SPECTATOR))return
        val p=e.player
        for(str in respawnMessages){
            p.sendMessage(str)
        }
        server.scheduler.runTask(this,Runnable{
            p.health=respawnHealth
            p.foodLevel=respawnFoodLevel
            for(str in respawnCommands){
                server.dispatchCommand(Bukkit.getConsoleSender(),str.replace("<player>",p.name))
            }
        })
    }

}