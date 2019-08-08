import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class JSSCTest {

    private SerialPort serialPortLed;

    private void checkLed() throws SerialPortException {
        if (serialPortLed == null) {
            serialPortLed = new SerialPort("COM1"); //输入你的串口号
        }
        if (!serialPortLed.isOpened()) {
            serialPortLed.openPort();
            serialPortLed.addEventListener(listenr);
        }
        serialPortLed.setParams(115200, 0, 8, 1);
    }

    private void openLed() {
        try {
            checkLed();
            sendPortData(serialPortLed, "这里是我的命令", true);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }


    /**
     *发送串口命令
     */
    private void sendPortData(SerialPort ComPort, String data, boolean isHex) throws SerialPortException {
        if (ComPort != null && ComPort.isOpened()) {
            //是否16进制的命令，如果是，走上面，如果是字符型的命令，直接走下面
            if (isHex) {
//                ComPort.writeBytes(MyFunc.HexToByteArr(data));
            } else {
                ComPort.writeString(data);
            }
        }
    }

    private SerialPortEventListener listenr = new SerialPortEventListener() {
        @Override
        public void serialEvent(SerialPortEvent serialPortEvent) {
            if (serialPortEvent.isRXCHAR()) {
                try {
                    if (serialPortEvent.getEventValue() > 0) {
                        System.out.println("come into return");
//                        System.out.println("serialPortEvent.getEventType():" + serialPortEvent.getEventType());
//                        System.out.println("serialPortEvent.getPortName():" + serialPortEvent.getPortName());
//                        System.out.println("serialPortEvent.getEventValue():" + serialPortEvent.getEventValue());
                        //以16进制的方式读取串口返回数据
                        System.out.println("read form serial:" + serialPortEvent.getPortName() + "" + serialPortLed.readHexString(serialPortEvent.getEventValue()));
                    }

                    Thread.sleep(250);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    };

    public static void main(String[] args) throws SerialPortException {
        JSSCTest jsscTest = new JSSCTest();
        jsscTest.checkLed();
    }
}
