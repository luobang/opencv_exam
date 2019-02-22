package answercard;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.*;

import static org.opencv.core.CvType.CV_8U;
import static org.opencv.imgproc.Imgproc.MORPH_RECT;


/**
 * @author  answercard
 * @email lsw_demail@163.com
 */
public class OpenCv {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String []args) {
        String sheet = "D://opencv//A4.jpg";

        //A4 二值化膨胀后生成的图片路径
        String results = "D://opencv//result.jpg";
        String msg = rowsAndCols(sheet, results);
        System.out.println(msg);
    }

    public static void Canny(String oriImg, String dstImg, int threshold) {
        //装载图片
        Mat img = Imgcodecs.imread(oriImg);
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
        Mat imag_ch1 = srcImage4.submat(new Rect(200, 1065, 1930, 2210));


        //识别所有轮廓
        Vector<MatOfPoint> chapter1 = new Vector<>();
        Imgproc.findContours(imag_ch1, chapter1, new Mat(), 2, 3);
        Mat result = new Mat(imag_ch1.size(), CV_8U, new Scalar(255));
        Imgproc.drawContours(result, chapter1, -1, new Scalar(0), 2);

        Imgcodecs.imwrite("D://opencv//result.jpg", result);



        //new一个 矩形集合 用来装 轮廓
        List<RectComp> RectCompList = new ArrayList<>();
        for (int i = 0; i < chapter1.size(); i++) {
            Rect rm = Imgproc.boundingRect(chapter1.get(i));
            RectComp ti = new RectComp(rm);
            //把轮廓宽度区间在 50 - 80 范围内的轮廓装进矩形集合
            if (ti.rm.width > 60 && ti.rm.width < 85) {
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
        for (RectComp rc : RectCompList) {

            for (int h = 0; h < 7; h++) {
                if ((rc.rm.contains(new Point(rc.rm.x + 20, 115 + (320 * h))))) {
                    for (int w = 0; w < 4; w++) {
                        if (rc.rm.contains(new Point(55 + (500 * w), rc.rm.y))) {
                            listenAnswer.put(1 + (20 * h) + (5 * w), "A");
                        } else if (rc.rm.contains(new Point(135 + (500 * w), rc.rm.y))) {
                            listenAnswer.put(2 + (20 * h) + (5 * w), "A");
                        } else if (rc.rm.contains(new Point(215 + (500 * w), rc.rm.y))) {
                            listenAnswer.put(3 + (20 * h) + (5 * w), "A");
                        } else if (rc.rm.contains(new Point(300 + (500 * w), rc.rm.y))) {
                            listenAnswer.put(4 + (20 * h) + (5 * w), "A");
                        } else if (rc.rm.contains(new Point(380 + (500 * w), rc.rm.y))) {
                            listenAnswer.put(5 + (20 * h) + (5 * w), "A");
                        }
                    }
                } else if ((rc.rm.contains(new Point(rc.rm.x + 20, 165 + (320 * h))))) {
                    for (int w = 0; w < 4; w++) {
                        if (rc.rm.contains(new Point(55 + (500 * w), rc.rm.y))) {
                            listenAnswer.put(1 + (20 * h) + (5 * w), "B");
                        } else if (rc.rm.contains(new Point(135 + (500 * w), rc.rm.y))) {
                            listenAnswer.put(2 + (20 * h) + (5 * w), "B");
                        } else if (rc.rm.contains(new Point(215 + (500 * w), rc.rm.y))) {
                            listenAnswer.put(3 + (20 * h) + (5 * w), "B");
                        } else if (rc.rm.contains(new Point(300 + (500 * w), rc.rm.y))) {
                            listenAnswer.put(4 + (20 * h) + (5 * w), "B");
                        } else if (rc.rm.contains(new Point(380 + (500 * w), rc.rm.y))) {
                            listenAnswer.put(5 + (20 * h) + (5 * w), "B");
                        }
                    }
                } else if ((rc.rm.contains(new Point(rc.rm.x + 20, 220 + (320 * h))))) {
                    for (int w = 0; w < 4; w++) {
                        if (rc.rm.contains(new Point(55 + (500 * w), rc.rm.y))) {
                            listenAnswer.put(1 + (20 * h) + (5 * w), "C");
                        } else if (rc.rm.contains(new Point(135 + (500 * w), rc.rm.y))) {
                            listenAnswer.put(2 + (20 * h) + (5 * w), "C");
                        } else if (rc.rm.contains(new Point(215 + (500 * w), rc.rm.y))) {
                            listenAnswer.put(3 + (20 * h) + (5 * w), "C");
                        } else if (rc.rm.contains(new Point(300 + (500 * w), rc.rm.y))) {
                            listenAnswer.put(4 + (20 * h) + (5 * w), "C");
                        } else if (rc.rm.contains(new Point(380 + (500 * w), rc.rm.y))) {
                            listenAnswer.put(5 + (20 * h) + (5 * w), "C");
                        }
                    }
                } else if ((rc.rm.contains(new Point(rc.rm.x + 20, 275 + (320 * h))))) {
                    for (int w = 0; w < 4; w++) {
                        if (rc.rm.contains(new Point(55 + (500 * w), rc.rm.y))) {
                            listenAnswer.put(1 + (20 * h) + (5 * w), "D");
                        } else if (rc.rm.contains(new Point(135 + (500 * w), rc.rm.y))) {
                            listenAnswer.put(2 + (20 * h) + (5 * w), "D");
                        } else if (rc.rm.contains(new Point(215 + (500 * w), rc.rm.y))) {
                            listenAnswer.put(3 + (20 * h) + (5 * w), "D");
                        } else if (rc.rm.contains(new Point(300 + (500 * w), rc.rm.y))) {
                            listenAnswer.put(4 + (20 * h) + (5 * w), "D");
                        } else if (rc.rm.contains(new Point(380 + (500 * w), rc.rm.y))) {
                            listenAnswer.put(5 + (20 * h) + (5 * w), "D");
                        }
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

    public static String rowsAndCols(String oriImg, String dstImg) {
        String msg = "";

        Canny(oriImg, dstImg, 50);

        Mat mat = Imgcodecs.imread(dstImg);
        msg += "\n行数:" + mat.rows();
        msg += "\n列数:" + mat.cols();
        msg += "\nheight:" + mat.height();
        msg += "\nwidth:" + mat.width();
        msg += "\nelemSide:" + mat.elemSize();
        //CvType contourSeq = null;

        return msg;
    }
}
