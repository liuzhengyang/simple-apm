# simple-apm

实现一个简版的arthas。 

javaagent运行时attach到目标进程，通过netty启动一个rpc接口，接收命令，如果是监控方法耗时、参数、返回值的watch命令，
则创建一个ClassFileTransformer, 通过ASM修改对应的类，在方法前后注入代码，调用Instrumentation.retransform方法完成增强。

## Commands

目前实现的方法有

### sc

Search Class

![sc](./docs/images/sc.png)

### watch

观察方法的参数、返回值、耗时
![watch](./docs/images/watch.png)

### el

执行表达式
![el](./docs/images/el.png)

### websocket

支持websocket链接，并且提供了一个默认的websocket页面
![websocket](./docs/images/websocket.png)
