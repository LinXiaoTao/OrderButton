### OrderButton是一个仿造*饿了么*下单按钮的自定义View

#### 感谢
部分代码借鉴这个项目[ElemeShoppingView](https://github.com/JeasonWong/ElemeShoppingView)

#### 效果图
![order.gif](https://github.com/LinXiaoTao/OrderButton/blob/master/gif/order.gif)

#### 相关知识
这个自定义View的效果实现倒不是难点所在，而在于应用在RecyclerView等涉及View复用的控件上，因为View的复用，所以要
处理好状态，才不会出现混乱的局面.
效果的实现主要是用了ValueAnimator
