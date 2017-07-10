package com.rockie;

import com.rockie.bpnn.Bpnn;
import com.rockie.bpnn.BpnnJudge;

public class Main {

    private static final String[] mNormalSms = new String[]{
            "妈妈不在家，你打电话",
            "通知，请每周一按时送孩子入园",
            "我正在路上",
            "中午吃饭在哪",
            "感谢您使用方标电信公司的指间话语业务，服务代码93891，信息费0.10元/次，由中国移动代收，客服电话010-58696692。中国移动",
            "温馨提示:尊敬的动感地带客户,您下月的套餐费用将在下月1号扣除,请及时拨打1008611查询余额,避免余额不足以扣除套餐费而影响通信",
    };

    private static final String[] mGarbageSms = new String[]{
            "3月底前至北京川百汇干锅烤鱼刷中信信用卡消费满200元减30,酒水特价菜除外",
            "有房/有车短期借款,当日放款，车最快5分钟拿钱，银行贷2-5天批，可贷评估值的100%。询5l292222",
            "安装卫星电视,可收看欧美港台好莱坞大片.HBO.CNN.凤凰卫视.国际新闻体育时尚.地理探索及加密成人电影,咨询13880357308尹旺",
            "中长期标准型纯债基金连续九年正收益，年化收益率5.68%。长盛季季红一年期债券基金",
            "尊敬的动感地带客户,您鸡蛋大小，特供“新疆大红枣”，纯天然绿色食品！肉厚，香甜可口，富含维生素营养极高！自食送礼均可产量有限！抢购热线4006508818货到付款 ",
            "专业安装家用卫星电视、看HBO、东森、香港凤凰卫视、星空、新闻、时装、好莱坞、欧美动作电影大片、成人电影等节目、电话13857118659",
    };

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        System.out.println("------------begin to train sms -------");
        //BpnnJudge.getInstance().trainBp();
        BpnnJudge.getInstance().loadData("files/bpnndataset");
        long end = System.currentTimeMillis();
        System.out.println("------------train sms finished, used: -------" + (end - start));

        System.out.println("------------normal sms check result -------");
        for (int i = 0; i < mNormalSms.length; i++) {
            BpnnJudge.getInstance().isGarbage(mNormalSms[i]);
        }

        System.out.println("------------garbage sms check result -------");
        for (int i = 0; i < mGarbageSms.length; i++) {
            BpnnJudge.getInstance().isGarbage(mGarbageSms[i]);
        }

        System.exit(0);
    }
}
