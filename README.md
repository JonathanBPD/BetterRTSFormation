# Better RTS Formation

更方便地管理 Mindustry 中的 RTS 编队。

为单位创建、选择多个编队，并通过清晰的数字角标快速识别单位归属。一个单位可以同时属于多个编队，也可以在非指挥模式下操作编队。

![编队角标示意图](assets/formation-badges.png)

当前版本：2.0.0

## 功能

- 追加前置键 + 数字：保留目标编队原有成员，追加当前选中单位并去重。
- 覆盖创建前置键 + 数字：清空目标编队，用当前选中单位重新创建。
- 排他追加前置键 + 数字：先将当前选中单位移出所有编队，再追加到目标编队。
- 选择前置键 + 数字：选择目标编队。
- 点按「移出所有编队」：立即将当前选中单位移出所有编队，不需要数字键。
- 主键盘与小键盘的 `1-9` / `0` 均可使用；已处理的编队组合会被模组消费，不会同时触发建筑栏或原生编队操作。
- 模组启用时，原生「指挥模式 + 创建编队 + 数字」组合会被阻止；关闭模组后恢复原生行为。
- 框选创建：开启「非指挥模式也能控制编队」后，按住追加前置键并拖动，为框内单位创建第一个空闲编队。
- 排他框选创建：按住排他追加前置键并拖动，先将框内单位移出所有编队，再放入第一个空闲编队。
- 框选删除：同时按住追加前置键与移出键并拖动（框以红色显示），移除框内单位的全部编队。
- 没有空闲编队时，框选创建不执行任何操作。
- 所有编队编辑只处理单位，不影响可指挥建筑。
- 设置：可开关「编队角标与严格编队选择」「非指挥模式下也能控制编队」。

## 使用

在控制设置 → Better RTS Formation 中可以修改五个功能键。点击「恢复默认」后的默认值为：

- 追加 / 框选创建：`左 Ctrl`
- 覆盖创建编队：`左 Shift`
- 排他追加 / 排他框选创建：未设置
- 选择编队：`左 Alt`
- 将选中单位移出所有编队：`Delete`

已保存的自定义键位不会被模组强制覆盖。数字动作按「排他追加 → 覆盖创建 → 阻止原生创建 → 追加 → 选择」的优先级判定。

模组支持原版 Mindustry 与 MindustryX，桌面端与 Android 端均可运行。

## 构建与安装

- 构建：运行 `./gradlew deploy`，产物为 `BetterRTSFormation-2.0.0.jar`（内含 `mod.json` 与 `classes.dex`），输出到 `build/libs/` 与 `dist/`。
- 发布副本：`构建/BetterRTSFormation/BetterRTSFormation-v2.0.0.jar`（仓库外的工作区暂存目录）。
- 安装：将 jar 放入 Mindustry 的 `mods` 文件夹，然后启动游戏。

---

Create, select, and recognize RTS control groups more easily in Mindustry.

Units can belong to multiple groups, with clear numbered badges for quick identification. Groups can also be managed outside command mode.

Current version: 2.0.0

## Features

- Append prefix + number: preserve the target group, append the selected units, and deduplicate them.
- Replace/create prefix + number: clear the target group and replace it with the selected units.
- Exclusive-append prefix + number: remove the selected units from every group, then append them to the target group.
- Select prefix + number: select the target group.
- Tap Remove from all groups: immediately remove the selected units from every group; no number is required.
- Both the number row and numpad `1-9` / `0` are supported. Handled group shortcuts are consumed so they do not also trigger the build hotbar or native group handling.
- While the mod is enabled, the native command-mode + create-group + number chord is suppressed. Disabling the mod restores native behavior.
- Box create: enable control groups outside command mode, then hold Append and drag to place the enclosed units in the first empty group.
- Exclusive box create: hold Exclusive append and drag to remove the enclosed units from every group, then place them in the first empty group.
- Box remove: hold Append and Remove together and drag a red box to strip all groups from the enclosed units.
- Box creation does nothing when no empty group is available.
- Group edits affect units only; commandable buildings are left unchanged.
- Settings: toggle "Enable formation badges and strict group selection" and "Allow control groups outside command mode".

## Usage

The five actions are configurable under Control → Better RTS Formation. Restore Default assigns:

- Append / box create: `Left Ctrl`
- Replace/create group: `Left Shift`
- Exclusive append / exclusive box create: Unset
- Select group: `Left Alt`
- Remove selected units from all groups: `Delete`

Saved custom bindings are not forcibly overwritten. Number actions use this priority: Exclusive append → Replace/create → suppress native creation → Append → Select.

Works with vanilla Mindustry and MindustryX, on desktop and Android.

## Build and install

- Build: run `./gradlew deploy` to produce `BetterRTSFormation-2.0.0.jar` (containing `mod.json` and `classes.dex`) in `build/libs/` and `dist/`.
- Release copy: `构建/BetterRTSFormation/BetterRTSFormation-v2.0.0.jar` (workspace staging dir outside the repo).
- Install: put the jar into Mindustry's `mods` folder and launch the game.
