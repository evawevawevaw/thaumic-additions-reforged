package thaumicadditions.client.emi;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.serializer.EmiIngredientSerializer;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.client.platform.GuiBlit;
import thaumicadditions.magic.MagicKind;

final class NetworkResourceStack extends EmiStack {
    private static final ResourceLocation TEXTURE = ResourceLocation.parse("thaumicadditions:textures/particle/vis_drip/fly.png");
    private final MagicKind kind;

    NetworkResourceStack(MagicKind kind, long amount) {
        this.kind = java.util.Objects.requireNonNull(kind);
        this.amount = amount;
    }

    @Override
    public EmiStack copy() {
        return new NetworkResourceStack(kind, amount).setChance(chance).setRemainder(getRemainder().copy());
    }

    @Override
    public boolean isEmpty() {
        return amount <= 0;
    }

    @Override
    public DataComponentPatch getComponentChanges() {
        return DataComponentPatch.EMPTY;
    }

    @Override
    public Object getKey() {
        return getId();
    }

    @Override
    public ResourceLocation getId() {
        return ResourceLocation.fromNamespaceAndPath("thaumicadditions", "network_resource/" + kind.name().toLowerCase(Locale.ROOT));
    }

    @Override
    public Component getName() {
        return Component.translatable(kind == MagicKind.VIS ? "emi.thaumicadditions.network_vis" : "emi.thaumicadditions.network_taint");
    }

    @Override
    public List<Component> getTooltipText() {
        return List.of(getName(), Component.translatable("emi.thaumicadditions.network_amount", amount),
            Component.translatable("emi.thaumicadditions.network_supply"));
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, float delta, int flags) {
        var font = Minecraft.getInstance().font;
        if ((flags & EmiIngredient.RENDER_ICON) != 0) {
            GuiBlit.blit(graphics, TEXTURE, x, y, 16, 16, 0, 0, 4, 4, 4, 4, 0xFF000000 | kind.color());
            graphics.drawString(font, kind == MagicKind.VIS ? "V" : "T", x + 5, y + 3, -1, true);
        }
        if ((flags & EmiIngredient.RENDER_AMOUNT) != 0 && amount != 1) {
            String number = Long.toString(amount);
            float scale = Math.min(0.65F, 16.0F / Math.max(1, font.width(number)));
            graphics.pose().pushPose();
            graphics.pose().translate(x + 16, y + 16, 250);
            graphics.pose().scale(scale, scale, 1);
            graphics.drawString(font, number, -font.width(number), -font.lineHeight, -1, true);
            graphics.pose().popPose();
        }
    }

    static final class Serializer implements EmiIngredientSerializer<NetworkResourceStack> {
        @Override
        public String getType() {
            return "thaumicadditions:network_resource";
        }

        @Override
        public JsonElement serialize(NetworkResourceStack stack) {
            JsonObject json = new JsonObject();
            json.addProperty("type", getType());
            json.addProperty("kind", stack.kind.name().toLowerCase(Locale.ROOT));
            json.addProperty("amount", stack.amount);
            json.addProperty("chance", stack.chance);
            if (!stack.getRemainder().isEmpty()) {
                json.add("remainder", EmiIngredientSerializer.getSerialized(stack.getRemainder()));
            }
            return json;
        }

        @Override
        public EmiIngredient deserialize(JsonElement element) {
            try {
                JsonObject json = element.getAsJsonObject();
                MagicKind kind = MagicKind.valueOf(json.get("kind").getAsString().toUpperCase(Locale.ROOT));
                long amount = json.get("amount").getAsLong();
                float chance = json.has("chance") ? json.get("chance").getAsFloat() : 1;
                if (amount <= 0 || !Float.isFinite(chance) || chance < 0 || chance > 1) {
                    return EmiStack.EMPTY;
                }
                EmiStack stack = new NetworkResourceStack(kind, amount).setChance(chance);
                if (json.has("remainder") && EmiIngredientSerializer.getDeserialized(json.get("remainder")) instanceof EmiStack remainder) {
                    stack.setRemainder(remainder);
                }
                return stack;
            } catch (RuntimeException invalid) {
                return EmiStack.EMPTY;
            }
        }
    }
}
