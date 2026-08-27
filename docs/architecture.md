# 架构总览

改 `src/` 下的任何代码前先读本文。Better RTS Formation 是一个聚焦单一领域的小型纯客户端模组：改善 Mindustry RTS 编队（control group）的创建、选择与识别。玩家视角的说明见[主页 README](../README.md)。

## 设计原则

1. **贴着原版数据模型走**：编队数据就存在 Mindustry 原生的 `InputHandler.controlGroups`（`IntSeq[]`，存单位 id）里，本模组只做读写与渲染，不引入自己的持久化。
2. **双形态可运行**：独立安装与并入 Neon 聚合包都能工作，差异收敛在设置入口与渲染上。
3. **纯客户端**：不要求服务器安装；只影响玩家自己视角的选择、编队与角标绘制。

## 入口与生命周期

入口只有 `mod.json` 的 `main` → `betterrtsformation.BetterRTSFormationMod`（`hidden: true`，声明为不加载游戏内容的纯代码模组）。

1. **init()**：委托 `BetterRTSFormationFeature.init()`，注册三类事件：
   - `WorldLoadEvent` → `resetState()`：取消进行中的框选、清空已消费按键记录。
   - `Trigger.update` → `updateInput()`：每帧刷新设置、维护框选状态机。
   - `Trigger.draw` → 挂两个绘制层：框选矩形在 `Layer.overlayUI - 0.1f`，编队角标在 `Layer.playerName + 1f`。
2. **ClientLoadEvent** → `loadClient()`：
   - 写入设置默认值（`brf-enabled=true`、`brf-outside-command-mode=false`）；
   - `registerKeybind()`：注册 5 个前置键（分类 `better-rts-formation`）；
   - `registerBoxInputProcessor()`：把 `InputProcessor` 插到 `Core.input` 处理链最前，用于捕获框选的鼠标拖拽；
   - 设置入口：独立形态自建 `ui.settings.addCategory(...)`，聚合形态跳过（见下文契约）。

## 核心类与数据流

只有两个类：

| 类 | 职责 |
| --- | --- |
| `BetterRTSFormationMod` | 薄入口：`Mod` 子类，持有 `bekBundled` 标记与 `BundledSettingsRenderer` 回调接口，全部委托给 Feature |
| `BetterRTSFormationFeature` | 全部逻辑的静态单例类：快捷键、框选状态机、编队编辑、角标与框选绘制、设置构建 |

数据流：

```text
玩家输入（前置键 + 数字 / 鼠标拖框）
  --> handleFormationShortcut() / updateFormationBox()
  --> applyGroupEdit() / createGroup() / selectGroup() / deleteFormations()
  --> Mindustry 原生 input.controlGroups（IntSeq[]，唯一数据源）
  --> Trigger.draw：drawBadges() 逐帧重建 groupMembership 缓存并画角标
```

要点：

- 成员过滤统一走 `isValidGroupUnit()`：可指挥（`isCommandable`）、存活、且属于玩家队伍。因此编队只含单位；`selectGroup()` 还会清空 `commandBuildings`，保证选择编队后下发的命令只作用于单位。
- 编辑语义四种：`groupEditAdd`（追加，保留原成员并去重）、`groupEditAddExclusive`（先从所有编队踢出再追加）、`groupEditRemoveAll`（把选中单位从所有编队踢出）、覆盖创建（`createGroup`，清空后重建）。
- 旧存档里可能有重复 id，`removeUnitIds()` 用 while 循环清干净；`firstEmptyGroup()` 会顺手剔除失效单位后再找第一个空闲编队。
- 数字键识别双通道：硬编码主键盘/小键盘 `1-9`/`0`，外加对 `Binding.blockSelect01..10` 自定义绑定的匹配。
- 框选是一个显式状态机（`formationBox*` 字段）：开始时记录世界坐标起点，松开鼠标或松开前置键即结束；矩形内单位经 `input.selectedCommandUnits(...)` 取得。

## 快捷键

键位在 `设置 → 控制 → Better RTS Formation` 分类下，均可自定义；下表为默认值。

