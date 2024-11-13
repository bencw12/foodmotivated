package io.github.bencw12.sellingbin.data;

import io.github.bencw12.sellingbin.world.item.SellingBinOffer;
import io.github.bencw12.sellingbin.world.item.SellingBinOffers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

public class SellingBinOffersSavedData extends SavedData {
    public static SellingBinOffersSavedData load(CompoundTag tag) {
        SellingBinOffersSavedData data = new SellingBinOffersSavedData();
        ListTag offers = tag.getList("SellingBinOffers", ListTag.TAG_COMPOUND);
        SellingBinOffers.OFFERS.clear();
        for (Tag t : offers) {
            CompoundTag offer = (CompoundTag) t;
            SellingBinOffer o = new SellingBinOffer(offer);
            SellingBinOffers.OFFERS.add(o);
        }

        return data;
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        return SellingBinOffers.OFFERS.createTag(tag);
    }

    public SellingBinOffers getOffers() {
        return SellingBinOffers.OFFERS;
    }
}
