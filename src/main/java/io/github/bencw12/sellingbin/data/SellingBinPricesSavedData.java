package io.github.bencw12.sellingbin.data;

import io.github.bencw12.sellingbin.world.item.SellingBinPrice;
import io.github.bencw12.sellingbin.world.item.SellingBinPrices;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

public class SellingBinPricesSavedData extends SavedData {
    public static SellingBinPricesSavedData load(CompoundTag tag) {
        SellingBinPricesSavedData data = new SellingBinPricesSavedData();
        ListTag prices = tag.getList("SellingBinPrices", ListTag.TAG_COMPOUND);
        for (Tag t : prices) {
            CompoundTag price = (CompoundTag) t;
            SellingBinPrice p = new SellingBinPrice(price);
            SellingBinPrices.PRICES.add(p);
        }

        return data;
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        return SellingBinPrices.PRICES.createTag(tag);
    }

    public SellingBinPrices getPrices() {
        return SellingBinPrices.PRICES;
    }
}
