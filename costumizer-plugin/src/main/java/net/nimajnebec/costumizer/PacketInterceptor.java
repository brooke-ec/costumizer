package net.nimajnebec.costumizer;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class PacketInterceptor extends ChannelDuplexHandler {
    public static final String HANDLER_NAME = "costumizer_packet_interceptor";
    private static final Field entriesField;
    private final CostumeService costumeService;
    private final Costumizer plugin;
    private final Player recipient;

    static {
        entriesField = getEntriesField();
        entriesField.setAccessible(true);
    }

    private static Field getEntriesField() {
        for (Field field : ClientboundPlayerInfoUpdatePacket.class.getDeclaredFields()) {
            // TODO: Check is type of List<Entry>
            if (List.class.isAssignableFrom(field.getType()))
                return field;
        }
        throw new NoSuchElementException("Could not find entries field.");
    }

    public static void removeInterceptor(Player player) throws NoSuchElementException {
        ServerPlayer handle = ((CraftPlayer) player).getHandle();
        handle.connection.connection.channel.pipeline().remove(HANDLER_NAME);
    }

    public static void injectInterceptor(Player player) {
        PacketInterceptor handler = new PacketInterceptor(player);
        ServerPlayer handle = ((CraftPlayer) player).getHandle();
        ChannelPipeline pipeline = handle.connection.connection.channel.pipeline();
        pipeline.addBefore("packet_handler", HANDLER_NAME, handler);
    }

    public  PacketInterceptor(Player recipient) {
        this.recipient = recipient;
        this.plugin = Costumizer.getInstance();
        this.costumeService = this.plugin.getCostumeService();
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof ClientboundPlayerInfoUpdatePacket packet) {
            try {
                handlePlayerInfo(packet);
            } catch (Exception e) {
                plugin.getSLF4JLogger().error(e.toString());
                e.printStackTrace();
            }
        }
        super.write(ctx, msg, promise);
    }

    private void handlePlayerInfo(ClientboundPlayerInfoUpdatePacket msg) {
        if (!msg.actions().contains(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER)) return;

        List<ClientboundPlayerInfoUpdatePacket.Entry> entries = new ArrayList<>();
        for (ClientboundPlayerInfoUpdatePacket.Entry entry : msg.entries()) {
            UUID uuid = entry.profileId();

            if (costumeService.inCostume(uuid))
                entries.add(new ClientboundPlayerInfoUpdatePacket.Entry(uuid, costumeService.getCostume(uuid),
                        entry.listed(), entry.latency(), entry.gameMode(), entry.displayName(), entry.chatSession()));
            else entries.add(entry);
        }

        try {
            entriesField.set(msg, entries);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
