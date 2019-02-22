package com.opencv;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zxc on 2019/2/21.
 */
public class OpencvUtil2 {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) throws Exception{
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

        createAnswerCardImage(sheet, desFile, "2018answer", questionItems);
    }


    public static boolean createAnswerCardImage(String path, String desFile, String title, List<QuestionItem> questionItems) throws Exception{
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
        writeTextToImage("问卷标题",200,200,desFile);
        return true;
    }

    public static void writeTextToImage(String text, int x, int y, String filePath) throws Exception {
        ImageIcon imgIcon = new ImageIcon(filePath);
        Image theImg = imgIcon.getImage();
        int width = theImg.getWidth(null) == -1 ? 200 : theImg.getWidth(null);
        int height = theImg.getHeight(null) == -1 ? 200 : theImg.getHeight(null);
        System.out.println(width);
        System.out.println(height);
        System.out.println(theImg);
        BufferedImage bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bimage.createGraphics();

        Color mycolor = Color.red;
        g.setColor(mycolor);
        g.setBackground(Color.red);
        g.drawImage(theImg, 0, 0, null);
        g.setFont(new Font("宋体", Font.PLAIN, 50)); //字体、字型、字号
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
