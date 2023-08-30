# FjRxBus
#背景

准备用于公司项目

#小Tip 

注意：目前并不会和RxBinding冲突，也不需要限制版本为android 26以上。
如果出现需要26以上版本的冲突。请使用java 8以上编译

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
    
#使用

Step 1.Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.mo-shangren:FjRxBus:1.0.0'
	}

#使用注解

代码简洁，方便易用，就是必须注册和注销；

    RxBus.get().register(obj)；
    RxBus.get().unregister(obj)；

用超类注册如何？会不会看起来难受？就是注册的时候注解会扫一遍该类所有的方法吧观察者提取出来保存
注销的时候用取出观察者杀掉


观察者采用多标签tags和类型type作为验证

    //完整体观察者：
    @Subscribe(
        tags = {
            @Tag(value = "标签1"),
            @Tag(value = "标签2"),
            @Tag(value = "标签3")
        }
    )
    public void test1(Xxxx data) {
        // 这里可以做你的操作了
        // 只要消息是Xxxx类型或者继承自Xxxx的子类，并且满足Tag与任意一个Tag相同就可以回调这个方法
        // 强调上面的 继承 二字
        // Xxxx不能是接口
        
    }
    // 对应：任意一条都会回调这个观察者
    RxBus.get().post("标签1",new Xxxx());
    RxBus.get().post("标签2",new Xxxx());
    RxBus.get().post("标签3",new Xxxx());
    
    ----------------------------------------------------------------------------------
    
    //观察者-2
    @Subscribe()
    public void test2() {
        // 这里可以做你的操作了
        // 这个观察者其实是Default类型，Tag为rxbus_default_tag
        
    }
    // 对应：
    RxBus.get().post();
    
    ----------------------------------------------------------------------------------
    
    //观察者-3
    @Subscribe(
        tags = {
            @Tag(value = "标签1"),
            @Tag(value = "标签2"),
            @Tag(value = "标签3")
        }
    )
    public void test3() {
        // 这里可以做你的操作了
    }
    // 对应：
    RxBus.get().postTag("标签1");//改了方法名是为了跟下一条作区分
    
    ----------------------------------------------------------------------------------
    
    //观察者-4
    @Subscribe()
    public void test4(Xxxx data) {
        // 这里可以做你的操作了
        // 这个观察者的Tag其实是rxbus_default_tag
        // Xxxx不能是接口
    }
    // 对应：
    RxBus.get().post(new Xxxx());
    
限制：那个观察者的观察类型，不能是接口。我想过办法了，改不了。
原因：观察者存储在一个HashMap里面作为Key的是EventType，里面放的是String tag和Class<?> clzss；
HashMap的get(key)的底层用的是getHashCode()作为对比。。。
若不是interface: clzss.getHashCode()和继承自他的子类的getHashCode()是相同的；若是interface：则不同。
所以！如果类型参数超过一个，或者是interface类型。。。我就抛异常


#Produce 

暂且叫它生产者吧。。

    @Produce(
        tags = {
            @Tag(value = "Produce")
        }
    )
    public String sendMsg() {
        //我觉得这个有毛病。。。注册即发送，又不能重复发送？？？
        //而且不能放在Activity里。
        //这个方法的本质：
        //RxBus.get().register(mProduce); ==> RxBus.get().post("Produce","注册立即发送该消息");
        //把它删了岂不是美滋滋。。。
        return "注册立即发送该消息";
    }

这个没怎么改动。我还没理解他的使用意图。。。到底什么情况下需要这种消息？？

删不删？

#已经删了⬆️





















