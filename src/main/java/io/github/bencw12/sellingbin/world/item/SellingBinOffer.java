package io.github.bencw12.sellingbin.world.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class SellingBinOffer {
    private final ItemStack item;
    private final double multiplier;

    public SellingBinOffer(CompoundTag tag) {
        this.item = ItemStack.of(tag.getCompound("item"));
        this.multiplier = tag.getDouble("multiplier");
    }

    public SellingBinOffer(ItemStack item, double multiplier) {
        this.item = item;
        this.multiplier = multiplier;
    }

    public ItemStack getItem() { return this.item; }

    public CompoundTag createTag() {
        CompoundTag tag = new CompoundTag();
        tag.put("item", this.item.save(new CompoundTag()));
        tag.putDouble("multiplier", this.multiplier);

        return tag;
    }

    public void writeToBuf(FriendlyByteBuf buf) {
        buf.writeItem(this.item);
        buf.writeDouble(this.multiplier);
    }

    public int getOffer() {
        return Math.max(1, (int)(((float) this.item.getFoodProperties(null).getNutrition()) * this.multiplier));
    }
}
