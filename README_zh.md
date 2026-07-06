# Ninjabrain-Fabricated

Minecraft 26.2 (1.21+) 的 Fabric 模组，将 [Ninjabrain Bot](https://github.com/Ninjabrain1/Ninjabrain-Bot) 的末地要塞三角定位算法移植为**全自动**客户端模组。使用贝叶斯推断从末影之眼抛投预测要塞位置——无需手动输入角度。

## 环境要求

- Minecraft 26.2
- Fabric Loader >=0.19.3
- Fabric API
- Cloth Config >=26.2

可选：
- Mod Menu（配置界面）

## 安装

1. 为 Minecraft 26.2 安装 Fabric Loader
2. 下载 Fabric API 和 Cloth Config，放入 `mods` 文件夹
3. 将 `ninjabrain-fabricated.jar` 放入 `mods` 文件夹
4. （可选）安装 Mod Menu 以获得配置界面
5. 启动游戏

## 使用方法 — 全自动

1. 扔出末影之眼
2. 走到末影之眼落点处
3. 等待末影之眼消失——抛投数据 **自动** 记录
4. 至少记录 2 次抛投（建议相距 100 格以上）即可获得三角定位结果

记录第一条数据后，覆盖层会自动显示在屏幕左上角。

## 覆盖层显示

记录抛投后自动显示，内容包含：
- 已记录的抛投次数
- 预测要塞区块（排名、8x8 坐标、置信度、距离）
- 最佳预测的 4x4 区块坐标

## 配置

- 安装了 Mod Menu 时：**Mods → Ninjabrain-Fabricated → Config**
- 或手动编辑 `config/ninjabrain-fabricated.json`

设置项：
- **Standard Deviation** — 角度测量不确定度（默认 0.05）
- **Alt Standard Deviation** — 备用标准差（默认 0.10）
- **Crosshair Correction** — 准星偏移校准（默认 0.0）
- **Number of Predictions** — 显示预测数量（默认 5）
- **Advanced Statistics** — 启用最近要塞校正（默认关闭）

## 快捷键

| 按键 | 功能 | 说明 |
|------|------|------|
| **R** | 重置 | 清除所有抛投和化石数据 |
| **Z** | 撤销 | 删除最后一次抛投 |
| **L** | 锁定/解锁 | 锁定测量结果，防止误操作 |
| **↑（上箭头）** | 微调增加 | 对最后一次抛投增加修正值（+0.01°，+1 增量） |
| **↓（下箭头）** | 微调减少 | 对最后一次抛投减少修正值（-0.01°，-1 增量） |

可在 **选项 → 控制 → Ninjabrain** 中自定义修改。

## 功能特性

- **全自动**末影之眼抛投检测——捕获玩家位置，从珍珠飞行轨迹推算角度
- 基于 2 次以上抛投的贝叶斯三角定位
- 按概率排序的预测排名
- 显示 4x4 和 8x8 区块坐标
- 抛投撤销、重置和手动角度修正
- 测量数据锁定
- 纯客户端模组，服务器无需安装
- 仅依赖 Fabric API 和 Cloth Config

## 算法说明

覆盖全部 8 个环的 128,672 个可能要塞位置的贝叶斯推断，每次抛投后更新后验概率分布。

## 许可证

GNU General Public License v3.0

---

> **免责声明**：本项目部分代码与文档由 AI 协助生成，可能存在不准确或不完整之处。请在使用前仔细核对相关内容，并根据实际情况进行调整和完善。
