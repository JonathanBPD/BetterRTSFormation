# 开发指南

环境、日常构建命令与代码约定。仓库级硬性约束在根目录 [AGENTS.md](../AGENTS.md)，两者冲突时以 AGENTS.md 为准。

## 环境

- JDK：CI 用 Temurin 17 构建；字节码目标是 Java 8（`sourceCompatibility/targetCompatibility = 1.8`，JDK 9+ 下自动 `options.release.set(8)`），所以 JDK 17 可直接开发。
- Gradle：使用仓库自带 wrapper（8.14.3）；Windows 下命令用 PowerShell 7（`pwsh`）。
- 依赖仅一个：`compileOnly com.github.Anuken.MindustryJitpack:core:v154.3`（对应 `minGameVersion 154`），可用 `-PmindustryVersion=...` 覆盖。JitPack POM 里非 tag 的 Arc 坐标会被 `resolutionStrategy` 重定向到 `v155.4`。
- 安卓 dex 需要 D8，探测顺序：`D8_PATH` 环境变量 → `ANDROID_SDK_ROOT` / `ANDROID_HOME` 的 `build-tools`（取最新版本）→ 工作区根的 `commandlinetools-win-*` 目录。`android.jar` 来自 SDK platforms，缺失时回退 Maven 坐标 `com.google.android:android:4.1.1.4`。都没有时 `dexAndroid` 直接失败。

## 常用命令

```powershell
.\gradlew.bat deploy        # 完整合并产物 + 自动复制到 dist/ 与 ../构建/，日常验证首选
.\gradlew.bat compileJava   # 只编译检查
.\gradlew.bat jarMerged     # 只出合并 jar（build/libs/BetterRTSFormation-<version>.jar）
.\gradlew.bat jarDesktop    # 只出桌面开发 jar（不含 dex，不可分发）
.\gradlew.bat dexAndroid    # 只出 classes.dex（build/d8/）
```

`deploy` = `jarMerged`，而 `jarMerged` 结束后自动执行两个复制任务（finalizedBy）：

| 任务 | 产物 |
| --- | --- |
| `jarMerged` | `build/libs/BetterRTSFormation-<version>.jar`（classes + 资源 + `mod.json` + `classes.dex`） |
| `copyMergedJarToDist` | 复制上者到 `dist/BetterRTSFormation-<version>.jar`（发布交付目录） |
| `copyDevArtifact` | 复制上者并重命名为 `../构建/BetterRTSFormation/BetterRTSFormation-dev.jar`（工作区暂存） |

注意区分：`jarDesktop` 的 `BetterRTSFormation-desktop.jar` 与 `jarAndroid` 的 `*-android.jar` 都是开发中间产物，不要对外分发；可分发形态只有含 `classes.dex` 的合并 jar。产物链细节见 [release.md](release.md)。

## 代码风格要点

- 两类分工：入口类（`BetterRTSFormationMod`）只放生命周期与聚合契约；所有逻辑写在静态单例 `BetterRTSFormationFeature`。新功能继续往 Feature 里加静态方法/字段，不要轻易拆出带横向依赖的新顶层类。
- 命名：类内常量/字段用 `keyEnabled`、`formationBoxActive` 这类前缀化小驼峰风格；包/类名保持 ASCII。
- 用户可见文案一律走 bundle key；新增或改名时同步维护 `bundle.properties` / `bundle_zh_CN.properties` / `bundle_zh_TW.properties` 三份。
- 输入处理：修改 `InputProcessor` 时保持全部方法实现（安卓 `AbstractMethodError` 防回归）。
- 编队编辑必须经 `applyGroupEdit()` / `collectValidUnitIds()` 的统一过滤，不要绕过 `isValidGroupUnit()` 直接写 `input.controlGroups`。

## 调试建议

- 桌面客户端装 `dist/` 下的合并 jar 冒烟；`bekBundled` 默认 `false`，独立形态下应在 `设置 → 模组` 出现自己的设置分类。
- 输入问题先确认场景守卫：对话框/聊天/搜索框打开时（`hasDialog` / `hasField` / `hasKeyboard`）快捷键与框选都会被抑制。
- 框选不生效时检查 `brf-outside-command-mode` 是否开启，且当前不在指挥模式。
- 快捷键冲突时对照[架构总览](architecture.md)的命中优先级表；排他追加默认未绑定，需先在 `设置 → 控制` 配键。
- 并入 Neon 验证：在 Neon 仓库执行同步后编译，确认设置页 brf 分组以 RbmStyle 正常渲染（见[架构总览](architecture.md)的聚合契约）。
