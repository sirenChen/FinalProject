package Message;

/**
 * Message process
 */
public class MeterMessageProcess {

    public MeterMessageProcess(Message message) {
        if (message == null) {
            System.out.println("Message process: NULL Message Object \n");
        } else {
            message.messageProcess();
        }
    }
}
