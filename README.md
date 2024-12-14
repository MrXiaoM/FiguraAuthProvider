# FiguraAuthProvider

第三方后端 [sculptor λ](https://github.com/MrXiaoM/sculptor) 的验证提供器，需要配合改版 [FiguraLambda](https://github.com/MrXiaoM/FiguraLambda) 使用。

## 这是什么

这是玩家模型Mod [Figura](https://modrinth.com/mod/figura) 的后端验证提供器，使得离线模式的玩家可在特定的服务器中，连接到自建后端拉取模型。

由于以下原因，你需要使用 [FiguraLambda](https://github.com/MrXiaoM/FiguraLambda) 才可正常使用这套生态。
+ Figura 前端（客户端Mod）在向后端请求验证之前，会先请求一次 Minecraft 正版验证，猜测这么做是为了减轻后端压力
+ Figura 一旦请求验证失败，就不会再重试，除非你手动打开衣柜界面重新连接后端服务器

针对上述问题，我们作了以下修改
+ 由服主自建后端，而不是使用公共的后端，压力不会很大，直接删除正版验证请求
+ 客户端Mod注册一个 CustomPayload 频道，接收插件的消息，收到消息时重新连接后端服务器

## 这个插件如何工作

本插件分为 Bukkit插件 和 代理插件 两个变种。目前所有变种均打包到了同一jar。

> 省流：
> + Bukkit插件 **只需要**装到登录服；如果不是离线模式，是外置登录，随便装到一个服就行
> + 将 代理插件 安装到代理端
> + 在 sculptor 的 authProviders 改为
> ```toml
> authProviders = [
>     { name = "Local", url = "http://127.0.0.1:5009/hasJoined" }
> ]
> ```
> 这样就可以了。

**Bukkit插件** 会开启一个 http服务器，有以下接口可使用
+ `GET /hasJoined` 用于后端 sculptor 的 authProviders
+ `POST /pushPlayerList` 用于接收代理端推送的全服玩家列表
+ (这个http服务器仅用于内网，请勿公开)

后端 sculptor 会向 Bukkit插件 请求，以判定是否验证通过，验证通过时会返回玩家的uuid。
+ 玩家在线时，通过 登录插件提供器 检查验证是否通过，玩家已登录时验证通过
+ 玩家不在线时，通过 全服玩家列表 检查验证是否通过，玩家在此列表时验证通过

在安装了 Vault 以及一个权限插件（比如 LuckPerms），并且权限插件已与 Vault 进行挂钩时，  
如果玩家有 `figura.upload` 权限，那么TA的 `canUpload` 标志为 true。  
但请注意，玩家需要重新连接到后端（重新进入服务器），这个标志才会刷新。

玩家通过登录插件登录成功后，登录验证提供器接收到通知，会向客户端发送一个 CustomPayload 包，提醒客户端应该要重新连接后端服务器了。

目前支持的登录插件如下：
+ [AuthMe](https://www.spigotmc.org/resources/6269)
+ *欢迎提交PR，以支持更多登录插件*

**代理插件** 会在玩家通过代理端连接或断开连接时，向 Bukkit插件 发送当前代理端的所有玩家列表。  
目前支持的代理端如下：（包括其衍生代理端）
+ BungeeCord

## 推荐方案

由于导出的模型不是很大，Figura Mod 也有完善的重连机制，使用 Cloudflare Tunnel 也足够自建后端了，而且还有足够的免费防御。

目前仓库所有者的所有服务部署在主服务器：
+ Waterfall 代理端
+ Purpur 服务端
+ sculptor 后端
+ Syncthing 同步工具

租了几台低配置高带宽的服务器，做低成本游戏盾和负载均衡：
+ hopper-rs 反向代理，连接 Waterfall 代理端
+ Syncthing 同步工具
+ nginx 静态页面，配合 Syncthing 分发服务器资源包

使用 Cloudflare Tunnel 为以下服务做内网穿透：
+ sculptor 后端

## 鸣谢

+ [FiguraMC/Figura](https://github.com/FiguraMC/Figura): 强大的玩家模型修改Mod，这个项目的基础 —— LGPL-2.1 License
+ [shiroyashik/sculptor](https://github.com/shiroyashik/sculptor): 第三方Figura后端，使得自定义验证方式成为可能 —— GPL-3.0 License

## Figura Lambda 生态软件

+ [FiguraLambda](https://github.com/MrXiaoM/FiguraLambda): 客户端Mod
+ [sculptor](https://github.com/MrXiaoM/sculptor): 第三方后端(fork)
+ [FiguraAuthProvider](https://github.com/MrXiaoM/FiguraAuthProvider): 服务端/代理端 玩家验证插件 `<-- 你在这里`
+ [FiguraAvatars](https://github.com/MrXiaoM/FiguraAvatars): 服务端 模型管理插件
