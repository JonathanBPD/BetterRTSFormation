# Better RTS Formation

> 让单位编队在数量变多后依然清楚、好管。

Better RTS Formation 改善 Mindustry 的 RTS 编队工作流。它让编队更容易建立、选择和识别，适合需要同时管理多支部队、频繁切换指挥组的玩家。

编队的目标不是增加新的战斗机制，而是让你始终知道当前选中了谁、下一条命令会交给谁。它可以在原版客户端和多人游戏中作为本地辅助使用。

当前版本：2.0.0。

## 使用

启用模组后，在游戏控制设置中查看编队相关按键。使用前置键配合数字即可创建或追加编队；相近的同编队单位会合并显示一个适度放大的编号，被 Mega 等载具搬运并释放后仍保留原编队归属。

## 构建与安装

~~~powershell
.\gradlew.bat deploy
~~~

将生成的模组 JAR 放入 Mindustry 的 mods 文件夹，然后启动游戏。

## English

> Keep RTS groups clear and manageable as your army grows.

Better RTS Formation improves the RTS control-group workflow in Mindustry. It makes groups easier to create, select, and recognize for players who manage several forces or switch command groups frequently.

The goal is not to add a new combat system, but to make it obvious which units are selected and where the next order will go. It works as a local client aid in vanilla multiplayer environments.

Current version: 2.0.0.

## Usage

Enable the mod and review the formation controls in the game settings. Use the formation prefix key with a number to create or add to a group; nearby units in the same group share one slightly larger marker, and units carried by Mega or another payload carrier keep their formation after release.

## Build and install

~~~powershell
.\gradlew.bat deploy
~~~

Put the generated mod JAR in Mindustry's mods directory and launch the game.

## 文档

更多说明见 [docs/README.md](docs/README.md)：架构总览、开发指南、版本与发布、测试指南与术语表。
