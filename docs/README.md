# Better RTS Formation 文档

Better RTS Formation（Neon 聚合模组中的 id：brf）的分类文档。文档以中文为主，功能名保留英文本名，便于与代码和设置界面对照。

## 从哪里开始

| 你是 | 从这里开始 |
| --- | --- |
| 玩家 | [README](../README.md)（中英双语使用说明就在主页） |
| 想了解模组怎么实现的人 | [架构总览](architecture.md) |
| 想改代码 | [开发指南](development.md) 与[架构总览](architecture.md) |
| 准备发版 | [版本与发布](release.md) |
| 改完想验证 | [测试指南](testing.md) |
| 遇到不认识的词 | [术语表](glossary.md) |

## 文档地图

```text
docs/
|-- README.md        本页：文档导航
|-- architecture.md  架构总览：入口、生命周期、编队数据流、设置系统、Neon 聚合形态
|-- development.md   开发指南：环境、构建命令、代码风格要点
|-- release.md       版本与发布：版本号、产物链、CI、Release 资产安全
|-- testing.md       测试指南：手测清单（按功能）
`-- glossary.md      术语表
```

## 相关文件

- [README.md](../README.md)：项目主页。
- [AGENTS.md](../AGENTS.md)：仓库维护约束（代码契约、验证清单、提交规范）。
- `mod.json`：模组描述（入口、版本、`minGameVersion`）。
- `src/main/resources/bundles/bundle*.properties`：用户可见文案（en / zh_CN / zh_TW）。

## 维护约定

- 本仓库同时以独立模组与 Neon 聚合子模组两种形态运行；涉及两种形态的内容必须写明差异，见[架构总览](architecture.md)的「双形态与 Neon 聚合契约」。
- 默认键位可能被玩家自定义；文档只写默认值，并注明在 `设置 → 控制` 可改。
- 用户可见文案一律走 bundle key；新增文案需同步维护 en / zh_CN / zh_TW 三份。
