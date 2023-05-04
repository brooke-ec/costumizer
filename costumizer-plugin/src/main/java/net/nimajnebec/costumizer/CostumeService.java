package net.nimajnebec.costumizer;

import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.nimajnebec.costumizer.api.json.CostumeData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CostumeService {

    private final Costumizer plugin;

    public CostumeService(Costumizer plugin) {
        this.plugin = plugin;
    }

    public void equip(ServerPlayer player, CostumeData data) {
        String name = player.getName().getString();
        Property property = new Property("textures", data.properties, data.signature);
        GameProfile profile = new GameProfile(player.getUUID(), " " + data.display);
        PropertyMap propertyMap = profile.getProperties();
        propertyMap.removeAll("textures");
        propertyMap.put("textures", property);
        Player bukkitPlayer = player.getBukkitEntity();
        bukkitPlayer.setPlayerProfile(new CraftPlayerProfile(profile));
        bukkitPlayer.displayName(Component.text(data.display));
        profile = new GameProfile(player.getUUID(), name);
        player.gameProfile = profile;

        Scoreboard scoreboard = player.getScoreboard();
        PlayerTeam team = new PlayerTeam(new Scoreboard(), "");
        team.setPlayerPrefix(PaperAdventure.asVanilla(plugin.getConfiguration().getNamePrefix()));
        team.getPlayers().add(" " + data.display);
        ClientboundSetPlayerTeamPacket packet = ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, true);
        for (ServerPlayer entityplayer : (List<ServerPlayer>) player.server.getPlayerList().players) {
            entityplayer.connection.send(packet);
        }
    }
}
