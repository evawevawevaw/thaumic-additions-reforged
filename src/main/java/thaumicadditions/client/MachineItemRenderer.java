package thaumicadditions.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import thaumcraft.client.render.NodeRendering;
import thaumcraft.node.NodeModifier;
import thaumcraft.node.NodeType;
import thaumicadditions.registry.ModItems;

final class MachineItemRenderer extends BlockEntityWithoutLevelRenderer {
    static final ModelResourceLocation CRYSTALLIZER = model("thaumic_crystallizer_base");
    static final ModelResourceLocation GENERATOR = model("thaumic_generator_base");
    private ThaumicGeneratorRenderer generator;

    MachineItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    private static ModelResourceLocation model(String name) {
        return ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("thaumicadditions", "item/" + name));
    }

    @Override
    public void onResourceManagerReload(ResourceManager resources) {
        super.onResourceManagerReload(resources);
        generator = null;
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext display, PoseStack pose, MultiBufferSource buffers,
        int light, int overlay) {
        Minecraft minecraft = Minecraft.getInstance();
        if (stack.is(ModItems.CREATIVE_NODE.get())) {
            pose.pushPose();
            pose.translate(0.5, 0.5, 0.5);
            NodeRendering.submitCross(buffers, pose, CreativeNodeRenderer.visualAspects(), NodeType.PURE, NodeModifier.BRIGHT);
            pose.popPose();
            return;
        }
        boolean crystallizer = stack.is(ModItems.THAUMIC_CRYSTALLIZER.get());
        var model = minecraft.getModelManager().getModel(crystallizer ? CRYSTALLIZER : GENERATOR);
        for (var type : model.getRenderTypes(stack, true)) {
            minecraft.getItemRenderer().renderModelLists(model, stack, light, overlay, pose, buffers.getBuffer(type));
        }
        if (crystallizer) {
            CrystallizerRenderer.renderItem(pose, buffers, light);
        } else {
            if (generator == null) {
                var context = new BlockEntityRendererProvider.Context(minecraft.getBlockEntityRenderDispatcher(),
                    minecraft.getBlockRenderer(), minecraft.getItemRenderer(), minecraft.getEntityRenderDispatcher(),
                    minecraft.getEntityModels(), minecraft.font);
                generator = new ThaumicGeneratorRenderer(context);
            }
            generator.renderItem(pose, buffers, light, overlay);
        }
    }
}
