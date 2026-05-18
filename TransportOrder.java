import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class TransportOrder {
    private String orderId;
    private String from;
    private String to;
    private double weight;
    private LocalDateTime orderTime;

    public TransportOrder() {
    }

    public TransportOrder(String orderId, String from, String to, double wight, LocalDateTime orderTime) {
        this.orderId = orderId;
        this.from = from;
        this.to = to;
        this.weight = wight;
        this.orderTime = orderTime;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double wight) {
        this.weight = wight;
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(LocalDateTime orderTime) {
        this.orderTime = orderTime;
    }

    @Override
    public String toString() {
        return "TransportOrder{" +
                "orderId='" + orderId + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", wight=" + weight +
                ", orderTime=" + orderTime +
                '}';
    }

    /**
     * 2. 信息获取方法：
     * 定义 `public String getOrderInfo()` 方法，返回订单号、发货地、目的地、货物重量、下单时间的拼接字符串
     * （格式：订单号:XXX | 路线:XXX→XXX | 重量:XXXX | 下单时间: yyyy-MM-dd HH:mm:ss）(4分)。
     */

    public String getOrderInfo() {

        return "订单号:" + orderId +
                " | 路线:" + from + "→" + to +
                " | 重量:" + weight +
                " | 下单时间: " +
                orderTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }


    // 1：校验重量的静态方法
    public static void validateWeight(Double weight) {
        if (weight == null || weight <= 0) {
            throw new RuntimeException("输入的重量不合规");
        }
    }

    // 2：校验订单号的静态方法
    public static void validateOrderId(String orderId) {
        if (orderId == null || !orderId.startsWith("ORD")) {
            throw new RuntimeException("订单号必须以ORD开头!");
        }
    }
}


