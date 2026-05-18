import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {
    /**业务要求使用线程池处理
     日志任务固定线程数量，节省资源
    程序报错时，丢给线程池去写错误日志
    主线程继续运行，不受日志写入影响*/
    private static final ThreadPoolExecutor pool = new ThreadPoolExecutor(
            3, 6, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(10)
    );

    public static void main(String[] args) {
        // 1. 读取文件订单
        loadOrdersFromFile();
        System.out.println("----------订单数据初始化完成----------");

        Scanner sc = new Scanner(System.in);
        while (true) {
            printMenu();
            System.out.print("请输入操作类型:");
            int choice = sc.nextInt();
            sc.nextLine();

            try {
                switch (choice) {
                    case 1:
                        inputNewOrder(sc);
                        break;
                    case 2:
                        LogisticsManager.deliverOrder();
                        break;
                    case 3:
                        searchByRoute(sc);
                        break;
                    case 4:
                        displayAllOrders();
                        break;
                    case 5:
                        saveOrdersToFile();
                        System.out.println("正在保存数据到orders.txt中...");
                        System.out.println("bye bye~");
                        pool.shutdown();
                        return;
                    default:
                        System.out.println("输入无效，请重新选择");
                }
            }
            //异常不能只打印，还要存日志文件
            //作用：把错误信息交给线程池，异步写入错误日志
            catch (RuntimeException e) {
                System.out.println(e.getMessage());
                pool.execute(new LogRunnable(e.getMessage()));
            }
        }
    }

    // 题目要求的菜单格式
    private static void printMenu() {
        System.out.println("----------物流运输管理系统----------");
        System.out.println("1. 录入新订单");
        System.out.println("2. 执行订单配送（先进先出）");
        System.out.println("3. 按路线查询订单");
        System.out.println("4. 查看所有订单（按录入顺序）");
        System.out.println("5. 退出系统");
        System.out.println("------------------------------------");
    }

    // 从文件加载订单
    private static void loadOrdersFromFile() {
        File file = new File("Manager/orders.txt");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
            //把一行文字按 逗号 切开
                String[] parts = line.split(",");
                String orderId = parts[0];
                String from = parts[1];
                String to = parts[2];
               //Double.parseDouble 把文字 → 转成小数
                double weight = Double.parseDouble(parts[3]);
                //LocalDateTime.parse文字转时间
                LocalDateTime orderTime = LocalDateTime.parse(parts[4], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                // 开始校验订单，防止重复、重量非法
                try {
                    // 校验订单号是否重复（重复会直接报错）
                    TransportOrder.validateOrderId(orderId);

                    // 校验重量是否合法（不能负数、不能为0）
                    TransportOrder.validateWeight(weight);

                    // 校验都通过 → 创建新订单 → 添加到系统订单列表
                    LogisticsManager.addOrder(new TransportOrder(orderId, from, to, weight, orderTime));
                }
                // 如果上面校验失败（比如订单号重复），就会进入这里
                catch (RuntimeException e) {
                    // 打印提示：这个订单号重复了，我跳过它，不加入系统
                    System.out.println("重复订单号跳过: " + orderId);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // 保存订单到文件（退出系统时自动调用）
    private static void saveOrdersToFile() {
        // 打开文件准备写入，自动关闭流，不用手动关
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("Manager/orders.txt"))) {

            // 遍历内存里所有订单，一个一个写进文件
            for (TransportOrder order : LogisticsManager.orderList) {

                // 把订单拼接成 逗号分隔 的一行文本
                String line = order.getOrderId() + ","      // 订单号
                        + order.getFrom() + ","         // 发货地
                        + order.getTo() + ","            // 目的地
                        + order.getWeight() + ","       // 重量
                        // 时间转成字符串格式
                        + order.getOrderTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                bw.write(line);       // 把这一行写入文件
                bw.newLine();         // 换行，下一个订单写下一行
            }
        } catch (IOException e) {
            // 写入失败就打印错误信息
            e.printStackTrace();
        }
    }

    // 录入新订单功能
    private static void inputNewOrder(Scanner sc) {
        // 提示用户按照指定格式输入订单所有信息
        System.out.println("----------请依次输入订单号,发货地,目的地,货物重量,下单时间（用英文逗号隔开）----------");
        // 读取用户输入的一整行数据
        String line = sc.nextLine();
        // 以英文逗号为分隔符，把整行数据拆分成数组
        String[] parts = line.split(",");

        // 依次取出拆分后的各项数据
        String orderId = parts[0];        // 获取订单号
        String from = parts[1];            // 获取发货地
        String to = parts[2];              // 获取目的地
        // 把字符串类型重量转为小数类型
        double weight = Double.parseDouble(parts[3]);
        // 把字符串时间转为程序可识别的时间对象
        LocalDateTime orderTime = LocalDateTime.parse(parts[4], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // 校验订单号是否重复
        TransportOrder.validateOrderId(orderId);
        // 校验货物重量是否合法
        TransportOrder.validateWeight(weight);
        // 创建订单对象，添加到订单集合中
        LogisticsManager.addOrder(new TransportOrder(orderId, from, to, weight, orderTime));
        // 控制台输出录入成功提示，展示当前录入的订单信息
        System.out.println("录入成功！订单号:" + orderId + "| 路线:" + from + "→" + to + " | 重量:" + weight + " | 下单时间: " + orderTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    // 根据发货地和目的地查询订单
    private static void searchByRoute(Scanner sc) {
        // 提示用户输入查询的起止地点
        System.out.println("----------请输入发货地,目的地（用英文逗号隔开）----------");
        // 读取用户输入内容
        String line = sc.nextLine();
        // 拆分出发货地与目的地
        String[] parts = line.split(",");
        // 调用管理类方法，执行路线查询逻辑
        LogisticsManager.searchOrderByPlace(parts[0], parts[1]);
    }

    // 查看系统中所有订单信息
    private static void displayAllOrders() {
        // 判断订单集合是否为空，没有订单直接提示并结束方法
        if (LogisticsManager.orderList.isEmpty()) {
            System.out.println("暂无订单记录");
            return;
        }
        // 遍历全部订单数据
        for (TransportOrder order : LogisticsManager.orderList) {
            System.out.println("============");
            System.out.println("订单号: " + order.getOrderId());     // 打印订单编号
            System.out.println("发货地: " + order.getFrom());       // 打印发货地点
            System.out.println("目的地: " + order.getTo());         // 打印目的地点
            System.out.println("货物重量: " + order.getWeight() + "吨"); // 打印货物重量
            // 格式化时间并打印下单时间
            System.out.println("下单时间: " + order.getOrderTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            System.out.println("============");
        }
    }
}