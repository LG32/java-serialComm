
import java.io.IOException;
import java.io.OutputStream;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

public class TestRxtx {

    public static final byte[] init = new byte[] { 0x1B, 0x40 };
    public static final byte[] clean = new byte[] { 0x0C };
    public static final byte[] pre_display = new byte[] { 0x1B, 0x51, 0x41 };
    public static final byte[] post_display = new byte[] { 0x0D };

    public static void displayCustomerScreen(String data, byte[] mode) {
        try {
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier("COM1"); //串口号
            SerialPort serialPort = (SerialPort) portIdentifier.open("test",5000); //使用者  和 最大响应时长(ms)
            serialPort.setSerialPortParams(115200,  //波特率
                    SerialPort.DATABITS_8, 			//校验位
                    SerialPort.STOPBITS_1, 			//数据位
                    SerialPort.PARITY_NONE);		//停止位
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
            try {
                OutputStream outputStream = serialPort.getOutputStream();
                if (mode != null) {
                    outputStream.write(mode);
                }
                if (data != null) {
                    outputStream.write(pre_display);
                    outputStream.write(data.getBytes());
                    outputStream.write(post_display);
                }
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                serialPort.close();
            }
        } catch (NoSuchPortException | UnsupportedCommOperationException | PortInUseException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        displayCustomerScreen(null,"abffff0000010d0a".getBytes());
    }
}
