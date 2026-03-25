package com.yuushya.yuushya_os.item;

import com.yuushya.yuushya_os.util.ClientMethod;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class PanelItem extends Item {
    public PanelItem() {
        super(new Properties()
                .rarity(Rarity.EPIC)
                .stacksTo(1));
    }

    public @NotNull InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (pPlayer instanceof LocalPlayer) {
            ClientMethod.openCreativeWorkshopScreen();
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }
}
