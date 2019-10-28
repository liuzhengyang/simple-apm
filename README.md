# simple-apm

实现一个简版的arthas。 

javaagent运行时attach到目标进程，通过netty启动一个rpc接口，接收命令，如果是监控方法耗时、参数、返回值的watch命令，
则创建一个ClassFileTransformer, 通过ASM修改对应的类，在方法前后注入代码，调用Instrumentation.retransform方法完成增强。