| 键位（bundle key） | 默认 | 作用 |
| --- | --- | --- |
| `brf-add-control-group` | 左 Ctrl | 追加 / 框选创建前置键 |
| `brf-create-control-group` | 左 Shift | 覆盖创建编队前置键 |
| `brf-exclusive-add-control-group` | 未绑定 | 排他追加 / 排他框选前置键 |
| `brf-select-control-group` | 左 Alt | 选择编队前置键 |
| `brf-delete-control-group` | Delete | 点按踢出选中单位的所有编队；与追加键同按则红框批量删除 |

快捷键命中优先级（`handleFormationShortcut`）：排他追加 > 覆盖创建 > 原生和弦抑制 > 追加 > 选择。模组启用时会吞掉原版「指挥模式 + 创建编队（`Binding.createControlGroup`）+ 数字」的冲突和弦；若配置了选择前置键，指挥模式下裸按数字的原生选择也会被拦截，保证编队操作只走前缀路径。

## 设置系统

两个布尔设置，`updateInput()` 每帧 `refreshSettings()`，切换即时生效，无需重进对局：

| key | 默认 | 含义 |
| --- | --- | --- |
| `brf-enabled` | true | 启用编队角标与严格编队选择 |
| `brf-outside-command-mode` | false | 在非指挥模式也能控制编队（框选创建的前提） |

用户可见文案全部在 `src/main/resources/bundles/`，三份语言（en / zh_CN / zh_TW）同步维护。

## 双形态与 Neon 聚合契约

本仓库是 Neon 聚合模组中 id=brf 子模组的**真源**（见 `../Neon/tools/submods.json`，`injectBekHooks=false`，契约由本仓库手工维护，不经注入脚本生成）。

主类上的契约成员：

| 成员 | 作用 |
| --- | --- |
| `public static boolean bekBundled` | 聚合标记，Neon 初始化时置 `true` |
| `bekBuildSettings(SettingsTable)` | 聚合设置契约方法，委托给 `bundledSettingsRenderer` |
| `configureBundledSettingsRenderer(...)` | 渲染回调注入口；默认实现是两个朴素 `checkPref` |
| `loadClient` 的 `!bekBundled` 守卫 | 只有独立形态才执行 `ui.settings.addCategory("@settings.brf", Icon.map, ...)` |

Neon 侧（`bektools.BekToolsMod`）装载 brf 的流程：

1. `markBundled("brf", ...)`：置 `BetterRTSFormationMod.bekBundled = true`，并调用 Neon 的 `bektools.BetterRTSFormationSettings#configure()`，通过回调把渲染器换成 `RbmStyle` 图标开关。
2. 聚合设置页调用 `bekBuildSettings(table)` 时走 Neon 注入的渲染器。

两种形态下设置 key 与语义完全一致，只有外观与入口差异。因此在本仓库改设置项时，必须同时检查 `buildDefaultSettings`（独立/兜底渲染）与 Neon 侧 `BetterRTSFormationSettings.build`（RbmStyle 渲染）两处是否同步。

## 目录速查

```text
mod.json                          模组描述（main / version / minGameVersion=154 / hidden）
build.gradle                      构建与打包（dex / 合并 jar / 产物复制）
src/main/java/betterrtsformation/
|-- BetterRTSFormationMod.java       入口与聚合契约
`-- BetterRTSFormationFeature.java   全部功能逻辑（快捷键/框选/角标/设置）
src/main/resources/bundles/       文案（en / zh_CN / zh_TW）
tmp-stubs/java/lang/Runnable.java D8 用的 java.lang.Runnable 桩
dist/                             本地发布交付目录（git 忽略）
.github/workflows/release.yml     tag 触发的发布 CI
.github/release-notes/            每个版本的双语发布说明
```

## 设计约束（务必保持）

- Java 8 字节码（JDK 9+ 下 `options.release.set(8)`）；只依赖 `compileOnly` 的 Mindustry core，不引第三方库。
- `InputProcessor` 必须实现全部方法：安卓端部分实现会触发 `AbstractMethodError`（已踩过坑，见提交 `5faad8e`）。
- 所有用户可见文案走 bundle key，不硬编码。
- 编队数据始终以 `input.controlGroups` 为准；除 bundle 外不引入任何持久化状态。
