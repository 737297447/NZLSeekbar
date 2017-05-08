# NZLSeekbar
范围拖动条seekbar

![](https://github.com/737297447/NZLSeekbar/blob/master/Screenshot_2017-05-08-17-43-38-896_com.nzl.seekba.png)

#如何使用
直接拷贝项目中activty_layout中的布局,注意修改为你项目的路径
如果要改样式,就重新替换图片就可以
nzltext:grayView="@mipmap/nzl_seekbar_gary" //灰色背景
nzltext:blueView="@mipmap/nzl_seekbar_blue" //蓝色进度条
nzltext:my_seekBar="@mipmap/nzl_seekbar" //拖动图片

下面这个可以不同改,因为是动态变化蓝色进度条的宽度达到拖动的效果,
如果蓝色进度条的宽度很窄,最做的的圆角体现不出来,所以用这个图片
盖在蓝色进度条上面,体现出圆角的效果
nzltext:leftView="@mipmap/nzl_seekbar_left"



代码实现监听文字变化 
#接口(NZLSeekbar.OnSeekChangeListener)

设置当前在那个位置,不能大于本身刻度的数量
seekbar.setPosition(5);
seekbar.setOnSeekChangeListener(this);

