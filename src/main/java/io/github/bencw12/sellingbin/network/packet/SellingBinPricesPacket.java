package io.github.bencw12.sellingbin.network.packet;

import io.github.bencw12.sellingbin.world.item.SellingBinPrice;
import io.github.bencw12.sellingbin.world.item.SellingBinPrices;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SellingBinPricesPacket {
    private final SellingBinPrices prices;

    public SellingBinPricesPacket(SellingBinPrices prices) {
        this.prices = prices;
    }

    public static void encode(SellingBinPricesPacket pkt, FriendlyByteBuf buf) {
        buf.writeInt(pkt.prices.size());
        synchronized (pkt.prices) {
            for (SellingBinPrice price : pkt.prices) {
                price.writeToBuf(buf);
            }
        }
    }

    public static void handle(SellingBinPricesPacket pkt, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            SellingBinPrices.PRICES.clear();
            SellingBinPrices.PRICES.addAll(pkt.prices);
        });
        context.get().setPacketHandled(true);
    }

    public static SellingBinPricesPacket decode(FriendlyByteBuf buf) {
        int num = buf.readInt();
        SellingBinPrices prices = new SellingBinPrices(num);
        for (int i = 0; i < num; i++) {
            ItemStack item = buf.readItem();
            double multiplier = buf.readDouble();
            SellingBinPrice p = new SellingBinPrice(item, multiplier);
            prices.add(p);
        }

        return new SellingBinPricesPacket(prices);
    }
}
