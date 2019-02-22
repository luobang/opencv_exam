package tessdata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author lsw
 *
 */
public class OCRUtil {

    //使用简体中文
    public static final String LANG_USE = "chi_sim";


    public static void runOCR(String realPath, String imagePath, String outPath, boolean chooseLang,String lang) throws Exception {
        try {

            Runtime r = Runtime.getRuntime();
            //String cmd = "\""+realPath+"Tesseract-OCR\\tesseract.exe\" \""+imagePath+"\" \""+outPath+"\" -l "+(chooseLang?lang:"");
            String cmd =  "tesseract "+imagePath+" "+outPath+"  -l "+(chooseLang?lang:"");
            Process process = r.exec(cmd);
            System.out.println(cmd);

            //在 Windows平台上，运行被调用程序的DOS窗口在程序执行完毕后往往并不会自动关闭，
            // 从而导致Java应用程序阻塞在waitfor()语句。导致该现象的一个可能的原因是，该可执行程序的标准输出比较多，
            // 而运行窗口的标准输出缓冲区不够大。解决的办法是，利用Java中Process类提供的方法让
            // Java虚拟机截获被调用程序的DOS运行窗口的标准输出，在waitfor()命令之前读出窗口的标准输出缓冲区中的内容。
            String s;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while((s=bufferedReader.readLine()) != null);
            System.out.println(s);


            process.waitFor();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
