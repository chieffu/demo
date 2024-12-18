# API接口文档

## 概述

本接口文档描述了提供的RESTful API，用于与Blackjack游戏相关资源进行交互。所有接口均基于HTTP协议，并通过`/api/bj`路径前缀访问。

- 请求数据：使用JSON格式（Content-Type: application/json）。
- 响应数据：使用JSON格式（Content-Type: application/json），状态码遵循HTTP标准。

## API列表

### 1. 获取Blackjack玩法类型列表

#### GET /api/bj/types

**说明**: 返回Blackjack游戏支持的所有玩法类型的列表。

**响应示例**:

```json
[
  "LUCKY_QUEEN",
  "HOT_THREE",
  "LUCKY_THREE",
  "PAIR",
  "BASE_PLAY",
  "BLOOM"
]
```

### 2. 获取牌桌指定轮次游戏详情

#### GET /api/bj/table/{tableId}/{roundId}

**说明**: 根据给定的`tableId`和`roundId`，获取该牌桌对应轮次的庄闲牌情况详细信息。

**路径参数**:
- `tableId`: 牌桌ID（字符串）
- `roundId`: 轮次ID（字符串）

**响应示例**:

```json
{
   "roundId":"2342000234",
   "tableId":"78454235445",
   "banker":["Q♣", "3❤"],
   "player":[["A♠", "2♦", "A♣", "3❤"],["A♠", "2♦", "3♣", "6❤"]]
}
```

### 3. 查询当前各种下注的数学期望

#### GET /api/bj/table/{tableId}/odds

**说明**: 根据给定的`tableId`，查询当前牌桌上各玩法类型的赢的概率。

**路径参数**:
- `tableId`: 牌桌ID（字符串）

**响应示例**:

```json
{
  "BASE_PLAY": "0.9889",
  "BLOOM": "0.6563",
  "HOT_THREE": "0.9483",
  "LUCKY_QUEEN": "1.0669",
  "LUCKY_THREE": "0.9630",
  "PAIR": "0.9590"
}
```

### 4. 移除牌桌上的牌

#### PUT /api/bj/table/{tableId}/remove-cards

**说明**: 根据给定的`tableId`，从牌桌上移除指定的牌（由请求体中的`cards`列表指定）。
牌值是一个三位数的数字，第一位表示花色 1:黑桃  2:红桃 3:梅花 4:方块 后面2位数表示点数
1-13分别表示A,2,3,4,5,6,7,8,9,10,J,Q,K
例如 101 表示黑桃A 413表示方块K  312表示梅花Q 208表示红桃8
返回剩余牌堆中每张牌的数量

**路径参数**:
- `tableId`: 牌桌ID（字符串,全局唯一）
  
**请求体**:
```json
{
  "cards": [101, 204, 101, 402] // 一串整数列表，表示待移除的牌
}
```

**响应示例**:

```json
{
  "Q♣": 4,
  "10♦": 0,
  ...
}
```

### 5. 更新牌桌上的牌

#### PUT /api/bj/table/{tableId}/update-cards

**说明**: 根据给定的`tableId`，更新某一轮游戏牌的数据，会比较牌堆，顺序不能错，如果错了会报错。
例如上次庄上报了 [103] ，这次有一张牌明牌了，就接着上报[103,206],会核实第1张牌是不是103，不是就会抛异常

**路径参数**:
- `tableId`: 牌桌ID（字符串）

**请求体**:
```json
{
  "roundId": "211234123",
  "banker": [103, 208, 313],
  "players": [[401, 205, 106], [307,208, 309]]
}
```

**响应示例**:

```json
{
  "Q♣": 4,
  "10♦": 0,
  ...
}
```

### 6. 计算再加一次牌后获胜的概率

#### POST /api/bj/table/{tableId}/win-rate-one-more

**说明**: 根据给定的`tableId`和`Play`对象，计算玩家在当前手牌基础上再加一张牌后的获胜概率。

**路径参数**:
- `tableId`: 牌桌ID（字符串）

**请求体**:
```json
{
  "player": [201, 302, 203],
  "banker": [104]
}
```

**响应示例**:

```json
0.7890
```

### 7. 计算当前获胜概率

#### POST /api/bj/table/{tableId}/win-rate

**说明**: 根据给定的`tableId`和`Play`对象，计算玩家当前手牌的获胜概率。

**路径参数**:
- `tableId`: 牌桌ID（字符串）

**请求体**:
```json
{
  "player": [105, 401],
  "banker": [406]
}
```

**响应示例**:

```json
0.5623
```