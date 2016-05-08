# SpeechBrowser
####@Author 武汉理工大学 胡科
####@Domin www.hukecn.cn
<br>
##项目截图展示：
<img src="https://github.com/hukecn/images/blob/master/img1.png" width="270" height="432" />
<img src="https://github.com/hukecn/images/blob/master/img2.png" width="270" height="432" />
<img src="https://github.com/hukecn/images/blob/master/img3.png" width="270" height="432" />
<img src="https://github.com/hukecn/images/blob/master/img4.png" width="270" height="432" />
<img src="https://github.com/hukecn/images/blob/master/img5.png" width="270" height="432" />
<img src="https://github.com/hukecn/images/blob/master/img6.png" width="270" height="432" />
<img src="https://github.com/hukecn/images/blob/master/img7.png" width="270" height="432" />
<img src="https://github.com/hukecn/images/blob/master/img8.png" width="270" height="432" />
<img src="https://github.com/hukecn/images/blob/master/img9.png" width="270" height="432" />
<p>语音浏览器基于现有的语音识别技术——科大讯飞SR，对用户的语音指令进行解析，然后通过WebView访问互联网获取用户需求信息，客户端对获取到的HTML源码采用基于行块分布函数的网页正文抽取算法，抽取其正文信息。对正文进行预处理后，采用科大讯飞语音合成技术，向用户输出所获得的信息。</p>
<p>目前系统已实现的功能有新闻播报功能、邮件播报功能，获取天气状况与实时位置功能，针对部分网页为了提高其正文抽取精确度，还采用了特定的基于Jsoup的抽取算法。应用中增加了“摇一摇”发指令的功能，以方便不同的用户群体使用。目前APP已适配主流安卓设备，经测试性能稳定。</p>
