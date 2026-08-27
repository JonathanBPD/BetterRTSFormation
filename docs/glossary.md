# 术语表

按主题归类。每条说明它在 Better RTS Formation 语境下的准确含义。

## 编队核心概念

### 控制组 / 编队（control group）
Mindustry 原生 RTS 指挥的单位分组，数据存放在 `InputHandler.controlGroups`（`IntSeq[]`，存单位 id）。共 10 组，以 `1`-`9`/`0` 标记。本模组不新建数据结构，直接读写该数组。

### 前置键（prefix key）
与数字键配合使用的功能修饰键：追加 / 覆盖创建 / 排他追加 / 选择。前置键本身不触发动作，决定「按数字时做什么」。全部在 `设置 → 控制` 可自定义，文档只写默认值。

### 追加（Append）
把当前选中单位加入目标编队，保留编队原成员并去重。默认键：左 Ctrl + 数字。

### 覆盖创建（Replace/create）
清空目标编队后用当前选中单位重建。默认键：左 Shift + 数字。

### 排他追加（Exclusive append）
先把选中单位从它所在的所有编队中移除，再加入目标编队，保证一个单位只属于一个编队。默认未绑定键位。

### 踢出（Remove from all groups）
把选中单位从所有编队中移除。默认键 Delete 点按，无需数字；与追加键同时按住则升级为红框批量删除。

### 严格编队选择（strict group selection）
`brf-enabled` 开关覆盖的行为之一：模组启用时拦截原版「指挥模式 + 创建编队 + 数字」和弦；配置了选择前置键后，指挥模式下裸按数字的原生选择也被拦截，保证编队操作只走前缀路径。

## 交互与渲染

### 框选（formation box）
在非指挥模式下按住前置键拖动鼠标的建队方式。普通框选创建进第一个空闲编队；排他框选是它的排他版；红框（追加键 + Delete）清除框内单位的所有编队。框选期间原生的框选输入被捕获，避免误触发原版选择。

### 编队角标（badge）
绘制在成员单位左下角的彩色数字标签，颜色来自模组的 10 色组色板。每帧按 `controlGroups` 重建成员缓存后绘制，仅限己方可指挥单位与视野内。

### 可指挥单位（commandable unit）
`unit.isCommandable()` 为真且属于玩家队伍的存活单位。编队编辑、选择与角标只作用于这类单位；可指挥建筑被显式排除在编队之外。

### 指挥模式（command mode）
Mindustry 原生的单位指挥模式（`InputHandler.commandMode`）。快捷键编队在两种模式下都可用；框选必须在非指挥模式（且开启 `brf-outside-command-mode`）下使用。

## 形态与构建

### bekBundled
主类上的静态布尔标记，表示「当前运行在 Neon 聚合环境中」。Neon 初始化时置 `true` 并注入设置渲染器；为 `true` 时模组跳过自建设置分类。

### BundledSettingsRenderer（设置渲染回调）
`BetterRTSFormationMod` 上的函数式接口，决定聚合设置页如何渲染 brf 的两个开关。默认实现是朴素 `checkPref`；Neon 通过 `configureBundledSettingsRenderer()` 换成 `RbmStyle` 图标开关。独立形态不使用它。

### 独立形态 / 聚合形态
同一份代码的两种运行方式：独立安装时自建设置分类（`ui.settings.addCategory`）；并入 Neon 时由聚合设置页经 `bekBuildSettings()` 渲染。设置 key 与行为完全一致，只差设置入口与外观。

### hidden 模组
`mod.json` 中 `"hidden": true`：Mindustry 跳过其内容加载。本模组不定义任何方块/单位等内容，是纯代码模组的标准声明。

### classes.dex / D8
D8 把 Java 字节码转成安卓可加载的 `classes.dex`；安卓端加载 Java 模组必须包含它。本仓库的 `dexAndroid` 需要两个 `--lib`：`android.jar` 与 `runnable-stub.jar`（为 D8 提供 `java.lang.Runnable` 桩）。详见[版本与发布](release.md)。
