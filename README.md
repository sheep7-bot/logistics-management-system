# 物流运输管理系统

Java SE 写的控制台版订单管理小程序，练手项目。实现了订单录入（订单号去重、重量校验）、先进先出配送、按路线查询并按重量排序，数据存到 orders.txt 里做持久化。异常日志用自定义线程池异步写入 error.txt，不阻塞主流程。

技术上是 Java SE 那一套：IO 流、线程池、Stream、LocalDateTime、LinkedList。
