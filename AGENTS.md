# BetterRTSFormation 开发指南（AGENTS.md）

本文件面向在本仓库工作的 AI 代理与人类协作者。面向人类的分类文档（架构 / 开发 / 发布 / 测试 / 术语表）在 [`docs/`](docs/README.md)，两者冲突时以本文件为准。

## 项目定位与边界

- Better RTS Formation（Neon 聚合模组中 id=brf）是 Mindustry 纯客户端 Java 模组：改善 RTS 编队（control group）的创建、选择与识别（快捷键编队、框选创建、编队角标）。目标游戏版本 `minGameVersion: 154`。
- 本仓库是该子模组的**真源**；Neon（`../Neon`）通过 `tools/update_submods.py` 从这里拷贝源码。`submods.json` 中 `injectBekHooks=false`，聚合契约由本仓库手工维护，不由注入脚本生成。
- 只做最小侵入修改：不顺手重构、不引入第三方依赖、不改与任务无关的文件。纯客户端，不新增游戏内容（`hidden: true`）。

## 文件结构

- `mod.json`：模组描述（`main`、`version`、`minGameVersion`、`hidden`）。本仓库没有 `mod.hjson`。
- `build.gradle`：构建、D8 dex、合并 jar、产物复制（Gradle wrapper 8.14.3）。
- `src/main/java/betterrtsformation/BetterRTSFormationMod.java`：入口 + 聚合契约（`bekBundled`、`bekBuildSettings`、`configureBundledSettingsRenderer`）。
- `src/main/java/betterrtsformation/BetterRTSFormationFeature.java`：全部功能逻辑（快捷键、框选状态机、编队编辑、角标/框选绘制、设置）。
- `src/main/resources/bundles/bundle*.properties`：文案，en / zh_CN / zh_TW 三份必须同步。
- `tmp-stubs/java/lang/Runnable.java`：D8 `--lib` 桩，勿删。
- `dist/`：本地发布交付目录（git 忽略）。
- `.github/workflows/release.yml` + `.github/release-notes/`：发布 CI 与每个版本的双语说明。

## 构建命令

```powershell
.\gradlew.bat deploy        # 合并 jar（含 classes.dex）→ build/libs/，并自动复制到 dist/ 与 ../构建/BetterRTSFormation/
.\gradlew.bat compileJava   # 只编译检查
```

- `BetterRTSFormation-desktop.jar` / `*-android.jar` 是中间产物，禁止对外分发；可分发的只有合并 jar。
- 安卓 dex 需要 D8（探测 `D8_PATH` → `ANDROID_SDK_ROOT`/`ANDROID_HOME` → 工作区 `commandlinetools-win-*`），详见 [docs/release.md](docs/release.md)。
- 纯文档任务不要求跑构建；代码任务至少 `deploy` 通过。

## 代码约束

- Java 8 字节码；只依赖 `compileOnly` 的 Mindustry core，不新增依赖。
- 逻辑进 `BetterRTSFormationFeature`；入口类只留生命周期与聚合契约。
- 编队唯一数据源是原生 `input.controlGroups`；写入必须经 `applyGroupEdit()` / `collectValidUnitIds()` 的过滤（`isValidGroupUnit`：可指挥、存活、己方队伍），不得绕过。
- `InputProcessor` 保持全部方法实现（安卓 `AbstractMethodError` 防回归）。
- 用户可见文案走 bundle key，三份语言文件同步更新。

## 与 Neon 聚合的关系（bekBundled 契约）

- 主类必须保留：`public static boolean bekBundled`、`public void bekBuildSettings(SettingsMenuDialog.SettingsTable)`、`configureBundledSettingsRenderer(...)` 回调，以及 `loadClient` 中带 `!bekBundled` 守卫的 `ui.settings.addCategory(...)`。
- 语义：独立运行时自建设置分类；Neon 置 `bekBundled = true` 并经 `bektools.BetterRTSFormationSettings#configure()` 注入 RbmStyle 渲染器，聚合设置页改调 `bekBuildSettings`。
- 新增/修改设置项时，必须同步检查本仓库 `buildDefaultSettings` 与 Neon 侧 `BetterRTSFormationSettings.build` 两处渲染。
- 改完本仓库后回 Neon 执行 `python tools/update_submods.py --only brf --check`（或全量同步）验证聚合编译与渲染；Neon 侧的同步产物不要在这里反向手改。

## 变更后验证清单

详单见 [docs/testing.md](docs/testing.md)，至少覆盖：

1. `.\gradlew.bat deploy` 成功；`dist/` 与 `../构建/BetterRTSFormation/` 产物已刷新。
2. 快捷键编队四操作（追加 / 覆盖创建 / 排他追加 / 选择）+ Delete 踢出按预期。
3. 框选（普通 / 排他 / 红框删除）在 `brf-outside-command-mode` 开启时工作，关闭时不出现。
4. 角标颜色/编号正确，失效单位不残留。
5. 独立形态设置分类出现；聚合形态（Neon）不重复出现且 RbmStyle 渲染正常。

## 提交与发布规范

- 提交信息参考现有风格：`BetterRTSFormation: ...`、`fix: ...`、`docs: ...`、`ci: ...`、`release: vX.Y.Z`；只提交与当前任务相关的文件，禁止 `git add -A` / `git add .`。
- 发版：同步改 `mod.json` 与 `build.gradle` 版本号 → 写 `.github/release-notes/vX.Y.Z.md`（双语）→ 本地 `.\gradlew.bat clean deploy` → tag `vX.Y.Z` 推送，CI 自动构建上传。详见 [docs/release.md](docs/release.md)。
- Release 必须有且只有一个 `.jar` 资产（含 `mod.json` 与 `classes.dex` 的合并 jar）；发布后用 `gh release view <tag> --json assets` 复查。

## 文档索引

- [docs/README.md](docs/README.md)：文档导航
- [docs/architecture.md](docs/architecture.md)：架构总览
- [docs/development.md](docs/development.md)：开发指南
- [docs/release.md](docs/release.md)：版本与发布
- [docs/testing.md](docs/testing.md)：测试指南
- [docs/glossary.md](docs/glossary.md)：术语表
