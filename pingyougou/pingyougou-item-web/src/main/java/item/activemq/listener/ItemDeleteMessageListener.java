package item.activemq.listener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.io.File;

public class ItemDeleteMessageListener extends
        AbstractAdaptableMessageListener{
    @Value("ITEM_HTML_PATH")
    private String ITEM_HTML_PATH;

    //删除商品操作（接受）
    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        ObjectMessage objectMessage = (ObjectMessage) message;
        Long[] goodsIds = (Long[]) objectMessage.getObject();
        for(Long goodsId : goodsIds){
            String fileName = ITEM_HTML_PATH + goodsId + ".html";
            File file = new File(fileName);
            if(file.exists()){
                file.delete();
            }
        }
    }
}
