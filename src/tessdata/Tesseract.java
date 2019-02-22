package tessdata;

public class Tesseract {
    public static void main(String[] args) throws Exception {
        OCRUtil.runOCR("C:\\Program Files (x86)","E:/666.png",
                "E:/eng3",false,OCRUtil.LANG_USE);

    }
}
