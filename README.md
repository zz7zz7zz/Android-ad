# Android-ad


一、国外广告SDK：

    一、Google广告

    二、Facebook广告
    
    三、Twitter广告
    
    四、华为广告


二、设计思路（基于策略的逻辑设计SDK）：
    
    App_SDK_AD：

        App_SDK_AD_GOOGLE

        App_SDK_AD_Huawei
    
        App_SDK_AD_ZFB
    
        App_SDK_AD_TX

       
 具体实现如下：           
    
    AD_Base: 
        定义接口，定义基本的数据类型
 
        AD_Impl_A: 

        AD_Impl_B: 

        AD_Impl_C: 
    
            实现具体支付(也即上面的App_SDK_AD_GOOGLE...)-->依赖AD_Base


    AD_Main: 调用逻辑 
        -->根据配置动态依赖AD_Impl_A，AD_Impl_B，AD_Impl_C
        -->依赖AD_Base


三、支付流程

  客户端流程：

    提供接入最快最简的最新SDK


  服务器流程：

      1.提供广告配置接口（策略调优）
        a.主动拉取
        b.被动接收(如通过推送SDK实时接收)

      2.提供直客广告响应接口，及时返回广告数据

      3.1.提供数据上报接口（收集展示广告的request,requestSuccess,Impression,Click等基础数据），
      3.2.从第三方获取广告数据（包括各个第三方的基础数据，收入数据等）

      3.1和3.2进行数据合并，展示广告的基础数据，广告收入数据（以报表的形式）

      4.提供实时最新的SDK

    总的来说
      1.我们的角色是作为SDK技术提供方，提供接入最快最简的最新SDK
      2.我们为客户提供最大的广告变现能力，我们对广告进行调优，对广告策略调优
      3.我们提供数据报表
      
  问题：商务市场对外的恰谈能力


    接入方式，联运接入：
      1.cp自己接入
      2.cp不愿自己接入，但是愿意提供代码，我方帮助接入
      
    收费方式：
      1.预先支付费用
      2.广告分成按比例
