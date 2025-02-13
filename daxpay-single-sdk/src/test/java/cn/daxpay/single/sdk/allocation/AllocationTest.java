package cn.daxpay.single.sdk.allocation;

import cn.daxpay.single.sdk.code.PayChannelEnum;
import cn.daxpay.single.sdk.code.PayMethodEnum;
import cn.daxpay.single.sdk.code.SignTypeEnum;
import cn.daxpay.single.sdk.model.allocation.AllocationModel;
import cn.daxpay.single.sdk.model.pay.PayModel;
import cn.daxpay.single.sdk.model.sync.AllocSyncModel;
import cn.daxpay.single.sdk.net.DaxPayConfig;
import cn.daxpay.single.sdk.net.DaxPayKit;
import cn.daxpay.single.sdk.param.allocation.AllocFinishParam;
import cn.daxpay.single.sdk.param.allocation.AllocSyncParam;
import cn.daxpay.single.sdk.param.allocation.AllocationParam;
import cn.daxpay.single.sdk.param.pay.PayParam;
import cn.daxpay.single.sdk.response.DaxPayResult;
import cn.hutool.core.util.RandomUtil;
import org.junit.Before;
import org.junit.Test;

/**
 * 支付分账测试
 * @author xxm
 * @since 2024/4/6
 */
public class AllocationTest {

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
     * 创建手动分账订单
     */
    @Test
    public void allocationOrder() {
        PayParam param = new PayParam();
        param.setClientIp("127.0.0.1");
        param.setNotNotify(true);

        param.setBizOrderNo("SDK_"+ System.currentTimeMillis());
        param.setTitle("测试手动分账");
        param.setDescription("这是支付备注");
        param.setAmount(10000);
        param.setChannel(PayChannelEnum.UNION_PAY.getCode());
        param.setMethod(PayMethodEnum.QRCODE.getCode());
        param.setAttach("{回调参数}");
        param.setAllocation(true);
        param.setReturnUrl("https://abc.com/returnurl");
        param.setNotifyUrl("https://abc.com/callback");

        DaxPayResult<PayModel> execute = DaxPayKit.execute(param);
        System.out.println(execute);
        System.out.println(execute.getData());
    }

    /**
     * 创建自动分账订单
     */
    @Test
    public void allocationAutoOrder() {
        PayParam param = new PayParam();
        param.setClientIp("127.0.0.1");
        param.setNotNotify(true);

        param.setBizOrderNo("SDK_"+ System.currentTimeMillis());
        param.setTitle("测试手动分账");
        param.setDescription("这是支付备注");
        param.setAmount(10000);
        param.setChannel(PayChannelEnum.UNION_PAY.getCode());
        param.setMethod(PayMethodEnum.QRCODE.getCode());
        param.setAttach("{回调参数}");
        param.setAllocation(true);
        param.setAutoAllocation(true);
        param.setReturnUrl("https://abc.com/returnurl");
        param.setNotifyUrl("https://abc.com/callback");

        DaxPayResult<PayModel> execute = DaxPayKit.execute(param);
        System.out.println(execute);
        System.out.println(execute.getData());
    }

    /**
     * 手动发起分账, 使用默认分账组
     */
    @Test
    public void allocationOpen() {
        // 分账参数
        AllocationParam param = new AllocationParam();
        param.setBizAllocationNo("A"+ RandomUtil.randomNumbers(5));
        param.setAttach("88899");
        param.setBizOrderNo("P1717073355992");
        param.setDescription("测试分账");
        param.setClientIp("127.0.0.1");

        DaxPayResult<AllocationModel> execute = DaxPayKit.execute(param);
        System.out.println(execute);
    }

    /**
     *
     */

    /**
     * 分账完结
     */
    @Test
    public void allocationFinish() {
        // 分账完结参数
        AllocFinishParam param = new AllocFinishParam();
        param.setBizAllocationNo("A1213");

        DaxPayResult<AllocationModel> execute = DaxPayKit.execute(param);
        System.out.println(execute);
    }

    /**
     * 分账同步
     */
    @Test
    public void allocationSync() {
        // 分账同步参数
        AllocSyncParam param = new AllocSyncParam();
        param.setBizAllocationNo("A1213");

        DaxPayResult<AllocSyncModel> execute = DaxPayKit.execute(param);
        System.out.println(execute);
    }

}
