import java.util.LinkedList;
import java.util.stream.Collectors;

public class LogisticsManager {


      public   static LinkedList<TransportOrder> orderList = new LinkedList<>();


    /**
     * 添加订单
     * 校验订单号不能重复，重复抛出异常
     * @param newOrder 要新增的订单对象
     */
    public static void addOrder(TransportOrder newOrder) {
        // 遍历已有所有订单
        for (TransportOrder existOrder : orderList) {
            // 判断订单号是否相同
            if (existOrder.getOrderId().equals(newOrder.getOrderId())) {
                throw new RuntimeException("订单号重复，无法添加");
            }
        }
        // 无重复则添加订单
        orderList.add(newOrder);
    }


    /**
     * 配送订单
     * 从头部取出订单，遵循先进先出
     */
    public static void deliverOrder() {
        // 判断集合是否为空
        if (orderList.isEmpty()) {
            throw new RuntimeException("暂时没有待配送订单");
        }
        // 取出最先存入的订单
        // orderList.removeFirst()
        //作用：删除列表中【第一个元素】，并且把这个被删除的元素返回出来
        TransportOrder sendOrder = orderList.removeFirst();
        System.out.println("订单" + sendOrder.getOrderId() + "已完成配送");
    }

    /**
     * 根据起止地点查询订单
     * 查询后按货物重量从小到大排序
     * @param startPlace （from）发货地
     * @param endPlace  （to）目的地
     */
    public static void searchOrderByPlace(String startPlace, String endPlace) {
        //从订单集合开启流式操作，后面一般拼接：过滤、遍历、拼接字符串，最终拼成一整条订单信息字符串。
        //把集合转成 Java 流式流，用来简洁遍历、筛选、拼接、处理数据
        //普通遍历：写 for 循环一个个拿
        //stream 流：一行链式代码搞定数据处理
        String orderInfo = orderList.stream()
                // 筛选对应路线订单
                .filter(order -> order.getFrom().equals(startPlace) && order.getTo().equals(endPlace))
                // 按重量升序排序(降序就调换一下比较位置Double.compare(order2.getWeight(), order1.getWeight())
                .sorted((order1, order2) -> Double.compare(order1.getWeight(), order2.getWeight()))
                // 获取订单完整信息
                //map:转换（把订单对象 → 订单文字信息）
                //类名::实例方法（方法引用）    等价于order -> order.getOrderInfo()
                .map(TransportOrder::getOrderInfo)
                // 换行拼接所有信息，.collect()：终止流，开始收集数据，Collectors.joining("\n")：收集方式为换行拼接
                .collect(Collectors.joining("\n"));

        // 判断是否查询到数据
        if (orderInfo.isBlank()) {
            System.out.println("暂无该路线相关订单");
        } else {
            System.out.println(orderInfo);
        }
    }

    /**
     * 展示系统中所有订单信息
     */
    public static void displayAllOrder() {
        if (orderList.isEmpty()) {
            System.out.println("当前系统暂无任何订单");
            return;
        }
        // 遍历打印所有订单
        for (TransportOrder order : orderList) {
            System.out.println(order.getOrderInfo());
        }
    }

}
