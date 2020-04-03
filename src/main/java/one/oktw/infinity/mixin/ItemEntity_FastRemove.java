package one.oktw.infinity.mixin;

import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ItemEntity.class)
public class ItemEntity_FastRemove {
    @ModifyConstant(method = "tick", constant = @Constant(intValue = 6000))
    private int changeRemoveDelay(int value) {
        return 200;
    }
}
