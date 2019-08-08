import com.fazecast.jSerialComm.SerialPort;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class JserialConnTest {
    public enum DataBits {
        Eight(8);

        public final int mask;

        DataBits(int mask) {
            this.mask = mask;
        }
    }

    public enum StopBits {
        One(SerialPort.ONE_STOP_BIT);

        public final int mask;

        StopBits(int mask) {
            this.mask = mask;
        }
    }

    public enum FlowControl {
        Off(SerialPort.FLOW_CONTROL_DISABLED);

        public final int mask;

        FlowControl(int mask) {
            this.mask = mask;
        }
    }

    public enum Parity {
        None(SerialPort.NO_PARITY);

        public final int mask;

        Parity(int mask) {
            this.mask = mask;
        }
    }

    private String portName = "COM1";

    private int baud = 115200;

    private FlowControl flowControl = FlowControl.Off;

    private DataBits dataBits = DataBits.Eight;

    private StopBits stopBits = StopBits.One;

    private Parity parity = Parity.None;

    private boolean setDtr = false;

    private boolean setRts = false;

    protected String name = "SerialPortCommunications";


    private SerialPort serialPort;

    public synchronized void connect() throws Exception {
        disconnect();
        serialPort = SerialPort.getCommPort(portName);
        serialPort.openPort(0);
        serialPort.setComPortParameters(baud, dataBits.mask, stopBits.mask, parity.mask);
        serialPort.setFlowControl(flowControl.mask);
        if (setDtr) {
            serialPort.setDTR();
        }
        if (setRts) {
            serialPort.setRTS();
        }
        serialPort.setComPortTimeouts(
                SerialPort.TIMEOUT_READ_SEMI_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, 5000, 0);
    }

    public synchronized void disconnect() throws Exception {
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.closePort();
            serialPort = null;
        }
    }


    /**
     * Returns an array of Strings containing the names of serial ports
     * present on the system
     *
     * @return array of Strings of serial port names
     */
    public static String[] getPortNames() {
        SerialPort[] ports = SerialPort.getCommPorts();
        ArrayList<String> portNames = new ArrayList<>();
        for (SerialPort port : ports) {
            portNames.add(port.getSystemPortName());
        }
        return portNames.toArray(new String[] {});
    }

    /**
     * Read a line from the serial port. Blocks for the default timeout. If the read times out a
     * TimeoutException is thrown. Any other failure to read results in an IOExeption;
     *
     * @throws TimeoutException
     * @throws IOException
     */
    public void readLine() throws TimeoutException, IOException {
        StringBuffer line = new StringBuffer();
        while (true) {
            byte ch = read();
            if (ch == '\n' || ch == '\r') {
                if (line.length() > 0) {
                    return;
                }
            }
            else {
                line.append((char) ch);
                System.out.println(Integer.toHexString(ch));
            }
        }
    }

    public void writeLine(String data) throws IOException
    {
        byte[] b = data.getBytes();
        int l = serialPort.writeBytes(b, b.length);
        if (l == -1) {
            throw new IOException("Write error.");
        }
//        b = getLineEndingType().getLineEnding().getBytes();
        l = serialPort.writeBytes(b, b.length);
        if (l == -1) {
            throw new IOException("Write error.");
        }
    }

    public byte read() throws TimeoutException, IOException {
        byte[] b = new byte[1];
        int l = serialPort.readBytes(b, 1);
        if (l == -1) {
            throw new IOException("Read error.");
        }
        if (l == 0) {
            throw new TimeoutException("Read timeout.");
        }
        return b[0];
    }

    public void write(int d) throws IOException {
        byte[] b = new byte[] { (byte) d };
        int l = serialPort.writeBytes(b, 1);
        if (l == -1) {
            throw new IOException("Write error.");
        }
    }

    public String getConnectionName() {
        return "serial://" + portName;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public int getBaud() {
        return baud;
    }

    public void setBaud(int baud) {
        this.baud = baud;
    }

    public FlowControl getFlowControl() {
        return flowControl;
    }

    public void setFlowControl(FlowControl flowControl) {
        this.flowControl = flowControl;
    }

    public DataBits getDataBits() {
        return dataBits;
    }

    public void setDataBits(DataBits dataBits) {
        this.dataBits = dataBits;
    }

    public StopBits getStopBits() {
        return stopBits;
    }

    public void setStopBits(StopBits stopBits) {
        this.stopBits = stopBits;
    }

    public Parity getParity() {
        return parity;
    }

    public void setParity(Parity parity) {
        this.parity = parity;
    }

    public boolean isSetDtr() {
        return setDtr;
    }

    public void setSetDtr(boolean setDtr) {
        this.setDtr = setDtr;
    }

    public boolean isSetRts() {
        return setRts;
    }

    public void setSetRts(boolean setRts) {
        this.setRts = setRts;
    }

    public static void main(String[] args) {
        JserialConnTest jserialConnTest = new JserialConnTest();
        try {
            jserialConnTest.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                jserialConnTest.readLine();
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
