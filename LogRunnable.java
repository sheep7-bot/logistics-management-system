import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
//该类实现 Runnable 接口，属于线程任务类，专门封装异常日志写入业务，交由自定义线程池异步执行，实现日志异步持久化。
public class LogRunnable implements Runnable {
    //定义私有字符串成员变量，用于接收外部传入的异常提示信息，作为日志内容主体。
    private String errorMsg;
    //有参构造方法，作用是初始化成员变量，将主线程捕获到的异常信息传递给当前线程任务类
    public LogRunnable(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public void run() {
        //1.时间格式化
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //2.拼接完整日志内容
        String log = "[" + LocalDateTime.now().format(formatter) + "] 异常信息: " + errorMsg;
        //把错误日志写入 error.txt 文件，并且不会覆盖之前的日志，出错了也不崩溃。
        //自动关闭文件流，不用手动写 bw.close()
        //打开文件，以追加模式写入，不会覆盖原来内容，这个true追加模式打开
        //new BufferedWriter(...):带缓冲区的写入，更快、更高效,为什么用它？比直接用 FileWriter 快很多，适合写日志。
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("Manager/error.txt", true))) {
            //把拼接好的错误日志写入文件
            bw.write(log);
            //换行,让每条错误日志各占一行，看起来整齐。
            bw.newLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}