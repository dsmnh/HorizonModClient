package com.dsmnh.mobile.modclient;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Horizon 客户端模组骨架：SU 侧栏 Horizon 入口、交互界面与 ME 方块客户端资源。
 * 当前为立仓骨架；侧栏 Hook 与显示资源按企划书阶段 6 迁移（AEMobile TabOverlayHook 为底）。
 */
@Mod(modid = Tags.MODID, name = Tags.MODNAME, version = Tags.VERSION,
        acceptedMinecraftVersions = "[1.7.10]")
public final class HorizonModClient {
    public static final Logger LOG = LogManager.getLogger("dsmnhmobile-client");

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOG.info("HorizonModClient {} 已加载（骨架：侧栏入口与显示资源待迁移）", Tags.VERSION);
    }
}