# 版本与发布

Better RTS Formation 的发版规则与产物链。原则：**版本号与构建在本地完成，CI 只在 `v*` tag 推送后构建并创建 GitHub Release**。

## 版本号体系

- 语义化字符串版本（当前 `2.0.0`），需要同步改两处：`mod.json` 的 `version` 与 `build.gradle` 的 `project.version`（合并 jar 文件名取自它）。
- tag 只用 `v*` 形态（`v1.0.2`、`v2.0.0`），并与 `.github/release-notes/` 下的说明文件名一一对应。

## 发布前本地必做步骤

1. 改 `mod.json` 与 `build.gradle` 两处版本号。
2. 新建 `.github/release-notes/vX.Y.Z.md`（中英双语条目，格式参照现有文件）。
3. 本地完整构建并核对产物：
   ```powershell
   .\gradlew.bat clean deploy
   ```
   - `dist/BetterRTSFormation-<version>.jar`：确认存在、含 `mod.json` 与 `classes.dex`。
   - `../构建/BetterRTSFormation/BetterRTSFormation-dev.jar`：工作区暂存副本已刷新。
4. 提交版本号与 release notes，打 `vX.Y.Z` tag 并推送。

## 产物链

```text
classes ──► d8InputJar ──► dexAndroid ──► jarMerged ──┬─► copyMergedJarToDist ─► dist/（发布交付）
             (classes)     (d8 --min-api 14          │
                            --release,               └─► copyDevArtifact ─► ../构建/BetterRTSFormation/
                            --lib android.jar                              BetterRTSFormation-dev.jar
                            --lib runnable-stub.jar)
```

要点：

- `dexAndroid` 的两个 `--lib` 缺一不可：`android.jar` 保证安卓 API 解析；`runnable-stub.jar`（由 `tmp-stubs/java/lang/Runnable.java` 经 `compileRunnableStub` → `jarRunnableStub` 编译）为 D8 的库解析提供 `java.lang.Runnable`。编译类路径（Mindustry core）会逐个以 `--classpath` 传入。
- `jarMerged` 是唯一可分发形态：classes + 资源 + `mod.json` + `classes.dex`，输出到 `build/libs/`。
- `jarDesktop` / `jarAndroid` 的产物是开发中间产物，只存在于 build 目录。
- `dist/` 在 `.gitignore` 里，不入库；CI 构建前会 `rm -rf dist` 重建，保证其中只有当次版本一个 jar。

## CI（`.github/workflows/release.yml`）

触发：推送 `v*` tag 或手动 dispatch。流程：

1. Temurin 17 + Gradle + Android SDK（`platforms;android-34`、`build-tools;34.0.0`）。
2. `clean deploy` 构建合并产物。
3. 校验 `dist/*.jar` 内含 `mod.json` / `mod.hjson` 描述文件，缺失即失败。
4. `softprops/action-gh-release` 上传 `dist/*.jar` 创建 Release，正文取 `.github/release-notes/<tag>.md`；手动 dispatch 只构建不发版。

## Release 资产安全规则

Mindustry 游戏内安装器会取 Release API 返回的**第一个 `.jar`**，且不按操作系统挑资产。因此：

- 一个 Release 必须有且只有一个 `.jar` 资产，即 `dist/BetterRTSFormation-<version>.jar`（含 `mod.json` + 桌面主类 + `classes.dex`）。
- 严禁把 `BetterRTSFormation-desktop.jar`、`BetterRTSFormation-android.jar`、`*-d8-input.jar` 等中间产物传上 Release（CI 的 `rm -rf dist` 保证了 dist 里只有当次合并 jar，不要绕过）。
- 发布后用 API 复查：
  ```bash
  gh release view <tag> --json assets
  ```
  发现多余 `.jar` 立即删除。
