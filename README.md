# HorizonModClient

Horizon 客户端模组：SU 侧栏 Horizon 入口、交互界面与 ME 方块客户端显示资源（仅客户端）。

- 状态：**立仓骨架**——侧栏 Hook 与显示资源待从 AEMobile 客户端迁移（TabOverlayHook 为底，见工作区测试待办）。
- 构建：`JAVA_HOME=<zulu8> ./gradlew build`（GTNH ExampleMod1.7.10 模板，Forge 10.13.4.1614）。
- 协议：无直接网络连接；与服务端模组（`HorizonModServer`，modid 同为 `dsmnhmobile`）经游戏内事件协作。
