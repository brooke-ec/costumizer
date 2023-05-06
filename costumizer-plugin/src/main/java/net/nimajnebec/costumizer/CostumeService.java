package net.nimajnebec.costumizer;

import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.nimajnebec.costumizer.api.json.CostumeData;
import net.nimajnebec.costumizer.configuration.CostumizerConfiguration;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CostumeService {
    private static final String PROPERTY_KEY = "textures";

    private final Costumizer plugin;
    private final CostumizerConfiguration configuration;
    private final Map<UUID, GameProfile> costumes = new HashMap<>();
    private final PlayerTeam team;

    public CostumeService(Costumizer plugin) {
        this.plugin = plugin;
        this.configuration = plugin.getConfiguration();
        this.team = new PlayerTeam(new Scoreboard(), "");
    }

    public void initialise() {
        this.team.setPlayerPrefix(PaperAdventure.asVanilla(plugin.getConfiguration().getNamePrefix()));
    }

    public ClientboundSetPlayerTeamPacket getTeamPacket() {
        return ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, true);
    }

    public void equip(Player player, CostumeData data) {
        UUID uuid = player.getUniqueId();

        // Generate Profile
        GameProfile profile = this.generateProfile(uuid, data);
        this.costumes.put(uuid, profile);

        // Reload profile on clients
        player.setPlayerProfile(player.getPlayerProfile());

        // Setup cosmetics
        this.joinTeam(profile.getName());
        player.displayName(configuration.getNamePrefix().append(Component.text(profile.getName())));
    }

    public boolean inCostume(UUID uuid) {
        return costumes.containsKey(uuid);
    }

    public GameProfile getCostume(UUID uuid) {
        return costumes.get(uuid);
    }

    private void joinTeam(String name) {
        ClientboundSetPlayerTeamPacket.Action action = ClientboundSetPlayerTeamPacket.Action.ADD;
        Packet<?> packet = ClientboundSetPlayerTeamPacket.createPlayerPacket(team, name, action);
        team.getPlayers().add(name);
        plugin.broadcast(packet);
    }

    private GameProfile generateProfile(UUID uuid, CostumeData data) {
        GameProfile profile = new GameProfile(uuid, " " + data.display);
        Property property = new Property(PROPERTY_KEY, data.properties, data.signature);
        PropertyMap map = profile.getProperties();
        map.removeAll(PROPERTY_KEY);
        map.put(PROPERTY_KEY, property);
        return profile;
    }
}
