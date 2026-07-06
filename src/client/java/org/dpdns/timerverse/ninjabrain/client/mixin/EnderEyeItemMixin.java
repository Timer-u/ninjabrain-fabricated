package org.dpdns.timerverse.ninjabrain.client.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnderEyeItem;
import net.minecraft.world.level.Level;

import org.dpdns.timerverse.ninjabrain.client.detect.AutoDetectionHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderEyeItem.class)
public class EnderEyeItemMixin {

	@Inject(at = @At("HEAD"), method = "use")
	private void onUse(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		if (!level.isClientSide()) return;
		AutoDetectionHandler.INSTANCE.onThrow(player.getX(), player.getZ());
	}

}
