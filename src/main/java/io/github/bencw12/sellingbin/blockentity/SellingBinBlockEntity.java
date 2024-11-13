package io.github.bencw12.sellingbin.blockentity;

import io.github.bencw12.sellingbin.gui.inventory.SellingBinMenu;
import io.github.bencw12.sellingbin.item.SellingBinItems;
import io.github.bencw12.sellingbin.world.item.SellingBinOffers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;

public class SellingBinBlockEntity extends BlockEntity implements Container, MenuProvider, Nameable {
    private final ItemStackHandler inventory = new ItemStackHandler(this.getContainerSize()) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            SellingBinBlockEntity.this.onContentChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    private final ContainerOpenersCounter openersCounter;
    private long lastTick;

    public SellingBinBlockEntity(BlockPos pos, BlockState state) {
       super(SellingBinBlockEntities.SELLING_BIN_BLOCK_ENTITY.get(), pos, state);
        this.openersCounter = new ContainerOpenersCounter() {
            protected void onOpen(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state) {}

            protected void onClose(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state) { }

            protected void openerCountChanged(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, int j, int i) {
            }

            protected boolean isOwnContainer(@NotNull Player player) {
                if (player.containerMenu instanceof SellingBinMenu menu) {
                    Container container = menu.getContainer();
                    return container == SellingBinBlockEntity.this;
                } else {
                    return false;
                }
            }
        };
        this.lastTick = -1;
    }



    public boolean isEmpty() {
        return this.getItems().stream().allMatch(ItemStack::isEmpty);
    }

    public @NotNull ItemStack getItem(int idx) {
        return this.getItems().get(idx);
    }

    public @NotNull ItemStack removeItem(int slot, int amount) {
        if (slot >= 0 && slot < inventory.getSlots()) {
            ItemStack stackInSlot = inventory.getStackInSlot(slot);

            if (stackInSlot.isEmpty()) {
                return ItemStack.EMPTY;
            }

            int toRemove = Math.min(amount, stackInSlot.getCount());
            ItemStack removedStack = stackInSlot.split(toRemove);

            if (stackInSlot.isEmpty()) {
                setItem(slot, ItemStack.EMPTY);
            } else {
                inventory.setStackInSlot(slot, stackInSlot);
            }

            onContentChanged();
            return removedStack;
        }
        return ItemStack.EMPTY;
    }

    public @NotNull ItemStack removeItemNoUpdate(int idx) {
        return ContainerHelper.takeItem(this.getItems(), idx);
    }

    public void setItem(int slot, @NotNull ItemStack item) {
        if (slot >= 0 && slot < inventory.getSlots()) {
            if (item.getCount() > this.getMaxStackSize()) {
                item.setCount(this.getMaxStackSize());
            }
            inventory.setStackInSlot(slot, item);
        }
        this.setChanged();
        this.onContentChanged();
    }

