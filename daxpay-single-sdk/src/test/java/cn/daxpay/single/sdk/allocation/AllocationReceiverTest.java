package cn.daxpay.single.sdk.allocation;

import cn.daxpay.single.sdk.code.PayChannelEnum;
import cn.daxpay.single.sdk.code.SignTypeEnum;
import cn.daxpay.single.sdk.model.allocation.AllocReceiverAddModel;
import cn.daxpay.single.sdk.model.allocation.AllocReceiverRemoveModel;
import cn.daxpay.single.sdk.model.allocation.AllocReceiversModel;
import cn.daxpay.single.sdk.net.DaxPayConfig;
import cn.daxpay.single.sdk.net.DaxPayKit;
import cn.daxpay.single.sdk.param.allocation.AllocReceiverAddParam;
import cn.daxpay.single.sdk.param.allocation.AllocReceiverRemoveParam;
import cn.daxpay.single.sdk.param.allocation.QueryAllocReceiverParam;
import cn.daxpay.single.sdk.response.DaxPayResult;
import org.junit.Before;
import org.junit.Test;

/**
 * 分账接收者测试类
 * @author xxm
 * @since 2024/5/27
 */
public class AllocationReceiverTest {

    @Before
    public void init() {
        // 初始化支付配置
        DaxPayConfig config = DaxPayConfig.builder()
                .serviceUrl("http://127.0.0.1:9000")
                .signSecret("123456")
                .signType(SignTypeEnum.HMAC_SHA256)
                .build();
        DaxPayKit.initConfig(config);
    }

    /**
     * 添加
     */
    @Test
    public void add() {
        AllocReceiverAddParam param = new AllocReceiverAddParam();
        param.setChannel(PayChannelEnum.ALI.getCode());
        param.setClientIp("127.0.0.1");
        param.setReceiverName("测试");
        param.setRelationType("user");
        param.setRelationName("测试");
        param.setReceiverNo("123456");
        param.setReceiverType("user");
        param.setReceiverAccount("123456");
        DaxPayResult<AllocReceiverAddModel> execute = DaxPayKit.execute(param);
        System.out.println(execute);
        System.out.println(execute.getData());
    }

    /**
     * 删除
     */
    @Test
    public void remove() {
        AllocReceiverRemoveParam param = new AllocReceiverRemoveParam();
        param.setClientIp("127.0.0.1");
        param.setReceiverNo("123456");
        DaxPayResult<AllocReceiverRemoveModel> execute = DaxPayKit.execute(param);
        System.out.println(execute);
        System.out.println(execute.getData());
    }

    /**
     * 查询
     */
    @Test
    public void query() {
        QueryAllocReceiverParam param = new QueryAllocReceiverParam();
        param.setChannel(PayChannelEnum.ALI.getCode());
        param.setClientIp("127.0.0.1");
        DaxPayResult<AllocReceiversModel> execute = DaxPayKit.execute(param);

        System.out.println(execute);
        System.out.println(execute.getData());
    }


}
