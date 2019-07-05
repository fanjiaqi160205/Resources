package cn.resource.jiaqi.action;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

public class ValidateUtil {
    /**
     * 日志记录
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractBaseController.class);
    
    /**
     * @Title: validateData @Description: 验证数据 @param bindResult 绑定数据 @return
     *         ErrorMessageDictionary @throws
     */
    public String validateData(BindingResult bindResult) {
        List<ObjectError> errorList = bindResult.getAllErrors();
        LOGGER.info("validate field : List<ObjectError> :{}" ,errorList);
        String errorMsg = "";
        if (errorList != null && !errorList.isEmpty()) {
            for (ObjectError errorDate : errorList) {
                errorMsg = errorDate.getDefaultMessage();
                LOGGER.info("validate field : getDefaultMessage :{}" , errorMsg);
                break;
            }
            return errorMsg;
        }
        return "";
    }
}