package com.dsmnh.mobile.modclient;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

/**
 * Horizon 客户端模组：SU 侧栏 Horizon 入口（数据驱动，资源见 assets/dsmnhmobile/sidebar_*.json）
 * 与无 SU 时的自有侧栏后备（企划书 02）。
 *
 * <p>SU 在场时按钮由 ServerUtilities 的 SidebarButtonManager 从本模组资源加载，本类不做任何事；
 * SU 缺席时 {@link OwnSidebar} 注册 HUD 常驻入口 + 快捷键 H 触发（HUD 无鼠标焦点，键位是 1.7.10 的标准交互方式）。
 */
@Mod(modid = Tags.MODID, name = Tags.MODNAME, version = Tags.VERSION,
        acceptedMinecraftVersions = "[1.7.10]")
public final class HorizonModClient {
    public static final Logger LOG = LogManager.getLogger("dsmnhmobile-client");

    private static final String SU_SIDEBAR = "serverutils.client.gui.SidebarButtonManager";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOG.info("HorizonModClient {} 已加载", Tags.VERSION);
    }

    @Mod.EventHandler
    public void init(cpw.mods.fml.common.event.FMLInitializationEvent event) {
        if (!hasSuSidebar()) {
            OwnSidebar.init();
            LOG.info("未发现 ServerUtilities 侧栏，启用自有 HUD 侧栏（键位：H）");
        } else {
            LOG.info("已发现 ServerUtilities：侧栏入口交由 SU sidebar_buttons.json 数据驱动");
        }
    }

    /** 只做类存在性检查，不引入 SU 编译依赖。 */
    private static boolean hasSuSidebar() {
        try {
            Class.forName(SU_SIDEBAR);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * 自有侧栏（SU 缺席时）：左上角常驻小面板 + H 键触发队伍信息查询。
     * 触发行为与 SU 按钮 click 语义一致（/dsmnhmobile team info，服务端命令）。
     */
    @SideOnly(Side.CLIENT)
    static final class OwnSidebar {
        private static final String ICON = "dsmnhmobile:textures/gui/sidebar/horizon.png";
        private static boolean registered;
        private static KeyBinding openKey;

        private OwnSidebar() {}

        static void init() {
            if (registered) {
                return;
            }
            registered = true;
            openKey = new KeyBinding("key.dsmnhmobile.ownsidebar", Keyboard.KEY_H, "key.categories.dsmnhmobile");
            ClientRegistry.registerKeyBinding(openKey);
            MinecraftForge.EVENT_BUS.register(new RenderHook());
            cpw.mods.fml.common.FMLCommonHandler.instance().bus().register(new KeyHook());
        }

        private static final class RenderHook {
            @SubscribeEvent
            public void onRender(RenderGameOverlayEvent.Post event) {
                if (event.type != RenderGameOverlayEvent.ElementType.ALL) {
                    return;
                }
                Minecraft mc = Minecraft.getMinecraft();
                if (mc.thePlayer == null || mc.currentScreen != null) {
                    return;
                }
                ScaledResolution res = event.resolution;
                int x = 4;
                int y = 4;
                // 面板：半透明底 + 图标 + 文案（与 SU 按钮同源的图标与语言键）
                Gui.drawRect(x, y, x + 74, y + 18, 0x66000000);
                mc.getTextureManager().bindTexture(new net.minecraft.util.ResourceLocation(ICON));
                drawIcon(x + 2, y + 2, 14, 14);
                mc.fontRenderer.drawStringWithShadow(
                        I18n.format("sidebar_button.dsmnhmobile.horizon"), x + 19, y + 5, 0xFFFFFF);
            }

            /** 16x16 贴图按目标尺寸绘制（GTNH 映射无 drawModalRectWithCustomSizedTexture，走 Tessellator）。 */
            private void drawIcon(int x, int y, int w, int h) {
                org.lwjgl.opengl.GL11.glColor4f(1f, 1f, 1f, 1f);
                net.minecraft.client.renderer.Tessellator tess = net.minecraft.client.renderer.Tessellator.instance;
                tess.startDrawingQuads();
                tess.addVertexWithUV(x, y + h, 0, 0, 1);
                tess.addVertexWithUV(x + w, y + h, 0, 1, 1);
                tess.addVertexWithUV(x + w, y, 0, 1, 0);
                tess.addVertexWithUV(x, y, 0, 0, 0);
                tess.draw();
            }
        }

        private static final class KeyHook {
            @SubscribeEvent
            public void onKey(InputEvent.KeyInputEvent event) {
                Minecraft mc = Minecraft.getMinecraft();
                if (mc.thePlayer == null || mc.currentScreen != null) {
                    return;
                }
                if (openKey.isPressed()) {
                    mc.thePlayer.sendChatMessage("/dsmnhmobile team info");
                }
            }
        }
    }
}
