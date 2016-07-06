/*
 * 
 */
package com.shell.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author shell
 *
 */
public class RequestUtil {
//	private static final String MD5_SOLT = "";
//	private static final int URL_MD5_TIMES = 0;
	private static final long EXPIRED_TIME = 0;
	
	/**
	 * 用于判断用户请求是否是ajax请求
	 * @param request
	 * @return
	 */
	public static boolean isAjax(HttpServletRequest request) {
        boolean ajax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        String ajaxFlag = null == request.getParameter("ajax") ? "false" : request.getParameter("ajax");
        boolean isAjax = ajax || ajaxFlag.equalsIgnoreCase("true");
        return isAjax;
    }
	
	/**
	 * 用于判断用户请求的接收资源类型是否是json数据
	 * @param request
	 * @return
	 */
	public static boolean acceptJSON(HttpServletRequest request) {
		String accept = request.getHeader("Accept");
		return accept.contains("json");
	}
	
	/**
	 * 请求非法，不符合规则
	 * token = md5(uri + timestmap + solt) * 次数
	 * @param request
	 * @return
	 */
	public static boolean isInvalid(HttpServletRequest request) {
		String token = request.getHeader("golden_token");
		//String timeStamp = request.getHeader("golden_timeStamp");
		
		// 根路径开始的uri
		//String uri = request.getRequestURI();
		/*String encryptURI = EncryptUtil.MD5Encrypt(uri + timeStamp,
				MD5_SOLT, URL_MD5_TIMES);*/
		
		return token == null || token.trim().isEmpty()/* || !token.equals(encryptURI)*/;
	}
	
	/**
	 * 请求过期
	 * @param request  
	 * @param curTime  接收到请求的时间
	 * @return
	 */
	public static boolean isExpired(HttpServletRequest request, long curTime) {
		String timeStamp = request.getHeader("golden_timeStamp");
		long requestTime = Long.parseLong(timeStamp);
		return curTime - requestTime > EXPIRED_TIME;
	}
	
	/**
	 * 校验请求验证码
	 * @param request
	 * @return
	 */
	public static boolean validateCode(HttpServletRequest request) {
		String clientVerifyCode = request.getParameter("");
		HttpSession session = request.getSession();
		String serverVerifyCode = (String) session.getAttribute("");
		
		return clientVerifyCode != null 
				&& !clientVerifyCode.trim().isEmpty() 
				&& clientVerifyCode.equals(serverVerifyCode);
	}
	
	/**
	 * 会话是否有用户已经登录
	 * @param request
	 * @return true 已经登录  false 没有登录
	 */
	/*public static boolean hasLogin(HttpServletRequest request) {
		Admin admin = (Admin) request.getSession().getAttribute(SysConstants.CURRENT_LOGIN_USER);
		return admin != null && LoginAdmin.contains(admin.getId());
	}*/
}
