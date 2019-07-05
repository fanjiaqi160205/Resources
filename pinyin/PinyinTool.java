package cn.resource.jiaqi.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
  * <p>
  *   PinyinTool描述
  * </p>
  *  
  * @author fanjiaqi
  * @since 0.0.1
  */
public class PinyinTool {

    private static final Logger LOGGER = LoggerFactory.getLogger(PinyinTool.class);
    
    /**
     * 构造方法
     */
    private PinyinTool() {
		// do something
	}
    
    /**
      * <p>
      *    获取每个词的首字母
      * </p>
      *
      * @action
      *    fanjiaqi 2019年1月4日 下午4:23:58 描述
      *
      * @param chinese
      * @return String
     */
    public static String cn2FirstLetter(String chinese) {
        StringBuilder pybd = new StringBuilder();
        char[] chineseArr = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for(int i =0; i < chineseArr.length; i++)
            getPinyin(pybd, chineseArr, defaultFormat, i);
        return pybd.toString().replaceAll("\\W", "").trim().toUpperCase();
    }

    private static void getPinyin(StringBuilder pybd, char[] chineseArr,
            HanyuPinyinOutputFormat defaultFormat, int i) {
        if (chineseArr[i] > 128) {
            try {
                String[] firstLetter = PinyinHelper.toHanyuPinyinStringArray(chineseArr[i],
                    defaultFormat);
                if (null != firstLetter) {
                    pybd.append(firstLetter[0]);
                }
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                LOGGER.error("cn2FirstLetter exception:", e);
                pybd.append("");
            }
        } else {
            pybd.append(chineseArr[i]);
        }
    }
}