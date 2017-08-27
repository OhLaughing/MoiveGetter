package com.bigwanggang;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        String s = "王牌保安][BD-mkv.720p.中英双字][2017年动作] ◎译　　名　王牌保安/保安 ◎片　　名　Security ◎年　　代　2017 ◎产　　地　美国 ◎类　　别　动作 ◎语　　言　英语 ◎字　　幕　中英双字幕 ◎IMDb评分  5.7/10 from 2,951 users ◎文件格式　x264 + AAC ◎视频尺寸　1280 x 720 ◎文件大小　1CD ◎片　　长　87分钟 ◎导　　演　Alain Desrochers ◎主　　演　本·金斯利 Ben Kingsley 　　　　　　安东尼奥·班德拉斯 Antonio Banderas 　　　　　　连姆·麦肯泰尔 Liam McIntyre 　　　　　　汪东城  Jiro Wang 　　　　　　查德·林德伯格 Chad Lindberg 　　　　　　加布里埃拉·赖特 Gabriella Wright 　　　　　　黎烈弓 Cung Le 　　　　　　马克·史密斯 Mark Rhino Smith 　　　　　　巴沙尔·拉海尔 Bashar Rahal ◎简　　介 　　退伍老兵(安东尼奥·班德拉斯饰)在一个购物商场当保安，然而他工作的首晚，一个被歹徒追击的女孩跑到商场寻求庇护……";
        String[] ss = s.split("◎");
        for (String s1 : ss) {
            System.out.println(s1);
        }
    }
}
