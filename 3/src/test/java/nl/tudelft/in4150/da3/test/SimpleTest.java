package nl.tudelft.in4150.da3.test;

import nl.tudelft.in4150.da3.DA_Byzantine_RMI;
import nl.tudelft.in4150.da3.Order;
import nl.tudelft.in4150.da3.message.OrderMessage;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SimpleTest{
    
    private TestSetup setup;

    //private final static Log LOGGER = LogFactory.getLog(SimpleTest.class);

    @Before
    public void init(){
        setup = new TestSetup();
        setup.init();
    }

    @Test
    public void testSimple(){
        int numProcesses = 4;
        DA_Byzantine_RMI commanderProcess = setup.getProcesses().get(0);
        DA_Byzantine_RMI lieutenantProcess1 = setup.getProcesses().get(1);
        DA_Byzantine_RMI lieutenantProcess2 = setup.getProcesses().get(2);
        DA_Byzantine_RMI lieutenantProcess3 = setup.getProcesses().get(3);

        int maxTraitors = 1;
        Order order = Order.ATTACK;        
        
        try{
            commanderProcess.reset(numProcesses);
            lieutenantProcess1.reset(numProcesses);
            lieutenantProcess2.reset(numProcesses);
            lieutenantProcess3.reset(numProcesses);

            
            // Gives new order to himself, like a root in a graph is it's own parent.
            // The already processed stays empty.
            // Both indicate that this process is the commander.
            OrderMessage message = new OrderMessage(0, commanderProcess.getIndex(), commanderProcess.getIndex());
            message.setCurrentMaxTraitors(maxTraitors);
            message.setTotalTraitors(maxTraitors);
            message.setOrder(order);
            commanderProcess.receiveOrder(message);
            
            Thread.sleep(5000);
            Assert.assertTrue(commanderProcess.isDone());
            Assert.assertTrue(lieutenantProcess1.isDone());
            Assert.assertTrue(lieutenantProcess2.isDone());
            Assert.assertTrue(lieutenantProcess3.isDone());
            
            Assert.assertEquals(order, commanderProcess.getFinalOrder());
            Assert.assertEquals(order, lieutenantProcess1.getFinalOrder());
            Assert.assertEquals(order, lieutenantProcess2.getFinalOrder());
            Assert.assertEquals(order, lieutenantProcess3.getFinalOrder());
            
            int totalNumberOfReceivedMessages = commanderProcess.getNumberOfReceivedMessages();
            totalNumberOfReceivedMessages += lieutenantProcess1.getNumberOfReceivedMessages();
            totalNumberOfReceivedMessages += lieutenantProcess2.getNumberOfReceivedMessages();
            totalNumberOfReceivedMessages += lieutenantProcess3.getNumberOfReceivedMessages();
            
            System.out.println("Total number of messages send: " + totalNumberOfReceivedMessages);

        } catch (Exception e){
            e.printStackTrace();
            Assert.fail();
        }
    }
}