    public boolean stillValid(@NotNull Player p) {
        return Container.stillValidBlockEntity(this, p);
    }
    public void clearContent() {
        this.getItems().clear();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> this.inventory);
    }

    public int getTotalItems() {
        int count = 0;
        for (int i = 0; i < this.inventory.getSlots(); i++) {
            ItemStack itemstack = this.inventory.getStackInSlot(i);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() == SellingBinItems.EMERALD_NUGGET_ITEM.get() || itemstack.isEdible()) {
                    count += itemstack.getCount();
                }
            }
        }

        return count;
    }


    public ArrayList<Item> getRenderStacks() {
        ArrayList<Item> stacks = new ArrayList<>();
        for (int i = 0; i < this.inventory.getSlots(); i++) {
            ItemStack itemstack = this.inventory.getStackInSlot(i);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() == SellingBinItems.EMERALD_NUGGET_ITEM.get() || itemstack.isEdible()) {
                    if (!stacks.contains(itemstack.getItem())) {
                        stacks.add(itemstack.getItem());
                    }
                }
            }
        }
        return stacks;
    }


    @javax.annotation.Nullable
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInv, @NotNull Player player) {
        SellingBinMenu menu = new SellingBinMenu(id, playerInv, this.inventory);
        menu.setContainer(this);
        return menu;
    }

    protected IItemHandler createUnSidedHandler() {
        return new InvWrapper(this);
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @org.jetbrains.annotations.Nullable Direction side) {
        return cap == ForgeCapabilities.ITEM_HANDLER && !this.remove ? this.lazyItemHandler.cast() : super.getCapability(cap, side);
    }

    public void invalidateCaps() {
        super.invalidateCaps();
        this.lazyItemHandler.invalidate();
    }

    public void reviveCaps() {
        super.reviveCaps();
        this.lazyItemHandler = LazyOptional.of(this::createUnSidedHandler);
    }

    public void onContentChanged() {
        if (this.level != null) {
            setChanged();
            if (!this.level.isClientSide()) {
                this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
            }
        }
    }


    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    public int getContainerSize() {
        return 27;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("CustomName", Component.Serializer.toJson(this.getDisplayName()));
        tag.put("Inventory", inventory.serializeNBT());
    }

    protected NonNullList<ItemStack> getItems() {
        NonNullList<ItemStack> items = NonNullList.withSize(this.inventory.getSlots(), ItemStack.EMPTY);
        for (int i = 0; i < this.inventory.getSlots(); i++) {
            items.set(i, this.inventory.getStackInSlot(i));
        }
        this.onContentChanged();
        this.setChanged();
        return items;
    }

    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.inventory.deserializeNBT(tag.getCompound("Inventory"));
    }

    public void startOpen(@NotNull Player player) {
        if (!this.remove && !player.isSpectator()) {
            this.openersCounter.incrementOpeners(player, Objects.requireNonNull(this.getLevel()), this.getBlockPos(), this.getBlockState());
        }

    }

    public void stopOpen(@NotNull Player player) {
        if (!this.remove && !player.isSpectator()) {
            this.openersCounter.decrementOpeners(player, Objects.requireNonNull(this.getLevel()), this.getBlockPos(), this.getBlockState());
        }

        this.setChanged();

    }

    public void recheckOpen() {
        if (!this.remove) {
            this.openersCounter.recheckOpeners(Objects.requireNonNull(this.getLevel()), this.getBlockPos(), this.getBlockState());
        }

    }

    @Override
    public @NotNull Component getName() {
        return this.getDisplayName();
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.selling_bin.selling_bin");
    }

    public void setLastTick(long lastTick) {
        this.lastTick = lastTick;
    }

    public long getLastTick() {
        return lastTick;
    }

    public void sellItems(ServerLevel level, BlockPos pos) {
        int numEmeralds = 0;
        // count total
        for (int i = 0; i < this.getItems().size(); i++) {
            ItemStack stack = this.getItems().get(i);

            int cost = SellingBinOffers.getEmeraldTotal(stack);

            if (cost > 0) {
                this.setItem(i, ItemStack.EMPTY);
            }

            numEmeralds += cost;
        }

        // replace with emeralds
        for (int i = 0; i < this.getItems().size(); i++) {
            ItemStack stack = this.getItems().get(i);
            if (numEmeralds > 0) {
                // stack already is emeralds
                if (stack.getItem() == SellingBinItems.EMERALD_NUGGET_ITEM.get() && stack.getCount() < stack.getMaxStackSize()) {
                    int diff = stack.getMaxStackSize() - stack.getCount();
                    int add = Math.min(diff, numEmeralds);
                    stack.setCount(stack.getCount() + add);
                    numEmeralds -= add;
                }
                // stack is empty
                if (stack.isEmpty()) {
                    int thisCount = Math.min(numEmeralds, stack.getMaxStackSize());
                    ItemStack emeralds = new ItemStack(SellingBinItems.EMERALD_NUGGET_ITEM.get(), thisCount);
                    this.setItem(i, emeralds);
                    numEmeralds -= thisCount;
                }
            }
        }

        // drop the rest on top
        if (numEmeralds > 0) {
            ItemStack emeralds = new ItemStack(SellingBinItems.EMERALD_NUGGET_ITEM.get(), numEmeralds);
            ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 1 + 0.5, pos.getZ() + 0.5, emeralds);
            entity.setDeltaMovement(level.random.nextGaussian() * 0.05, 0.2, level.random.nextGaussian() * 0.05);

            level.addFreshEntity(entity);
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        if (level instanceof ServerLevel serverLevel) {
            long time = serverLevel.getDayTime() % 24000;

            if (blockEntity instanceof SellingBinBlockEntity sellingBinBlockEntity) {
                if (sellingBinBlockEntity.getLastTick() > time) {
                    sellingBinBlockEntity.sellItems((ServerLevel)level, pos);
                }

                sellingBinBlockEntity.setLastTick(time);
            }
        }
    }
}
