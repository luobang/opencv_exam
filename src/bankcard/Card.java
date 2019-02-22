package bankcard;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


/**
 * @author
 * @email
 */
public class Card {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        // 读取图像，参数为图像的存储路径
        Mat source = Imgcodecs.imread("D://bankcard.jpg");

        runCanny(source);

    }


    private static void runCanny(Mat srcImage) {

        // 参数定义
        // Mat dstImage = new Mat();
        Mat edge = new Mat();


        // 将原图转化成灰度图
        Imgproc.cvtColor(srcImage, edge, Imgproc.COLOR_BGR2GRAY);

        // 使用3x3内核来降噪
        Imgproc.blur(edge, edge, new Size(3, 3));

        // 运行Canny算子
        Imgproc.threshold(edge, edge, 30, 255, Imgproc.THRESH_BINARY);

        // 3*3图像腐蚀
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Imgproc.erode(edge, edge, element);

        //上下界
        int a = 0;
        int b = 0;

        int state = 0;

        for (int y = 0; y < edge.height(); y++) {
            // 阈值
            int count = 0;

            for (int x = 0; x < edge.width(); x++) {
                // 得到该行像素点的值
                byte[] data = new byte[1];
                edge.get(y, x, data);
                if (data[0] == 0){
                    count = count + 1;
                }

            }
            // 还未到有效行
            if (state == 0) {
                // 找到了有效行
                if (count >= 150) {
                    // 有效行允许十个像素点的噪声
                    a = y;
                    state = 1;
                }
            } else if (state == 1) {
                // 找到了有效行
                if (count <= 150) {

                    // 有效行允许十个像素点的噪声
                    b = y;
                    state = 2;
                }
            }
        }
        System.out.println("过滤下界" + Integer.toString(a));
        System.out.println("过滤上界" + Integer.toString(b));

        // 参数,坐标X,坐标Y,截图宽度,截图长度
        Rect roi = new Rect(0, a, edge.width(), b - a);

        Mat roi_img = new Mat(edge, roi);
        Mat tmp_img = new Mat();

        roi_img.copyTo(tmp_img);

        System.out.println(Imgcodecs.imwrite("D://blackto.jpg", roi_img));

    }
}
