# 运途

运途是一个 Java SE 写的控制台版物流订单管理程序，练手项目。管理运输订单的完整流程：录入订单（订单号去重、重量校验）、按先进先出顺序配送、按路线查询并按重量排序，数据持久化到 orders.txt。异常日志由自定义线程池异步写入 error.txt，不阻塞主流程。

技术上用到了 IO 流、线程池、Stream、LocalDateTime、LinkedList。

## 怎么跑

```
javac *.java
java Main
```
