package search.avtivemq.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;
import search.service.ItemSearchService;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.util.Arrays;

public class ItemDeleteMessageListener extends
        AbstractAdaptableMessageListener{

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        //接受信息
        ObjectMessage objectMessage = (ObjectMessage) message;
        //从信息中获取内容
        Long[] goodsIds = (Long[]) objectMessage.getObject();
        //同步索引库
        itemSearchService.deleteItemByGoodsIdList(Arrays.asList(goodsIds));
        System.out.println("同步删除索引库中数据完成！");
    }
}
