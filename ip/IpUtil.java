package cn.resource.jiaqi.action;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

public class IpUtil {
    /**
     * 日志记录
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractBaseController.class);
    
    private static final List<String> IP_HEADER = ImmutableList.of(
            "x-forwarded-for", "X_Forwarded_For", "X-Real-IP", "Proxy-Client-IP",
            "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR");
    
    /**
     * 获取ip地址
     */
    protected String getIpString(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        LOGGER.info("Request Params Header {} !", ip);
        
        for (int i = 0; i < IP_HEADER.size() && isValidIp(ip); i++) {
            ip = request.getHeader(IP_HEADER.get(i));
            LOGGER.info("Request Params Header {}: {} !", IP_HEADER.get(i), ip);
        }
        
        if (isValidIp(ip)) {
            ip = request.getRemoteAddr();
            LOGGER.info("Request Params RemoteAddr: {}!", ip);
        }
        String optionalIp = Optional.ofNullable(ip).orElse("0.0.0.0");
        // 如果长度过长，取第一组
        if (optionalIp.length() > 15) {
                String[] ips = optionalIp.split(",");
                optionalIp = ips[0].trim();
        }
        return optionalIp;
    }
    
    /**
     * 判断IP是否为空
     * 
     * @param ip @return boolean @throws
     */
    private static boolean isValidIp(String ip) {
        return Strings.isNullOrEmpty(ip) || "unknown".equalsIgnoreCase(ip);
    }

    /**
     * 判断IP是否合法
     * 
     * @param ip
     *            传入的ip字符串
     * @return 返回IP合法情况
     */
    public static boolean isValidFormat(String ip) {
        LOGGER.debug("isValidFormat[ip:{}]", ip);
        boolean res = ip.matches("^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                        + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                        + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                        + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$");
        LOGGER.debug("isValid return with value:{}", res);
        return res;
    }
}