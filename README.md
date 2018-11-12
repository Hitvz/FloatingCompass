应用链接：[悬浮指南针](http://shouji.baidu.com/software/11502324.html)

这是源码此应用源码

指南针部分是自定义view写出来

暂时还没时间写保存大小的功能

## 1、项目基本简介

1. 项目使用MVP结构
2. 使用自定义view绘制指南针，而不是使用图片
3. 使用NoActionBar的theme，自己使用控件实现Toolbar，主要为兼容Android低版本机型
4. 使用ConstraintLayout作为布局，重写onLayout和onDraw方法，用于绘制调节大小时的方框
5. 保存指南針配置到SharePreferences

## 2、目录结构

- --impl实现
- --interfaces 接口声明
- --module 实体类
- --mvp mvp的结构抽象及实现
- --service androidservice组件，实现浮窗时的功能逻辑
- --util 辅助工具类
- --view 自定义view及一些view的重写
- --res --values 其中strings是默认英文适配 --values-zh 里面的strings是中文翻译


