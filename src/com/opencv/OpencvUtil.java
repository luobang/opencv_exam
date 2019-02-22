package com.opencv;

import answercard.*;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.util.*;
import java.util.List;

import static org.opencv.core.CvType.CV_8U;
import static org.opencv.imgproc.Imgproc.MORPH_RECT;

/**
 * Created by zxc on 2019/2/21.
 */
public class OpencvUtil {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) throws Exception {
        String sheet = "D://opencv//white.jpg";
        String desFile = "D://opencv//answer_card.jpg";
        List<QuestionItem> questionItems = new ArrayList<QuestionItem>();

        for (int i = 0; i < 100; i++) {
            QuestionItem questionItem = new QuestionItem();
            questionItem.setQuestionType(1);
            questionItem.setIndex(i + 1);
            questionItem.setAnswerCount(4);
            questionItems.add(questionItem);
        }

        //createAnswerCardImage(sheet, desFile, "2018answer", questionItems);
        recogAnswerCard(desFile, questionItems);
    }

    public static void recogAnswerCard(String path, List<QuestionItem> questionItems) throws Exception {
        //装载图片
        Mat img = Imgcodecs.imread(path);
        Mat srcImage2 = new Mat();
        Mat srcImage3 = new Mat();
        Mat srcImage4 = new Mat();
        Mat srcImage5 = new Mat();

        //图片变成灰度图片
        Imgproc.cvtColor(img, srcImage2, Imgproc.COLOR_RGB2GRAY);
        //图片二值化
        Imgproc.adaptiveThreshold(srcImage2, srcImage3, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 255, 1);
        //确定腐蚀和膨胀核的大小
        Mat element = Imgproc.getStructuringElement(MORPH_RECT, new Size(1, 6));
        //腐蚀操作
        Imgproc.erode(srcImage3, srcImage4, element);
        //膨胀操作
        Imgproc.dilate(srcImage4, srcImage5, element);
        //Imgcodecs.imwrite("E:/picpool/bankcard/enresults.jpg", srcImage4);

        //确定每张答题卡的ROI区域
        Mat imag_ch1 = srcImage4.submat(new Rect(50, 900, 2380, 2558));


        //识别所有轮廓
        Vector<MatOfPoint> chapter1 = new Vector<>();
        Imgproc.findContours(imag_ch1, chapter1, new Mat(), 2, 3);
        Mat result = new Mat(imag_ch1.size(), CV_8U, new Scalar(255));
        Imgproc.drawContours(result, chapter1, -1, new Scalar(0), 2);

        Imgcodecs.imwrite("D://opencv//result.jpg", result);


        //new一个 矩形集合 用来装 轮廓
        List<answercard.RectComp> RectCompList = new ArrayList<>();
        for (int i = 0; i < chapter1.size(); i++) {
            Rect rm = Imgproc.boundingRect(chapter1.get(i));
            answercard.RectComp ti = new answercard.RectComp(rm);
            //把轮廓宽度区间在 50 - 80 范围内的轮廓装进矩形集合
            if (ti.rm.width > 50 && ti.rm.width < 100) {
                RectCompList.add(ti);
            }
        }

        //new一个 map 用来存储答题卡上填的答案 (A\B\C\D)
        TreeMap<Integer, String> listenAnswer = new TreeMap<>();
        //按 X轴 对listenAnswer进行排序
        RectCompList.sort((o1, o2) -> {
            if (o1.rm.x > o2.rm.x) {
                return 1;
            }
            if (o1.rm.x == o2.rm.x) {
                return 0;
            }
            if (o1.rm.x < o2.rm.x) {
                return -1;
            }
            return -1;
        });

            /*
            如果精度高，可以通过像素计算
          for (RectComp rc : RectCompList) {
            int x = RectCompList.get(t).getRm().x - 16;
            int y = RectCompList.get(t).getRm().y - 94;

            //计算x轴上的分割 如果超过5题，那么还会有一个大分割
            int xSplit = x/85 /5;
            //因为第一题 x=21 计算机中题目从0开始算，现实是从1开始 所以+1
            int xTitleNum = x/85 + 1;

            //由于精度问题  x轴会慢慢递减  递减到上一个答案去 如果不跨过两个答案以上，都没问题  如果答题卡x轴40题左右 会出问题
            if(x%85>20){
                System.out.println("x轴递减程度" + x%85);
                xTitleNum++;
            }
            xTitleNum = xTitleNum - xSplit;
            System.out.println(xTitleNum);
            }
            */


        //根据 Y轴 确定被选择答案 (A\B\C\D)
        for (answercard.RectComp rc : RectCompList) {

            for (int h = 0; h < 5; h++) {
                if ((RectUtil.rectContainRect(new Rect(0, 100 + 500 * h, 2380, 100), rc.rm, 30))) {
                    int x=0;
                    for (int j = 0; j < 20; j++) {
                        if ((RectUtil.rectContainPoint(new Rect(x, 100 + 500 * h, 100, 100), new Point(rc.rm.x+rc.rm.width/2,rc.rm.y+rc.rm.height/2))))
                        {
                            listenAnswer.put(h*20+j+1,"C");
                            break;
                        }
                        x += 100;
                        if ((j + 1) % 4 == 0)
                            x += 100;
                    }
                } else if ((RectUtil.rectContainRect(new Rect(0, 200 + 500 * h, 2380, 100), rc.rm, 30))) {
                    int x=0;
                    for (int j = 0; j < 20; j++) {
                        if ((RectUtil.rectContainPoint(new Rect(x, 200 + 500 * h, 100, 100), new Point(rc.rm.x+rc.rm.width/2,rc.rm.y+rc.rm.height/2))))
                        {
                            listenAnswer.put(h*20+j+1,"C");
                            break;
                        }
                        x += 100;
                        if ((j + 1) % 4 == 0)
                            x += 100;
                    }
                } else if ((RectUtil.rectContainRect(new Rect(0, 300 + 500 * h, 2380, 100), rc.rm, 30))) {
                    int x=0;
                    for (int j = 0; j < 20; j++) {
                        if ((RectUtil.rectContainPoint(new Rect(x, 300 + 500 * h, 100, 100), new Point(rc.rm.x+rc.rm.width/2,rc.rm.y+rc.rm.height/2))))
                        {
                            listenAnswer.put(h*20+j+1,"C");
                            break;
                        }
                        x += 100;
                        if ((j + 1) % 4 == 0)
                            x += 100;
                    }
                } else if ((RectUtil.rectContainRect(new Rect(0, 400 + 500 * h, 2380, 100), rc.rm, 30))) {
                    int x=0;
                    for (int j = 0; j < 20; j++) {
                        if ((RectUtil.rectContainPoint(new Rect(x,400 + 500 * h, 100, 100), new Point(rc.rm.x+rc.rm.width/2,rc.rm.y+rc.rm.height/2))))
                        {
                            listenAnswer.put(h*20+j+1,"C");
                            break;
                        }
                        x += 100;
                        if ((j + 1) % 4 == 0)
                            x += 100;
                    }
                }
            }
        }

        Iterator iter = listenAnswer.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            System.out.println("第" + key + "题,分数:" + val);
        }
    }

    /**
     * 创建答题卡
     *
     * @param path
     * @param desFile
     * @param title
     * @param questionItems
     * @return
     * @throws Exception
     */
    public static boolean createAnswerCardImage(String path, String desFile, String title, List<QuestionItem> questionItems) throws Exception {
        Mat img = Imgcodecs.imread(path);
        //绘制整体边框线
        Imgproc.line(img, new Point(50, 50), new Point(2430, 50), new Scalar(0, 0, 0), 1);
        Imgproc.line(img, new Point(50, 50), new Point(50, 3458), new Scalar(0, 0, 0), 1);
        Imgproc.line(img, new Point(2430, 50), new Point(2430, 3458), new Scalar(0, 0, 0), 1);
        Imgproc.line(img, new Point(50, 3458), new Point(2430, 3458), new Scalar(0, 0, 0), 1);
        //绘制内容下边框
        Imgproc.line(img, new Point(50, 900), new Point(2430, 900), new Scalar(0, 0, 0), 1);
        //绘制答案及选项
        int rowNum = (questionItems.size() % 20 == 0) ? (questionItems.size() / 20) : (questionItems.size() / 20) + 1;

        int y = 1000;
        for (int i = 0; i < rowNum; i++) {
            int x = 60;
            int optionY = y;
            for (int j = 0; j < 20; j++) {
                Imgproc.putText(img, "" + (i * 20 + j + 1), new Point(x, y), Core.FONT_HERSHEY_SCRIPT_COMPLEX, 1, new Scalar(0, 0, 0));
                optionY += 100;
                //绘制4个选项A、B、C、D
                String[] options = {"[A]", "[B]", "[C]", "[D]"};
                for (int z = 1; z <= 4; z++) {
                    String option = options[z - 1];
                    Imgproc.putText(img, option, new Point(x, optionY), Core.FONT_HERSHEY_DUPLEX, 1, new Scalar(0, 0, 0));
                    optionY += 100;
                }
                x += 100;
                if ((j + 1) % 4 == 0)
                    x += 100;
                optionY = y;
            }

            y += 500;
        }
        Imgcodecs.imwrite(desFile, img);

        //绘制问卷标题
        writeTextToImage("问卷标题", 200, 200, desFile);
        return true;
    }

    public static void writeTextToImage(String text, int x, int y, String filePath) throws Exception {
        ImageIcon imgIcon = new ImageIcon(filePath);
        Image theImg = imgIcon.getImage();
        int width = theImg.getWidth(null) == -1 ? 200 : theImg.getWidth(null);
        int height = theImg.getHeight(null) == -1 ? 200 : theImg.getHeight(null);
        BufferedImage bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bimage.createGraphics();

        Color mycolor = Color.black;
        g.setColor(mycolor);
        g.setBackground(Color.white);
        g.drawImage(theImg, 0, 0, null);
        g.setFont(new Font("宋体", Font.PLAIN, 70)); //字体、字型、字号
        System.out.println(text);
        g.drawString(text, x, y); //画文字
        g.dispose();
        try {
            FileOutputStream out = new FileOutputStream(filePath); //先用一个特定的输出文件名
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bimage);
            param.setQuality(100, true);  //
            encoder.encode(bimage, param);
            out.close();
        } catch (Exception e) {
            throw e;
        }
    }

}
