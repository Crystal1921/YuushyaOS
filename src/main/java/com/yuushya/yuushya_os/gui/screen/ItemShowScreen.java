package com.yuushya.yuushya_os.gui.screen;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class ItemShowScreen extends Screen {
    private final ItemStack itemStack;
    public ItemShowScreen(ItemStack itemStack) {
        super(Component.literal("Item Show"));
        this.itemStack = itemStack;
    }
}
